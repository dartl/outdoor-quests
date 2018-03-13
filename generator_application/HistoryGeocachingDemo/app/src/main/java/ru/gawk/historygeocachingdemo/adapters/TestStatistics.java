package ru.gawk.historygeocachingdemo.adapters;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by GAWK on 27.11.2017.
 */

public class TestStatistics {
    private static boolean mActiveStatistics = true;

    public static class saveCoordinate extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            TestStatistics.saveCoordinateFunc(params[0],params[1], params[2]);
            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    static void saveCoordinateFunc(String name, String lat, String lng) {
        if (mActiveStatistics) {
            try {
                URL mUrl = new URL("http://site1.cofp.ru/put_coordinate.php");
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write("username=" + name + "&lat=" + lat + "&long=" + lng);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                int code = conn.getResponseCode();
                Log.e("GAWK_ERR", "saveCoordinateFunc() - Response code of the object is "+code);
                if (code==200)
                {
                    Log.e("GAWK_ERR","saveCoordinateFunc() - OK");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class saveActions extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            TestStatistics.saveActionsFunc(params[0],params[1]);
            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    static void saveActionsFunc(String name, String action) {
        if (mActiveStatistics) {
            try {
                URL mUrl = new URL("http://site1.cofp.ru/put_action.php");
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write("username=" + name + "&action=" + action);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                int code = conn.getResponseCode();
                Log.e("GAWK_ERR", "saveActionsFunc() - Response code of the object is "+code);
                if (code==200)
                {
                    Log.e("GAWK_ERR","saveActionsFunc() - OK");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
