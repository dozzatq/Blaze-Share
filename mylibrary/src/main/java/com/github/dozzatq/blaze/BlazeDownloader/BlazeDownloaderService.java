package com.github.dozzatq.blaze.BlazeDownloader;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by RondailP on 10.10.2016.
 */
public class BlazeDownloaderService extends Service {

    public BlazeDownloaderService()
    {
        super();
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            Bundle intentBundle = msg.getData();

            if (intentBundle==null)
                return;

            String filename = intentBundle.getString(BlazeDownloaderConfig.PARAM_FILENAME);
            String receiver = intentBundle.getString(BlazeDownloaderConfig.PARAM_RECEIVER);
            String downloadUrl = intentBundle.getString(BlazeDownloaderConfig.PARAM_URL);
            if (filename==null)
                filename = getCacheDir().getPath() + FireBasePushIdGenerator.generatePushId();

            if (downloadUrl==null)
                stopSelf(msg.arg1);

            BlazeDownloaderTask blazeDownloaderTask = new BlazeDownloaderTask(filename, receiver, downloadUrl);
            blazeDownloaderTask.execute();

        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();

        msg.arg1 = startId;
        msg.setData(intent.getExtras());

        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    private class BlazeDownloaderTask extends AsyncTask<Void, Integer, TaskResult> {

        private String filename;
        private String receiver;
        private String downloadUrl;
        private Integer receivedBytes;
        private Integer totalBytes;

        private BlazeDownloaderTask(String filename, String receiver, String downloadUrl) {
            this.filename = filename;
            this.receiver = receiver;
            this.downloadUrl = downloadUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent progressIntent = new Intent(receiver);
            progressIntent.putExtra(BlazeDownloaderConfig.PARAM_STATUS, BlazeDownloaderConfig.STATUS_PRE_DOWNLOADING);
            LocalBroadcastManager.getInstance(BlazeDownloaderService.this)
                    .sendBroadcast(progressIntent);
        }

        @Override
        protected TaskResult doInBackground(Void... params) {

            try {
                URL obj = new URL(downloadUrl);

                URLConnection connection = obj.openConnection();
                connection.connect();

                totalBytes = connection.getContentLength();
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());

                receivedBytes = 0;

                OutputStream outputStream = new FileOutputStream(filename);

                Integer percentage = 0;
                Integer oldPercentage = percentage;

                if (totalBytes==-1)
                    percentage = -1;

                byte data[] = new byte[1024];
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    receivedBytes += count;
                    if (percentage!=-1) {
                        Float floatPercent = ((Float) receivedBytes.floatValue() / totalBytes.floatValue() * 100.0f);
                        percentage = floatPercent.intValue();
                    }
                    else percentage = -1;
                    if (!percentage.equals(oldPercentage))
                    {
                        oldPercentage = percentage;
                        publishProgress(oldPercentage);
                    }
                    outputStream.write(data, 0, count);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return new TaskResult(e.getMessage(), false);
            } catch (IOException e) {
                e.printStackTrace();
                return new TaskResult(e.getMessage(), false);
            }

            return new TaskResult(null, true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Intent progressIntent = new Intent(receiver);
            progressIntent.putExtra(BlazeDownloaderConfig.PARAM_STATUS, BlazeDownloaderConfig.STASUS_DOWNLOADING);
            progressIntent.putExtra(BlazeDownloaderConfig.PARAM_PROGRESS_PERCENTAGE, values[0]);
            LocalBroadcastManager.getInstance(BlazeDownloaderService.this)
                    .sendBroadcast(progressIntent);
        }

        @Override
        protected void onPostExecute(TaskResult result) {
            super.onPostExecute(result);
            Intent resultIntent = new Intent(receiver);
            if (result.isResult())
            {
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_URL, downloadUrl);
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_RECEIVED_BYTES, receivedBytes);
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_TOTAL_BYTES, totalBytes);
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_FILENAME, filename);
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_STATUS,BlazeDownloaderConfig.STATUS_DOWNLOADED);
                LocalBroadcastManager.getInstance(BlazeDownloaderService.this)
                        .sendBroadcast(resultIntent);
            }
            else
            {
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_PROGRESS_EXCEPTION, result.getException());
                resultIntent.putExtra(BlazeDownloaderConfig.PARAM_STATUS,BlazeDownloaderConfig.STATUS_FAILED);
                LocalBroadcastManager.getInstance(BlazeDownloaderService.this)
                        .sendBroadcast(resultIntent);
            }
            stopSelf();
        }
    }

    private class TaskResult{
        private String exception;
        private boolean result;

        private TaskResult(String exception, boolean result) {
            this.exception = exception;
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }

        public String getException() {
            return exception;
        }
    }
}
