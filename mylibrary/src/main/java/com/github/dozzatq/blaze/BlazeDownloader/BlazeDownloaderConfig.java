package com.github.dozzatq.blaze.BlazeDownloader;

/**
 * Created by RondailP on 10.10.2016.
 */
public class BlazeDownloaderConfig {
    public final static String PARAM_FILENAME = ":BlazeDownloader:FILENAME";
    public final static String PARAM_URL = ":BlazeDownloader:URL";
    public final static String PARAM_RECEIVER = ":BlazeDownloader:RECEIVER";
    public final static String PARAM_TOTAL_BYTES = ":BlazeDownloader:TOTALBYTES";
    public final static String PARAM_RECEIVED_BYTES = ":BlazeDownloader:RECEIVED";
    public final static String SEND_PARAMS_FORMAT = ".BlazeDownloader:SEND_ID:%d";

    public final static String PARAM_STATUS = ".BlazeDownloaderService:STATUS";

    public final static String PARAM_PROGRESS_PERCENTAGE = ".BlazeDownloaderService:PERCENTAGE";

    public final static String PARAM_PROGRESS_EXCEPTION = ".BlazeDownloaderService:EXCEPTION";

    public final static String PARAM_DOWNLOAD_RESULT = ".BlazeDownloaderService:RESULT";

    public final static int STATUS_PRE_DOWNLOADING = 4;
    public final static int STASUS_DOWNLOADING = 1;
    public final static int STATUS_FAILED = 2;
    public final static int STATUS_DOWNLOADED = 3;
}
