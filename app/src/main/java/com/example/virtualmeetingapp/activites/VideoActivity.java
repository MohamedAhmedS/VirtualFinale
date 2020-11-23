package com.example.virtualmeetingapp.activites;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.utils.ToastHelper;

import java.io.File;
import java.io.IOException;

import carbon.widget.ImageView;

public class VideoActivity extends BaseActivity {

    ProgressBar progressBar;
    VideoView videoView;
    ImageView ivPlay;

    @Override
    public void initXML() {
        progressBar = findViewById(R.id.downloadProgress);
        videoView = findViewById(R.id.vvVideo);
        ivPlay = findViewById(R.id.ivPlay);
    }

    @Override
    public void initVariables() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initXML();
        initVariables();

        ivPlay.setOnClickListener(v -> {
            if (!downloading) {
                String url = getIntent().getStringExtra("videoUrl");
                String title = String.valueOf(getIntent().getLongExtra("title", -1));

                String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/files/Video/";
                File videoDir = new File(file);
                if (!videoDir.exists()) {
                    boolean created = videoDir.mkdirs();
                }
                File videoFile = new File(videoDir.getAbsolutePath() + "/" + title + ".mp4");
                if (!finishDownload && !videoFile.exists()) {
                    downloadManager(url, title, videoDir.getAbsolutePath());
                } else if (!videoLoaded) {
                    playVideo(videoFile.getAbsolutePath());
//                    ivPlay.setVisibility(View.GONE);
//                    videoView.setVideoURI(Uri.parse(videoFile.getAbsolutePath()));
//                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            ivPlay.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    videoView.start();
//                    videoLoaded = true;
                } else {
                    videoView.start();
                    ivPlay.setVisibility(View.GONE);
                }
            } else {
                ToastHelper.showToast("Please wait for download to complete.");
            }
        });

//        videoView.setOnClickListener(vv -> {
//            if (videoView.isPlaying()) {
//                ivPlay.setVisibility(View.VISIBLE);
//                videoView.pause();
//            } else {
//                ivPlay.setVisibility(View.GONE);
//                videoView.resume();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(onDownloadComplete, IntentFilter.create("Download Completed", ""));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(onDownloadComplete);
    }

    long downloadID = -1;
    boolean finishDownload = false;
    boolean downloading = false;
    boolean videoLoaded = false;

    AsyncTask downloadingTask = null;

    private void downloadManager(String url, String title, String directory) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle("" + title);
        // in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(this, "/Video/", title + ".mp4");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        downloadID = manager.enqueue(request);

        ToastHelper.showToast("Downloading...");
        downloadingTask = new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                int progress;
                while (!finishDownload) {
                    Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(downloadID));
                    if (cursor.moveToFirst()) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        switch (status) {
                            case DownloadManager.STATUS_FAILED: {
                                finishDownload = true;
                                downloading = false;
                                break;
                            }
                            case DownloadManager.STATUS_PAUSED:
                                break;
                            case DownloadManager.STATUS_PENDING:
                                break;
                            case DownloadManager.STATUS_RUNNING: {
                                downloading = true;
                                final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (total >= 0) {
                                    final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    // if you use downloadmanger in async task, here you can use like this to display progress.
                                    // Don't forget to do the division in long to get more digits rather than double.
                                    publishProgress((int) ((downloaded * 100L) / total));
                                }
                                break;
                            }
                            case DownloadManager.STATUS_SUCCESSFUL: {
                                progress = 100;
                                // if you use aysnc task
                                publishProgress(progress);
                                downloading = false;
                                finishDownload = true;
                                break;
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(values[0]);

                if (values[0] == 100) {
                    progressBar.setVisibility(View.GONE);
                    ToastHelper.showToast("Downloading Completed");

                    playVideo(directory + "/" + title + ".mp4");
                }
            }
        }.execute();
    }

    private void playVideo(String videoFile) {
        ivPlay.setVisibility(View.GONE);
        videoView.setVideoURI(Uri.parse(videoFile));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivPlay.setVisibility(View.VISIBLE);
            }
        });
        videoView.start();
        videoLoaded = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (downloadingTask != null) {
            downloadingTask.cancel(true);
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(VideoActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
