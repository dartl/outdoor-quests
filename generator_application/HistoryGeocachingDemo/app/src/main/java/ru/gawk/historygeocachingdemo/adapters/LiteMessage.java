package ru.gawk.historygeocachingdemo.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by GAWK on 06.08.2017.
 */

public class LiteMessage {
    private String mText;
    private int mDuration;
    private Context mContext;

    private Toast mToast;

    public LiteMessage(Context mContext, String mText) {
        this.mText = mText;
        this.mDuration = Toast.LENGTH_SHORT;
        this.mContext = mContext;
        create();
    }

    public LiteMessage(Context mContext, String mText, int mDuration) {
        this.mText = mText;
        this.mDuration = mDuration;
        this.mContext = mContext;
        create();
    }

    private void create() {
        mToast = Toast.makeText(mContext, mText, mDuration);
    }

    public void show() {
        mToast.show();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
