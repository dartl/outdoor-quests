package ru.gawk.historygeocachingdemo.adapters;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.models.QuestHistory;

/**
 * Created by GAWK on 28.11.2017.
 */

public class InstanceDB {
    private static InstanceDB sInstance;

    private ArrayList<QuestHistory> listElements = new ArrayList<>();
    private JSONArray jreader;
    final Random random = new Random();

    private long mActiveQuest = -1;

    /**
     * Метод для получения ссылки на статический класс
     */
    public static synchronized InstanceDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InstanceDB(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Стандартный конструктор для получения экземпляра класса
     */
    private InstanceDB(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.quest);
        try {
            String json = convertStreamToString(inputStream);
            inputStream.close();
            jreader = new JSONArray(json);
            if (random.nextBoolean()) {
                for (int i = 0; i < jreader.length(); i++) {
                    JSONObject objNote = jreader.getJSONObject(i);
                    Log.e("GAWK_ERR","objNote = " + objNote.toString());
                    listElements.add(new QuestHistory(objNote));
                }
            } else {
                Log.e("GAWK_ERR","ojreader.length() = " + jreader.length());
                for (int i = jreader.length() - 1; i >= 0; i--) {
                    Log.e("GAWK_ERR","i = " + i);
                    JSONObject objNote = jreader.getJSONObject(i);
                    Log.e("GAWK_ERR","objNote = " + objNote.toString());
                    listElements.add(new QuestHistory(objNote));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public QuestHistory getQuestHistory(long number) {
        Log.e("GAWK_ERR", "number = " + number);
        return listElements.get((int)number);
    }

    public QuestHistory getCurrentQuestHistory() {
        for (int i = 0; i < listElements.size(); i++) {
            if (listElements.get(i).getNumber() == mActiveQuest) return listElements.get(i);
        }
        return null;
    }

    public ArrayList<QuestHistory> getListElements() {
        return listElements;
    }

    public void setListElements(ArrayList<QuestHistory> listElements) {
        this.listElements = listElements;
    }

    public long getActiveQuest() {
        return mActiveQuest;
    }

    public void setActiveQuest(long mActiveQuest) {
        this.mActiveQuest = mActiveQuest;
    }
}
