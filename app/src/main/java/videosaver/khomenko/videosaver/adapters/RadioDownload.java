package videosaver.khomenko.videosaver.adapters;

import android.content.Context;
import android.util.AttributeSet;

import videosaver.khomenko.videosaver.youtubeExtractor.YtFile;


public class RadioDownload extends android.support.v7.widget.AppCompatRadioButton {
    private YtFile ytfile;
    private String fileName;
    private String title;

    public RadioDownload(Context context) {
        super(context);
    }

    public RadioDownload(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioDownload(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public YtFile getYtfile() {
        return ytfile;
    }

    public void setYtfile(YtFile ytfile) {
        this.ytfile = ytfile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
