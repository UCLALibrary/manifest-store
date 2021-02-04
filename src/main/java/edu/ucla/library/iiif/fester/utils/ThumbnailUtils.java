
package edu.ucla.library.iiif.fester.utils;

import edu.ucla.library.iiif.fester.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility methods for thumbnail selection.
*/
public final class ThumbnailUtils {
    private static final String HEADER_THUMB = "thumbnail";

    private ThumbnailUtils() {
    }

   /**
     * Adds a "thumbnail" column to a CSV if needed..
     *
     * @param aCsvList A CSV parsed into a list of arrays
     * @return The CSV with added "thumbnail" column
   */
    public static List<String[]> addThumbnailColumn(final List<String[]> aCsvList) {
        Objects.requireNonNull(aCsvList);
        final List<String[]> csvList;

        if (hasThumbnailColumn(aCsvList.get(0))) {
            return aCsvList;
        } else {
            final int newArraySize = aCsvList.get(0).length + 1;
            csvList = new ArrayList<>(aCsvList.size());
            aCsvList.stream().forEach(entry -> csvList.add(Arrays.copyOf(entry, newArraySize)));
            csvList.get(0)[newArraySize - 1] = HEADER_THUMB;
            return csvList;
        }
    }

   /**
     * Adds a base IIF URL for the thumnail image.
     *
     * @param aIndex Index in CSV row where thumbnail URL will be added
     * @param aURL The thumbnail URL
     * @param aRow A row from a CSV in string array format
   */
    public static void addThumbnailURL(final int aIndex, final String aURL, final String... aRow) {
        Objects.requireNonNull(aURL);
        Objects.requireNonNull(aRow);
        if (aRow[aIndex] == null || aRow[aIndex].trim().equals(Constants.EMPTY)) {
            aRow[aIndex] = aURL;
        }
        /*for (int index = 1; index < aCsvList.size(); index++ ) {
            if (aCsvList.get(index)[thumbnailIndex] == null ||
                aCsvList.get(index)[thumbnailIndex].trim().equals(Constants.EMPTY) ) {
                aCsvList.get(index)[thumbnailIndex] = aURL;
            }
        }*/
    }

   /**
     * Return the index of the thumbnail column in a CSV header row.
     *
     * @param aRow Header row from a CSV
     * @return Index of the thumbnail header
   */
    public static int findThumbHeaderIndex(final String... aRow) {
        return Arrays.asList(aRow).indexOf(HEADER_THUMB);
    }

   /**
     * Chooses the index for a randomly-selected thumbnail.
     *
     * @param aMax Maximum value in selection range
     * @return Random number between 2 (index position 3) and aMax
   */
    public static int pickThumbnailIndex(final int aMax) {
        final int min = 2;
        return ThreadLocalRandom.current().nextInt(min, aMax + 1);
    }

   /**
     * Determines whether a CSV has a "Thumbnail" column.
     *
     * @param aHeaderRow The header row from a CSV
     * @return Yes/no for "thumbnail" column existence
   */
    private static boolean hasThumbnailColumn(final String... aHeaderRow) {
        return Arrays.stream(aHeaderRow).anyMatch(HEADER_THUMB::equals);
    }

}
