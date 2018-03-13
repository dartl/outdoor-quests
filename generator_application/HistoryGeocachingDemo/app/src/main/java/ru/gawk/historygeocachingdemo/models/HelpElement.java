package ru.gawk.historygeocachingdemo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by GAWK on 20.03.2017.
 */

public class HelpElement implements Serializable {
    /* контанты для парсера JSON */
    public static final String HELP_NUMBER = "number";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";

    private long number;
    private String data, image;
    private boolean active;

    public HelpElement() {}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public HelpElement(JSONObject obj) {
        try {
            number = obj.getLong(HELP_NUMBER);
            active = false;
            data = obj.getString(TYPE_TEXT);
            image = obj.getString(TYPE_IMAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "HelpElement{" +
                "number=" + number +
                ", data='" + data + '\'' +
                ", image='" + image + '\'' +
                ", active=" + active +
                '}';
    }
}
