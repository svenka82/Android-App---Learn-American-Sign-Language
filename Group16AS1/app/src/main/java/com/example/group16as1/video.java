package com.example.group16as1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class video extends AppCompatActivity {

    String gesture = "";
    String gestureUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        URLMap helper = new URLMap();
        HashMap<String, String> urlMap = helper.getMapping();

        Bundle bun = getIntent().getExtras();

        if (bun != null) {
            gesture = bun.getString("Gesture");
            gestureUrl = urlMap.get(gesture);

            TextView view = (TextView) findViewById(R.id.textView5);
            view.setText(gesture);
        }
    }

    public void playVideo(View view) {
        DownloadJob job = new DownloadJob();
        job.execute();
    }

    public void moveToPractice(View view) {
        Intent intent = new Intent(video.this, record.class);
        intent.putExtra("GestureName", gesture);
        startActivity(intent);
    }

    public class DownloadJob extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            File SDCardRoot = getExternalFilesDir(null);
            File directory = new File(SDCardRoot, "/my_folder/");
            if (!directory.exists()) {
                directory.mkdir();
            }

            String fileName = "Action" + ".mp4";
            try {
                InputStream input = null;
                try {

                    URL url = new URL(gestureUrl);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setReadTimeout(95 * 1000);
                    urlConnection.setConnectTimeout(95 * 1000);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("X-Environment", "android");

                    urlConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                    urlConnection.setSSLSocketFactory(
                            (SSLSocketFactory) SSLSocketFactory.getDefault());

                    urlConnection.connect();
                    input = urlConnection.getInputStream();
                    OutputStream output = new FileOutputStream(new File(directory, fileName));

                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);

                        }
                        output.close();
                    } catch (Exception exception) {
                        Log.d("Error", String.valueOf(exception));
                        publishProgress(String.valueOf(exception));
                        output.close();
                    }
                } catch (Exception exception) {
                    publishProgress(String.valueOf(exception));

                } finally {
                    input.close();
                }
            } catch (Exception exception) {
                publishProgress(String.valueOf(exception));
            }

            return "true";
        }

        @Override
        protected void onPostExecute(String text) {
            VideoView vv = (VideoView) findViewById(R.id.videoView);
            vv.setVideoPath(getExternalFilesDir(null) + "/my_folder/Action.mp4");
            vv.start();
        }
    }
}
