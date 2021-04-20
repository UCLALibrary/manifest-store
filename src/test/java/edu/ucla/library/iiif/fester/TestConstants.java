
package edu.ucla.library.iiif.fester;

/**
 * Constants useful to testing Fester.
 */
public final class TestConstants {

    /**
     * ID Prefix to use for all PUT tests.
     */
    public static final String PUT_TEST_ID_PREFIX = "PUT_";

    /**
     * The port variable for testing.
     */
    public static final String HTTP_PORT = "http.port";

    /**
     * The name of the Fester container image.
     */
    public static final String CONTAINER_IMAGE = "fester.container.tag";

    /**
     * The IP for an unspecified host.
     */
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public static final String LOCALHOST = "localhost";

    /**
     * Creates a new TestConstants object.
     */
    private TestConstants() {
    }
}
