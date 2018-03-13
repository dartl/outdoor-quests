package ru.gawk.historygeocachingdemo.adapters;

import java.io.Serializable;
import java.util.ArrayList;

import ru.gawk.historygeocachingdemo.models.HistoryPoint;

/**
 * Created by GAWK on 05.08.2017.
 */

public class MarkersInfoAdapter implements Serializable {
    private ArrayList<HistoryPoint> points = new ArrayList<>();
    private int pointIndex;
    private int selectIndex;

    public HistoryPoint getActiveHistoryPoint() {
        if (pointIndex >= 0) {
            return points.get(pointIndex);
        }
        return null;
    }

    public HistoryPoint getCurrentHistoryPoint() {
        if (selectIndex > points.size())  return points.get(points.size());
        return points.get(selectIndex);
    }

    public HistoryPoint getNext() {
        if (selectIndex < points.size() && selectIndex < pointIndex) {
            return points.get(++selectIndex);
        }
        return null;
    }

    public HistoryPoint getPrev() {
        if (selectIndex < points.size() && selectIndex > 0) {
            return points.get(--selectIndex);
        }
        return null;
    }

    public boolean isLastActive() {
        if (selectIndex == pointIndex) {
            return true;
        }
        return false;
    }

    public MarkersInfoAdapter(ArrayList<HistoryPoint> points, int pointIndex) {
        this.points = points;
        if (pointIndex >= 0) {
            this.selectIndex = this.pointIndex = pointIndex;
        } else {
            this.selectIndex = this.pointIndex = -1;
        }
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public ArrayList<HistoryPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<HistoryPoint> points) {
        this.points = points;
    }

    public int size() {
        return points.size();
    }
}
