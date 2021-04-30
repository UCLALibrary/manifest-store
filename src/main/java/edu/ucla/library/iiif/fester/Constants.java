
package edu.ucla.library.iiif.fester;

/**
 * A class for package constants.
 */
public final class Constants {

    /**
     * ResourceBundle file name for I18n messages.
     */
    public static final String MESSAGES = "fester_messages";

    /**
     * The IP for an unspecified host.
     */
    public static final String UNSPECIFIED_HOST = "0.0.0.0";

    /**
     * The default S3 endpoint.
     */
    public static final String S3_ENDPOINT = "https://s3.amazonaws.com";

    /**
     * The message header key associated with the Fester operation (HTTP request) that caused the message send.
     */
    public static final String ACTION = "action";

    /**
     * The message body key associated with the Fester operation input data (HTTP request body) that caused the message
     * send.
     */
    public static final String DATA = "data";

    /**
     * The name of the manifest ID parameter.
     */
    public static final String MANIFEST_ID = "manifestId";

    /**
     * The name of the collection name parameter.
     */
    public static final String COLLECTION_NAME = "collectionName";

    /**
     * The content-type header key.
     */
    public static final String CONTENT_TYPE = "content-type";

    /**
     * The content-disposition of the reponse.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * The media type for JSON (the format of IIIF manifests).
     */
    public static final String JSON_MEDIA_TYPE = "application/json";

    /**
     * The media type for HTML.
     */
    public static final String HTML_MEDIA_TYPE = "text/html";

    /**
     * The media type for CSV.
     */
    public static final String CSV_MEDIA_TYPE = "text/csv";

    /**
     * The plain text content-type.
     */
    public static final String PLAIN_TEXT_TYPE = "text/plain";

    /**
     * COR header for allowing access to our manifests to the world.
     */
    public static final String CORS_HEADER = "Access-Control-Allow-Origin";

    /**
     * An asterisk/star for use as a wildcard.
     */
    public static final String STAR = "*";

    /**
     * A slash for constructing URL paths.
     */
    public static final String SLASH = "/";

    /**
     * A dot.
     */
    public static final String DOT = ".";

    /**
     * Just a empty string, useful.
     */
    public static final String EMPTY = "";

    /**
     * A delimiter for use in string parsing.
     */
    public static final String COMMA = ",";

    /**
     * A regular expression representing end of line character(s).
     */
    public static final String EOL_REGEX = "\\r|\\n|\\r\\n";

    /**
     * The file extension for JSON files.
     */
    public static final String JSON_EXT = "json";

    /**
     * The file path of the CSV collection file being processed.
     */
    public static final String CSV_FILE_PATH = "csv-file-path";

    /**
     * The name of the CSV collection file being processed.
     */
    public static final String CSV_FILE_NAME = "csv-file-name";

    /**
     * The name of the IIIF host parameter.
     */
    public static final String IIIF_HOST = "iiif-host";

    /**
     * The name of the optional property indicating the load is only a metadata update. Its value, if present, is
     * <code>true</code> or <code>false</code>.
     */
    public static final String METADATA_UPDATE = "metadata-update";

    /**
     * The name of the IIIF presentation version parameter.
     */
    public static final String IIIF_API_VERSION = "iiif-version";

    /**
     * Used as a message header when the sender wants the manifest returned from S3 without resource URLs re-written.
     */
    public static final String NO_REWRITE_URLS = "no-rewrite-urls";

    /**
     * A unique random placeholder URL that prefixes all IIIF Presentation API resource URLs in all manifests at rest in
     * S3. It gets replaced with Constants.URL on each GET request.
     */
    public static final String URL_PLACEHOLDER = "http://b1dbe4a0-443c-479f-bf0a-25c352df0d8f.iiif.library.ucla.edu";

    /**
     * The HTTP POST route for CSV uploads.
     */
    public static final String POST_CSV_ROUTE = "/collections";

    /**
     * The HTTP POST route for CSV thumbnail uploads.
     */
    public static final String POST_THUMB_ROUTE = "/thumbnails";

    /**
     * The host at which we're serving content.
     */
    public static final String FESTER_HOST = "fester-host";

    /**
     * The suffix for work manifest URI paths.
     */
    public static final String MANIFEST_URI_PATH_SUFFIX = "/manifest";

    /**
     * The prefix for work manifest S3 keys.
     */
    public static final String WORK_S3_KEY_PREFIX = "works/";

    /**
     * The prefix for collection manifest S3 keys.
     */
    public static final String COLLECTION_S3_KEY_PREFIX = "collections/";

    /**
     * The prefix for collection manifest URI paths.
     */
    public static final String COLLECTION_URI_PATH_PREFIX = SLASH + COLLECTION_S3_KEY_PREFIX;

    /**
     * The record of completed S3 uploads.
     */
    public static final String RESULTS_MAP = "s3-uploads";

    /**
     * A name for wait counters.
     */
    public static final String WAIT_COUNT = "wait-count";

    /**
     * A name for the S3 request counter.
     */
    public static final String S3_REQUEST_COUNT = "s3-request-count";

    /**
     * Manifest content, stored as a JSON object.
     */
    public static final String MANIFEST_CONTENT = "manifest-content";

    /**
     * Collection content, stored as a JSON object.
     */
    public static final String COLLECTION_CONTENT = "collection-content";

    /**
     * Updates to manifest or collection content, stored as a string array.
     */
    public static final String UPDATED_CONTENT = "updated-content";

    /**
     * Manifest pages, stored in list form.
     */
    public static final String MANIFEST_PAGES = "manifest-pages";

    /**
     * The placeholder image property name.
     */
    public static final String PLACEHOLDER_IMAGE = "placeholder-image";

    /**
     * CSV headers, stored as a JSON object.
     */
    public static final String CSV_HEADERS = "csv-headers";

    /**
     * The POST parameter for uploading a CSV file.
     */
    public static final String CSV_FILE = "csv-file";

    /**
     * The name of the mapping of deployed verticles.
     */
    public static final String VERTICLE_MAP = "fester-verticles";

    /**
     * The collection type of resource.
     */
    public static final String COLLECTION = "collection";

    /**
     * The manifest type of resource.
     */
    public static final String MANIFEST = "work";

    /**
     * Version 2 of the IIIF presentation API.
     */
    public static final String IIIF_API_V2 = "v2";

    /**
     * Version 3 of the IIIF presentation API.
     */
    public static final String IIIF_API_V3 = "v3";

    /**
     * The string template of default sample URIs.
     */
    public static final String SAMPLE_URI_TEMPLATE = "{}/full/{},/0/default.jpg";

    /**
     * The default size of sample images.
     */
    public static final int DEFAULT_SAMPLE_SIZE = 600;

    /**
     * The id property of a v2 IIIF resource.
     */
    public static final String ID_V2 = "@id";

    /**
     * The id property of a v3 IIIF resource.
     */
    public static final String ID_V3 = "id";

    /**
     * The context property of a v2 IIIF resource.
     */
    public static final String CONTEXT_V2 = "http://iiif.io/api/presentation/2/context.json";

    /**
     * The context property of a v3 IIIF resource.
     */
    public static final String CONTEXT_V3 = "http://iiif.io/api/presentation/3/context.json";

    /**
     * The default pattern for A/V URLs. .
     */
    public static final String DEFAULT_AV_STRING = "mp4";

    /**
     * Private constructor for Constants class.
     */
    private Constants() {
    }

}
