package com.github.dozzatq.blaze.BlazeDownloader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.github.dozzatq.blaze.BlazeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by RondailP on 10.10.2016.
 */
public class BlazeDownloader
{
    private String downloadUrl;
    private String filename;
    private List<OnProgressListener> progressListenerList = new ArrayList<>();
    private List<OnSuccessListener> successListenerList = new ArrayList<>();
    private List<OnFailureListener> failureListenersList = new ArrayList<>();
    private List<BroadcastReceiver> calledReceiverList = new ArrayList<>();
    private Boolean needNotification=false;
    private PendingIntent notificationIntent;
    private String nameReceiver;
    private int notificationId;
    private Integer notificationIcon;
    private String notificationTitle;
    private String notificationText;
    private String downloadedText;
    private String startNotificationText;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder builderNotification;

    private BlazeDownloader(String downloadUrl, String filename) {
        this.downloadUrl = downloadUrl;
        if (downloadUrl == null)
            throw new NullPointerException("BlazeDownloader download URL must not be null;");
        this.filename = filename;
        mNotifyManager =
                (NotificationManager) BlazeContext.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private boolean indeterminateBinded = false;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BlazeDownloaderConfig.PARAM_STATUS,0);
            switch (status)
            {
                case BlazeDownloaderConfig.STATUS_PRE_DOWNLOADING:
                    if (needNotification)
                    {
                        builderNotification = builderNotification
                                .setContentText(getStartNotificationText())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getStartNotificationText()));
                        mNotifyManager.notify(notificationId, builderNotification.build());
                    }
                    break;
                case BlazeDownloaderConfig.STASUS_DOWNLOADING:
                    int progress = intent.getIntExtra(BlazeDownloaderConfig.PARAM_PROGRESS_PERCENTAGE, 0);
                    for (OnProgressListener listener : progressListenerList) {
                        listener.OnProgress(progress);
                    }
                    if (needNotification && !indeterminateBinded)
                    {
                        if (progress!=-1)
                            builderNotification.setProgress(100, progress, false);
                        else {
                            builderNotification.setProgress(0, 0, true);
                            indeterminateBinded =true;
                        }
                        builderNotification
                                .setContentText(getNotificationText())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getDownloadedText()));
                        mNotifyManager.notify(notificationId, builderNotification.build());
                    }
                    break;
                case BlazeDownloaderConfig.STATUS_DOWNLOADED:

                    String downloadUrl = intent.getStringExtra(BlazeDownloaderConfig.PARAM_URL);
                    Integer receivedBytes = intent.getIntExtra(BlazeDownloaderConfig.PARAM_RECEIVED_BYTES, 0);
                    Integer totalBytes = intent.getIntExtra(BlazeDownloaderConfig.PARAM_TOTAL_BYTES, 0);
                    String fileName = intent.getStringExtra(BlazeDownloaderConfig.PARAM_FILENAME);

                    if (needNotification)
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                builderNotification = builderNotification.setContentIntent(notificationIntent)
                                        .setProgress(0,0, false)
                                        .setAutoCancel(true)
                                        .setOngoing(false)
                                        .setSound(alarmSound)
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(getDownloadedText()))
                                        .setContentText(getDownloadedText());
                                mNotifyManager.notify(notificationId, builderNotification.build());
                            }
                        }, 60);
                    }

                    for (OnSuccessListener successListener : successListenerList) {
                        successListener.OnSuccess(new DownloadTask(fileName, downloadUrl, receivedBytes, totalBytes));
                    }
                    break;
                case BlazeDownloaderConfig.STATUS_FAILED:
                    String exception = intent.getStringExtra(BlazeDownloaderConfig.PARAM_PROGRESS_EXCEPTION);
                    for (OnFailureListener listener : failureListenersList) {
                        listener.OnFailure(new Exception(exception));
                    }
                    if (needNotification)
                    {
                        mNotifyManager.cancel(notificationId);
                    }
                    break;
            }
        }
    };

    public void getDownload()
    {
        Intent intent = new Intent(BlazeContext.getInstance().getApplicationContext(), BlazeDownloaderService.class);
        intent.putExtra(BlazeDownloaderConfig.PARAM_URL, downloadUrl);
        indeterminateBinded = false;
        if (nameReceiver==null)
            nameReceiver = String.format(Locale.US, BlazeDownloaderConfig.SEND_PARAMS_FORMAT, System.currentTimeMillis());
        intent.putExtra(BlazeDownloaderConfig.PARAM_RECEIVER, nameReceiver);
        intent.putExtra(BlazeDownloaderConfig.PARAM_FILENAME, filename);
        if (needNotification)
        {
            builderNotification = new NotificationCompat.Builder(BlazeContext.getInstance().getApplicationContext());
            builderNotification = builderNotification.setAutoCancel(false).setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setOngoing(true)
                    .setSmallIcon(notificationIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText));
        }
        calledReceiverList.add(broadcastReceiver);
        LocalBroadcastManager.getInstance(BlazeContext.getInstance().getApplicationContext())
                .registerReceiver(broadcastReceiver, new IntentFilter(nameReceiver));

        BlazeContext.getInstance().getApplicationContext().startService(intent);
    }

    public static BlazeDownloader getReferenceFromUrl(String url)
    {
        return new BlazeDownloader(url, null);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public static BlazeDownloader getReference(@NonNull String downloadUrl, @NonNull String filename)
    {
        return new BlazeDownloader(downloadUrl,filename);
    }

    public BlazeDownloader addSuccessListener(@NonNull OnSuccessListener successListener)
    {
        successListenerList.add(successListener);
        return this;
    }

    public BlazeDownloader addProgressListener(@NonNull OnProgressListener progressListener)
    {
        progressListenerList.add(progressListener);
        return this;
    }

    public BlazeDownloader addFailureListener(@NonNull OnFailureListener failureListener)
    {
        failureListenersList.add(failureListener);
        return this;
    }

    public BlazeDownloader setNeedNotification(Boolean needNotification) {
        this.needNotification = needNotification;
        return this;
    }

    public Boolean getNeedNotification() {
        return needNotification;
    }

    public int getNotificationId() {
        if (notificationId==0)
            return 532346;
        return notificationId;
    }

    public BlazeDownloader setNotificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public PendingIntent getNotificationIntent() {
        return notificationIntent;
    }

    public BlazeDownloader setNotificationIntent(PendingIntent notificationIntent) {
        this.notificationIntent = notificationIntent;
        return this;
    }

    public Integer getNotificationIcon() {
        return notificationIcon;
    }

    public BlazeDownloader setNotificationIcon(Integer notificationIcon) {
        this.notificationIcon = notificationIcon;
        return this;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public BlazeDownloader setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
        return this;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public BlazeDownloader setNotificationText(String notificationText) {
        this.notificationText = notificationText;
        return this;
    }

    public String getDownloadedText() {
        if (downloadedText!=null)
            return downloadedText;
        else return notificationText;
    }

    public BlazeDownloader setDownloadedText(String downloadedText) {
        this.downloadedText = downloadedText;
        return this;
    }

    public String getStartNotificationText() {
        if (startNotificationText!=null)
            return startNotificationText;
        else return notificationText;
    }

    public BlazeDownloader setStartNotificationText(String startNotificationText) {
        this.startNotificationText = startNotificationText;
        return this;
    }

    public interface OnProgressListener{
        /* Percent progress */
        public void OnProgress(Integer progress);
    }

    public interface OnSuccessListener{
        public void OnSuccess(DownloadTask downloadTask);
    }

    public interface OnFailureListener{
        public void OnFailure(Exception e);
    }

    public class DownloadTask{
        private String downloadedFile;
        private String downloadUrl;
        private Integer receivedBytes;
        private Integer totalSize;

        public DownloadTask(String downloadedFile, String downloadUrl, Integer receivedBytes, Integer totalSize) {
            this.downloadedFile = downloadedFile;
            this.downloadUrl = downloadUrl;
            this.receivedBytes = receivedBytes;
            this.totalSize = totalSize;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public String getDownloadedFile() {
            return downloadedFile;
        }

        public Integer getReceivedBytes() {
            return receivedBytes;
        }

        public Integer getTotalSize() {
            return totalSize;
        }
    }
}
