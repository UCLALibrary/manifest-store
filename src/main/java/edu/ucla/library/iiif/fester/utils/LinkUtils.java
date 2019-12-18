
package edu.ucla.library.iiif.fester.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import info.freelibrary.util.StringUtils;

import edu.ucla.library.iiif.fester.CSV;
import edu.ucla.library.iiif.fester.ObjectType;

/**
 * A utility class for link generation.
 */
public final class LinkUtils {

    private static final String WORK_MANIFEST_URL = "{}/{}/manifest";

    private static final String COLLECTION_DOC_URL = "{}/collections/{}";

    private LinkUtils() {
    }

    /**
     * Adds manifest links to a supplied CSV.
     *
     * @param aHostURL The Fester host URL
     * @param aCsvList The CSV data structure
     * @return A modified CSV data structure
     */
    public static List<String[]> addManifests(final String aHostURL, final List<String[]> aCsvList) {
        Objects.requireNonNull(aHostURL);
        Objects.requireNonNull(aCsvList);

        final int objectTypeHeaderIndex = getColumnIndex(CSV.OBJECT_TYPE, aCsvList);
        final int manifestHeaderIndex = getColumnIndex(CSV.MANIFEST_URL, aCsvList);
        final int itemArkHeaderIndex = getColumnIndex(CSV.ITEM_ARK, aCsvList);
        final int columnLength = aCsvList.get(0).length;
        final List<String[]> csvList;

        if (manifestHeaderIndex == columnLength) {
            final Iterator<String[]> iterator = aCsvList.iterator();
            final int newArraySize = columnLength + 1;

            csvList = new ArrayList<>(aCsvList.size());

            // Populate our CSV list with larger String arrays so we can add the new column
            while (iterator.hasNext()) {
                csvList.add(Arrays.copyOf(iterator.next(), newArraySize));
            }
        } else {
            csvList = aCsvList;
        }

        for (int index = 0; index < csvList.size(); index++) {
            final String[] row = csvList.get(index);

            if (index == 0) {
                row[manifestHeaderIndex] = CSV.MANIFEST_URL;
            } else {
                final String objectType = row[objectTypeHeaderIndex];
                final String itemARK = URLEncoder.encode(row[itemArkHeaderIndex], StandardCharsets.UTF_8);

                // URLs vary depending on whether the row is a Collection or Work
                if (ObjectType.COLLECTION.equals(objectType)) {
                    row[manifestHeaderIndex] = StringUtils.format(COLLECTION_DOC_URL, aHostURL, itemARK);
                } else if (ObjectType.WORK.equals(objectType)) {
                    row[manifestHeaderIndex] = StringUtils.format(WORK_MANIFEST_URL, aHostURL, itemARK);
                } else {
                    row[manifestHeaderIndex] = ""; // Use an empty placeholder for things without links
                }
            }
        }

        return csvList;
    }

    private static int getColumnIndex(final String aHeader, final List<String[]> aCsvList) {
        final String[] headers = aCsvList.get(0);

        for (int index = 0; index < headers.length; index++) {
            if (aHeader.equalsIgnoreCase(headers[index])) {
                return index;
            }
        }

        // If we don't find it, we'll put it after the last array slot
        return headers.length;
    }
}
