package ru.gawk.historygeocachingdemo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GAWK on 20.03.2017.
 */

public class HistoryPoint implements Serializable {
    /* контанты для парсера JSON */
    public static final String POINT_NUMBER = "number";
    public static final String POINT_NAME = "name";
    public static final String POINT_SHORT_DESCRIPTION = "short_description";
    public static final String POINT_DESCRIPTION = "description";
    public static final String POINT_LAT = "lat";
    public static final String POINT_LONG = "long";
    public static final String POINT_RADIUS = "radius";
    public static final String POINT_HELPS = "help";
    public static final String POINT_IMAGES = "images";

    private long number;
    private String name, description,short_description;
    private double latitude, longitude;
    private int radius;
    private boolean check;
    private ArrayList<HelpElement> helps = new ArrayList<>();
    private ArrayList<ImageHistoryPoint> images = new ArrayList<>();

    public HistoryPoint() {

    }

    public HistoryPoint(JSONObject obj) {
        try {
            number = obj.getLong(POINT_NUMBER);
            name = obj.getString(POINT_NAME);
            description = obj.getString(POINT_DESCRIPTION);
            short_description = obj.getString(POINT_SHORT_DESCRIPTION);
            latitude = obj.getDouble(POINT_LAT);
            longitude = obj.getDouble(POINT_LONG);
            radius = obj.getInt(POINT_RADIUS);
            check = false;
            JSONArray helps = obj.getJSONArray(POINT_HELPS);
            for (int i = 0; i < helps.length(); i++) {
                JSONObject help = helps.getJSONObject(i);
                HelpElement hElement = new HelpElement(help);
                if (i == 0) {
                    hElement.setActive(true);
                }
                this.helps.add(hElement);
            }

            JSONArray images = obj.getJSONArray(POINT_IMAGES);
            for (int i = 0; i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                ImageHistoryPoint hElement = new ImageHistoryPoint(image);
                this.images.add(hElement);
            }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public ArrayList<HelpElement> getHelps() {
        return helps;
    }

    public void setHelps(ArrayList<HelpElement> helps) {
        this.helps = helps;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public ArrayList<ImageHistoryPoint> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageHistoryPoint> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "HistoryPoint{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", short_description='" + short_description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", check=" + check +
                ", helps=" + helps +
                ", images=" + images +
                '}';
    }
}
