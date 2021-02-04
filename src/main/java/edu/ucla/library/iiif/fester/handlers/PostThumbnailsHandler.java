
package edu.ucla.library.iiif.fester.handlers;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import info.freelibrary.util.IOUtils;
import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.StringUtils;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucla.library.iiif.fester.Config;
import edu.ucla.library.iiif.fester.Constants;
import edu.ucla.library.iiif.fester.CsvParser;
import edu.ucla.library.iiif.fester.CsvParsingException;
import edu.ucla.library.iiif.fester.CSV;
import edu.ucla.library.iiif.fester.HTTP;
import edu.ucla.library.iiif.fester.MessageCodes;
import edu.ucla.library.iiif.fester.ObjectType;
import edu.ucla.library.iiif.fester.utils.ThumbnailUtils;

/**
 * Class to select and add thumbnail link to uploaded CSV
*/
public class PostThumbnailsHandler extends AbstractFesterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostThumbnailsHandler.class, Constants.MESSAGES);

    private static final String MANIFESTS = "manifests";

    private static final String ATTACHMENT = "attachment; filename=\"{}\"";

    private static final String BR_TAG = "<br>";

    private static final String V2_ID_TAG = "@id";

    private static final String V3_ID_TAG = "id";

    private static final String API_2 = "presentation/2";

    private static final String API_3 = "presentation/3";

    private final String myExceptionPage;

    private final String myFesterizeVersion;

    /**
     * FUA = Festerize User Agent
     */
    private final Pattern myFUAPattern;

    /**
     * @param aVertx
     * @param aConfig
    */
    public PostThumbnailsHandler(final Vertx aVertx, final JsonObject aConfig) throws IOException {
        super(aVertx, aConfig);

        final byte[] bytes = IOUtils.readBytes(getClass().getResourceAsStream("/webroot/error.html"));

        myExceptionPage = new String(bytes, StandardCharsets.UTF_8);
        myFesterizeVersion = aConfig.getString(Config.FESTERIZE_VERSION);
        myFUAPattern = Pattern.compile("Festerize/(?<version>\\d+\\.\\d+\\.\\d+)");
    }

    /*
     * (non-Javadoc)
     * @see io.vertx.core.Handler#handle(java.lang.Object)
     */
    @Override
    public void handle(final RoutingContext aContext) {
        final HttpServerRequest request = aContext.request();
        final HttpServerResponse response = aContext.response();
        final Set<FileUpload> csvUploads = aContext.fileUploads();
        final String festerizeUserAgent = StringUtils.trimTo(request.getHeader("User-Agent"), Constants.EMPTY);
        final Matcher festerizeUserAgentMatcher = myFUAPattern.matcher(festerizeUserAgent);
        final String errorMessage;

        // An uploaded CSV is required
        if (csvUploads.isEmpty()) {
            errorMessage = LOGGER.getMessage(MessageCodes.MFS_037);
            returnError(response, HTTP.BAD_REQUEST, errorMessage);
        } else if (festerizeUserAgentMatcher.matches() &&
                   !festerizeUserAgentMatcher.group("version").equals(myFesterizeVersion)) {
            errorMessage = LOGGER.getMessage(MessageCodes.MFS_147, myFesterizeVersion);
            returnError(response, HTTP.BAD_REQUEST, errorMessage);
        } else {
            final FileUpload csvFile = csvUploads.iterator().next();
            final String filePath = csvFile.uploadedFileName();
            final String fileName = csvFile.fileName();

            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)))) {
                final CsvParser parser = new CsvParser().parse(Paths.get(filePath));
                final List<String[]> originalLines = csvReader.readAll();
                final List<String[]> linesWithThumbs = ThumbnailUtils.addThumbnailColumn(originalLines);
                final int manifestIndex = Arrays.asList(linesWithThumbs.get(0)).indexOf(CSV.MANIFEST_URL);
		/*
		 * loop through lineswiththumbs
		 * check row type vua CsvParser.getObjectType(row, parser.getCsvHeaders())
		 * if row is work or page, get the manifest URL and retrieve manifest
		 * determine manifest api version
		 * retrieve array of canvases
		 * select image, insert base URL into row
		 */
                for (int index = 1; index < linesWithThumbs.size(); index++) {
                    final String[] row = linesWithThumbs.get(index);
                    final ObjectType rowType = CsvParser.getObjectType(row, parser.getCsvHeaders());
                    if (rowType.equals(ObjectType.WORK) || rowType.equals(ObjectType.PAGE)) {
                        final String manifestURL = row[manifestIndex];
                        final Future<JsonObject> manifest = getManifest(manifestURL);
                        manifest.onComplete(result -> {
                            if (result.failed()) {
                                returnError(response, HTTP.INTERNAL_SERVER_ERROR, result.cause().getMessage());
                            } else {
                                final JsonObject manifestBody = result.result();
                                final String context = manifestBody.getString("@context");
                                final int thumbIndex = ThumbnailUtils.findThumbHeaderIndex(linesWithThumbs.get(0));
                                if (context.contains(API_2)) {
                                    addV2Thumb(thumbIndex, manifestBody, row);
                                } else if (context.contains(API_3)) {
                                    addV3Thumb(thumbIndex, manifestBody, row);
                                } else {
                                    LOGGER.info("message about unknown API version");
                                }
                            }
                        });
                    }
                }
                final String collectionURL = linesWithThumbs.get(1)[manifestIndex];
                final Future<JsonObject> collection = getManifest(collectionURL);
                collection.onComplete(result -> {
                    if (result.failed()) {
                        returnError(response, HTTP.INTERNAL_SERVER_ERROR, result.cause().getMessage());
                    } else {
                        final JsonArray canvases = result.result().getJsonArray(MANIFESTS);
                        final int canvasCount = canvases.size();
                        final int canvasIndex;
                        if (canvasCount <= 3) {
                            canvasIndex = 0;
                        } else {
                            canvasIndex = ThumbnailUtils.pickThumbnailIndex(canvasCount - 1);
                        }
                        final String canvasURL = canvases.getJsonObject(canvasIndex).getString(V2_ID_TAG);
                        final Future<JsonObject> canvas = getManifest(canvasURL);
                        canvas.onComplete(canvasResult -> {
                            if (canvasResult.failed()) {
                                returnError(response, HTTP.INTERNAL_SERVER_ERROR, canvasResult.cause().getMessage());
                            } else {
                                final JsonObject thumbCanvas = canvasResult.result();
                                final String thumbURL = thumbCanvas.getJsonArray("sequences").getJsonObject(0)
                                      .getJsonArray("canvases").getJsonObject(0).getJsonArray("images")
                                      .getJsonObject(0).getJsonObject("resource").getString(V2_ID_TAG);
                                //ThumbnailUtils.addThumbnailURL(linesWithThumbs, thumbURL);
                                returnCSV(fileName, filePath, linesWithThumbs, response);
                            }
                        });
                    }
                });
            } catch (CsvException | IOException | CsvParsingException details) {
                logError(details);
                returnError(response, HTTP.INTERNAL_SERVER_ERROR, details.getMessage());
            }

        }
    }

    private Future<JsonObject> getManifest(final String aUrl) {
        final Promise<JsonObject> promise = Promise.promise();
        final HttpRequest<JsonObject> request;
        request = WebClient.create(myVertx)
            .getAbs(aUrl)
            .putHeader("Accept", "application/json")
            .as(BodyCodec.jsonObject())
            .expect(ResponsePredicate.SC_OK);

        if (aUrl.startsWith("https")) {
            request.ssl(true);
        }

        request.send(asyncResult -> {
            if (asyncResult.succeeded()) {
                promise.complete(asyncResult.result().body());
            } else {
                promise.fail(asyncResult.result().statusMessage());
            }
        });

        return promise.future();
    }

    private void addV2Thumb(final int aIndex, final JsonObject aManifest, final String... aRow) {
        final JsonArray canvases = aManifest.getJsonArray("sequences").getJsonObject(0).getJsonArray("canvases");
        final int canvasIndex = chooseThumbIndex(canvases.size());
        final String thumbURL = canvases.getJsonObject(canvasIndex).getJsonObject("images")
                                .getJsonArray(0).getJsonObject("service").getString(V2_ID_TAG);
        ThumbnailUtils.addThumbnailURL(aIndex, thumbURL, aRow);
    }

    private void addV3Thumb(final int aIndex, final JsonObject aManifest, final String... aRow) {
        final String thumbURL = null;
        ThumbnailUtils.addThumbnailURL(aIndex, thumbURL, aRow);
    }

    private int chooseThumbIndex(final int aCount) {
        if (aCount <= 3) {
            return 0;
        } else {
            return ThumbnailUtils.pickThumbnailIndex(aCount - 1);
        }
    }

    private void returnCSV(final String aFileName, final String aFilePath, final List<String[]> aCsvList,
                           final HttpServerResponse aResponse) {
        final StringWriter writer = new StringWriter();
        final String responseMessage = LOGGER.getMessage(MessageCodes.MFS_038, aFileName, aFilePath);
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeAll(aCsvList);

            aResponse.setStatusCode(HTTP.OK);
            aResponse.setStatusMessage(responseMessage);
            aResponse.putHeader(Constants.CONTENT_TYPE, Constants.CSV_MEDIA_TYPE);
            aResponse.putHeader(Constants.CONTENT_DISPOSITION, StringUtils.format(ATTACHMENT, aFileName));
            aResponse.end(Buffer.buffer(writer.toString()));
        } catch (final IOException details) {
            logError(details);
            returnError(aResponse, HTTP.INTERNAL_SERVER_ERROR, details.getMessage());
        }
    }

    private void logError(final Throwable aThrowable) {
        LOGGER.error(aThrowable, LOGGER.getMessage(MessageCodes.MFS_166, aThrowable.getMessage()));
    }

    private void returnError(final HttpServerResponse aResponse, final int aStatusCode, final String aError) {
        final String body = LOGGER.getMessage(MessageCodes.MFS_166, aError.replaceAll(Constants.EOL_REGEX, BR_TAG));

        aResponse.setStatusCode(aStatusCode);
        aResponse.setStatusMessage(aError.replaceAll(Constants.EOL_REGEX, Constants.EMPTY));
        aResponse.putHeader(Constants.CONTENT_TYPE, Constants.HTML_MEDIA_TYPE);
        aResponse.end(StringUtils.format(myExceptionPage, body));
    }
}
