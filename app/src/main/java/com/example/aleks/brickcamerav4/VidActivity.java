package com.example.aleks.brickcamerav4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VidActivity extends AppCompatActivity {

    private VideoView vvLastVid;
    private LinearLayout llInfo;
    private TextView tvOrientation;
    private TextView tvLong;
    private TextView tvLat;
    private Button btnPic;
    private Button btnVideo;

    private static final int VIDEO_REQUEST_CODE = 321;
    public static final String SAVED_VIDEO_PREFERENCES = "SAVED_VIDEO_PREFERENCES";
    public static final String SAVED_VIDEO_PATH = "SAVED_PICTURE_PATH";

    private File videoDirectory;
    private String videoDirectoryStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vid);

        vvLastVid = (VideoView) findViewById(R.id.vvLastVideo);
        llInfo = (LinearLayout) findViewById(R.id.llInfo);
        tvOrientation = (TextView) findViewById(R.id.tvOrientationValue);
        tvLat = (TextView) findViewById(R.id.tvLatValue);
        tvLong = (TextView) findViewById(R.id.tvLongValue);
        btnPic = (Button) findViewById(R.id.btnTakePic);
        btnVideo = (Button) findViewById(R.id.btnRecordVideo);

        if(isExternalStorageReadable() && isExternalStorageWritable())
            Toast.makeText(this, "Can do stuff", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Can't do stuff", Toast.LENGTH_LONG).show();
        initializeDirectory();
    }

    private void initializeDirectory() {
        videoDirectory = getMyVidDirectory();
        if(videoDirectory != null)
            videoDirectoryStr = videoDirectory.getPath();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vid, menu);
        return true;
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".csv")){
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == VIDEO_REQUEST_CODE)
        {
            Log.d("onActivityResult", "VIDEO_REQUEST_CODE");
            if(resultCode == RESULT_OK)
            {
                Log.d("onActivityResult", "RESULT_OK");
                String filename = loadLastAttemptedVideoCaptureFilename();
//                saveMode();
                setVideo(filename, vvLastVid);
            }
            if(resultCode == RESULT_CANCELED)
            {
                Log.d("onActivityResult", "RESULT_CANCELED");
            }
        }

    }

    private void saveLastAttemptedVideoCaptureFilename(String filename) {
        SharedPreferences prefs = getSharedPreferences(SAVED_VIDEO_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        videoDirectoryStr = filename;

        editor.putString(SAVED_VIDEO_PATH, videoDirectoryStr);
        editor.apply();
    }

    private String loadLastAttemptedVideoCaptureFilename() {
        SharedPreferences prefs = getSharedPreferences(SAVED_VIDEO_PREFERENCES, MODE_PRIVATE);
        String saved_vid_path;

        File movStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "BrickCamera");

        saved_vid_path = prefs.getString(SAVED_VIDEO_PATH, movStorageDir + File.separator + "VID_20150923_185106.mp4");
        return saved_vid_path;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String vid_filename = loadLastAttemptedVideoCaptureFilename();
        setVideo(vid_filename, vvLastVid);
    }

    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void setVideo(String filename, VideoView vv)
    {
        MediaController mc = new MediaController(this);
        mc.setAnchorView(vv);
        mc.setMediaPlayer(vv);

        vv.setMediaController(mc);
//            vv.setClickable(true);
//            vv.setEnabled(true);
        getExifInfo(filename);

        vv.setVideoPath(filename);
    }

    private void getExifInfo(String filename) {
        String orientation = null;
        String latValue;
        String latRef;
        String longValue;
        String longRef;

        MediaMetadataRetriever metaRetriver;
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(filename);

        String infoDate = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String p = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
        latValue = p.substring(0, Math.min(p.length(), 8));
        longValue = p.substring(8, Math.min(p.length(), 17));
        tvLat.setText(latValue);
        tvLong.setText(longValue);
    }

    private File getMyVidDirectory()
    {
        File mediaStorageDir;

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "BrickCamera");

        if (checkMyDirectory(mediaStorageDir))
            return mediaStorageDir;
        else
            return null;
    }

    private boolean checkMyDirectory(File filename)
    {
        if (! filename.exists()){
            if (! filename.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return false;
            }
        }
        return true;
    }

    public void onBtnRecordVideoClick(View view) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String filename = videoDirectory.getPath() + File.separator+"VID_"+timeStamp+".mp4";

        videoDirectoryStr = filename;

        File videoFile = new File(filename);
        Uri videoUri = Uri.fromFile(videoFile);

//        saveLastAttemptedImageCaptureFilename(videoDirectoryStr);
        saveLastAttemptedVideoCaptureFilename(filename);

        recordVideo(videoUri);
    }

    public void recordVideo(Uri filepath)
    {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, filepath);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if(videoIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(videoIntent, VIDEO_REQUEST_CODE);
    }

    public void onVideoClick(View view) {
        vvLastVid.start();
    }
}
