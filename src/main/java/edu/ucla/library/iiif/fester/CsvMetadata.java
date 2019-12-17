
package edu.ucla.library.iiif.fester;

import java.util.List;
import java.util.Map;

import info.freelibrary.iiif.presentation.Collection;

/**
 * Processed metadata from a supplied CSV file.
 */
public class CsvMetadata {

    private final Map<String, List<Collection.Manifest>> myWorksMap;

    private final List<String[]> myWorksList;

    private final Map<String, List<String[]>> myPagesMap;

    /**
     * Creates a CSV metadata object.
     *
     * @param aWorksMap A map of metadata for works stored in a collection document
     * @param aWorksList A list of works metadata
     * @param aPagesMap A map of pages metadata
     */
    public CsvMetadata(final Map<String, List<Collection.Manifest>> aWorksMap, final List<String[]> aWorksList,
            final Map<String, List<String[]>> aPagesMap) {
        myWorksMap = aWorksMap;
        myWorksList = aWorksList;
        myPagesMap = aPagesMap;
    }

    /**
     * Gets the metadata for the works stored in a collection document.
     *
     * @return The metadata for the works stored in a collection document
     */
    public Map<String, List<Collection.Manifest>> getWorksMap() {
        return myWorksMap;
    }

    /**
     * Gets the metadata for the works manifests.
     *
     * @return The metadata for the works manifests
     */
    public List<String[]> getWorksList() {
        return myWorksList;
    }

    /**
     * Gets the metadata for the manifest pages.
     *
     * @return The metadata for the manifest pages
     */
    public Map<String, List<String[]>> getPagesMap() {
        return myPagesMap;
    }

}
