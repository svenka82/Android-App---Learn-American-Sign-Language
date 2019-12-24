package com.example.group16as1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class record extends AppCompatActivity {

    Uri fileUri = null;
    File fl = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }

    public void record(View view) {

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

            Bundle bun = getIntent().getExtras();
            String gestureName = bun.getString("GestureName");

            Date currentTime = Calendar.getInstance().getTime();
            String prefix = "/Video-Group16-" + gestureName + "-" + currentTime.getTime() + "-" + ".mp4";

            fl = new File(getExternalFilesDir(null) + prefix);
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = FileProvider.getUriForFile(record.this,
                    BuildConfig.APPLICATION_ID + ".provider", fl);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);


            if (intent.resolveActivity(getPackageManager()) != null) {

                startActivityForResult(intent, 200);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200) {

            if (resultCode == RESULT_OK) {
                System.out.println("OK");
                fileUri = data.getData();

            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("Canceled");
            } else {
                System.out.println("Failed");
            }
        }
    }

    public void upload(View view) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("uploaded_file", fl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post("http://10.0.2.2:5000/upload", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    Toast.makeText(getApplicationContext(),
                            "File Upload Complete.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void play(View view) {

        VideoView vv = (VideoView) findViewById(R.id.videoView2);
        vv.setVideoURI(fileUri);
        vv.start();
    }
}
