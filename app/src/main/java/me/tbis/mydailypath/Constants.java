package me.tbis.mydailypath;

/**
 * Created by Zhognze Tang on 2017/11/6.
 *
 */

final class Constants {
    static final int SUCCESS_RESULT = 0;

    static final int FAILURE_RESULT = 1;

    static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final String PACKAGE_NAME = "me.tbis.mydailypath";

    static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    static final String LOCATION_DATA_LONGITUDE = PACKAGE_NAME + ".LOCATION_DATA_LONGITUDE";

    static final String LOCATION_DATA_LATITUDE = PACKAGE_NAME + ".LOCATION_DATA_LATITUDE";

    static final String ACTION_UPDATEUI = PACKAGE_NAME + ".UPDATE_UI";
}
