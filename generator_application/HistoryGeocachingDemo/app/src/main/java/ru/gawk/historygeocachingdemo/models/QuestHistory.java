package ru.gawk.historygeocachingdemo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by GAWK on 20.03.2017.
 */

public class QuestHistory implements Serializable{
    /* контанты для парсера JSON */
    public static final String QUEST_NUMBER = "number";
    public static final String QUEST_NAME = "name";
    public static final String QUEST_DESCRIPTION = "description";
    public static final String QUEST_POINTS = "points";
    public static final String QUEST_IMAGE = "image";

    private long number;
    private String name, description, image;
    private ArrayList<HistoryPoint> points = new ArrayList<>();
    private boolean active;

    public void start() {
        Iterator<HistoryPoint> point_iterator = points.iterator();
        HistoryPoint temp;
        while (point_iterator.hasNext()){
            point_iterator.next().setCheck(false);
        }
        points.get(0).setCheck(true);
    }

    public QuestHistory() {}

    public QuestHistory(JSONObject obj) {
        try {
            number = obj.getLong(QUEST_NUMBER);
            name = obj.getString(QUEST_NAME);
            description = obj.getString(QUEST_DESCRIPTION);
            image = obj.getString(QUEST_IMAGE);
            active = false;
            JSONArray points = obj.getJSONArray(QUEST_POINTS);
            for (int i = 0; i < points.length(); i++) {
                JSONObject point = points.getJSONObject(i);
                HistoryPoint hPoint = new HistoryPoint(point);
                this.points.add(hPoint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public ArrayList<HistoryPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<HistoryPoint> points) {
        this.points = points;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "QuestHistory{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }
}
