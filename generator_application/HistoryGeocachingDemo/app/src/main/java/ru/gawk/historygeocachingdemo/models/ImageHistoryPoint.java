package ru.gawk.historygeocachingdemo.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by GAWK on 06.08.2017.
 */

public class ImageHistoryPoint implements Serializable {
    /* контанты для парсера JSON */
    public static final String IMAGE_NUMBER = "number";
    public static final String IMAGE_NAME = "name";
    public static final String IMAGE_URL = "image";

    private long mNumber;
    private String mImageURL, mName;

    public ImageHistoryPoint(long mNumber, String mImageURL, String mName) {
        this.mNumber = mNumber;
        this.mImageURL = mImageURL;
        this.mName = mName;
    }

    public ImageHistoryPoint(JSONObject obj) {
        try {
            mNumber = obj.getLong(IMAGE_NUMBER);
            mImageURL = obj.getString(IMAGE_URL);
            mName = obj.getString(IMAGE_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getNumber() {
        return mNumber;
    }

    public void setNumber(long mNumber) {
        this.mNumber = mNumber;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
