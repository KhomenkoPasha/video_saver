package android.videosaver.khomenko.videosaver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.videosaver.khomenko.videosaver.adapters.RadioDownload;
import android.videosaver.khomenko.videosaver.youtubeExtractor.VideoMeta;
import android.videosaver.khomenko.videosaver.youtubeExtractor.YouTubeExtractor;
import android.videosaver.khomenko.videosaver.youtubeExtractor.YtFile;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;

import java.io.File;

public class StartActivity extends AppCompatActivity {

    // private TextView mTextMessage;
    private static String youtubeLink;

    private RadioGroup mainLayout;
    private ProgressBar mainProgressBar;
    private Button download;
    private EditText inputURL;
    private Button button_detect;
    private TextView file_name;
    private RadioDownload rd;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:

                    Intent i = new Intent(StartActivity.this, FileBrowser.class); //works for all 3 main classes (i.e FileBrowser, FileChooser, FileBrowserWithCustomHandler)
                    i.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Downloads").getAbsolutePath());
                    startActivity(i);
                    return true;
                case R.id.navigation_notifications:
                    // mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // this.onCreate(null);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        android.support.v7.app.ActionBar ab = getSupportActionBar();

        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, // Width of TextView
                ActionBar.LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);
        // Set text to display in TextView
        if (ab != null) {
            tv.setText(ab.getTitle()); // ActionBar title text
        }
        // Set the text color of TextView to black
        // This line change the ActionBar title text color
        tv.setTextColor(Color.WHITE);
        // Set the TextView text size in dp
        // This will change the ActionBar title text size
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        // Set the ActionBar display option
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(tv);
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = findViewById(R.id.main_layout);
        mainProgressBar = findViewById(R.id.prgrBar);
        download = findViewById(R.id.button_download);
        inputURL = findViewById(R.id.inputURL);
        button_detect = findViewById(R.id.button_detect);
        file_name = findViewById(R.id.file_name);

        // mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //  inputURL.setText("https://youtu.be/koYzo2QCQL0");

        mainLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rd = findViewById(checkedId);

            }
        });


        button_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.setEnabled(false);
                mainProgressBar.setVisibility(View.VISIBLE);
                mainLayout.removeAllViews();
                String urlinp = inputURL.getText().toString();
                if (!urlinp.isEmpty()
                        && (urlinp.contains("://youtu.be/") || urlinp.contains("youtube.com/watch?v="))) {
                    youtubeLink = urlinp;
                    // We have a valid link
                    getYoutubeDownloadUrl(youtubeLink);
                } else {
                    file_name.setText("");
                    mainProgressBar.setVisibility(View.GONE);
                    Toast.makeText(StartActivity.this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rd != null) {
                    downloadFromUrl(rd.getYtfile().getUrl(), rd.getTitle(), rd.getFileName());
                    download.setEnabled(false);
                    mainProgressBar.setVisibility(View.GONE);
                    mainLayout.removeAllViews();
                    rd = null;
                }
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("yURL") != null) {
                String ytLink = getIntent().getExtras().getString("yURL");
                if (ytLink != null
                        && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v="))) {
                    youtubeLink = ytLink;
                    // We have a valid link
                    getYoutubeDownloadUrl(youtubeLink);
                    inputURL.setText(youtubeLink);
                } else {
                    Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                }
            } else if (savedInstanceState != null && youtubeLink != null) {
                getYoutubeDownloadUrl(youtubeLink);
            } else mainProgressBar.setVisibility(View.GONE);

        } else mainProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERM", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                try {
                    mainProgressBar.setVisibility(View.GONE);

                    if (ytFiles == null) {
                        // Something went wrong we got no urls. Always check this.
                        Toast.makeText(StartActivity.this, getString(R.string.no_content), Toast.LENGTH_LONG).show();
                        file_name.setText("");
                        return;
                    }
                    // Iterate over itags
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        // ytFile represents one file with its url and meta data
                        YtFile ytFile = ytFiles.get(itag);

                        // Just add videos in a decent format => height -1 = audio
                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            addButtonToMainLayout(vMeta.getTitle(), ytFile);
                        }

                    }
                    file_name.setText(vMeta.getTitle());
                    download.setEnabled(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.extract(youtubeLink, true, false);
    }

    /*

    @Override
    protected void onStart() {
        super.onStart();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("RUNING", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("RUNING", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.apply();

    }
*/

    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " MPEG-DASH" : "";

        RadioDownload btn = (RadioDownload) getLayoutInflater().inflate(R.layout.radio_button, null, false);

        btn.setYtfile(ytfile);

        String filenameButon;

        if (videoTitle.length() > 55) {
            filenameButon = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
        } else {
            filenameButon = videoTitle + "." + ytfile.getFormat().getExt();
        }
        filenameButon = filenameButon.replaceAll("[\\\\><\"|*?%:#/]", "");

        btn.setFileName(filenameButon);
        btn.setTitle(videoTitle);
        btn.setText(btnText);

        file_name.setText(filenameButon.substring(0, filenameButon.lastIndexOf(".")));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, R.style.AppTheme_button
        );
        params.setMargins(20, 10, 20, 10);
        btn.setLayoutParams(params);

        mainLayout.addView(btn);
    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


}
