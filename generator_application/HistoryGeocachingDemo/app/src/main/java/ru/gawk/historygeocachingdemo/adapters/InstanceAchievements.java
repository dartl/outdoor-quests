package ru.gawk.historygeocachingdemo.adapters;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;


import ru.gawk.historygeocachingdemo.R;

/**
 * Created by GAWK on 10.12.2017.
 */

public class InstanceAchievements {
    private static InstanceAchievements sInstanceAchievements;
    private Context mContext;
    private PrefUtil mPrefUtil;

    /**
     * Метод для получения ссылки на статический класс
     */
    public static synchronized InstanceAchievements getInstance(Context context) {
        if (sInstanceAchievements == null) {
            sInstanceAchievements = new InstanceAchievements(context.getApplicationContext());
        }
        return sInstanceAchievements;
    }

    /**
     * Стандартный конструктор для получения экземпляра класса
     */
    private InstanceAchievements(Context context){
        mContext = context;
        mPrefUtil = new PrefUtil(mContext);
    }

    public void checkAchievements() {
        try {
            if (mPrefUtil.getInt(PrefUtil.COMPLETE_QUEST,0) == 1) {
                Games.getAchievementsClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                        .unlock(mContext.getString(R.string.achievement));
            }

            if (mPrefUtil.getInt(PrefUtil.COMPLETE_QUEST,0) >= 2) {
                Games.getAchievementsClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                        .unlock(mContext.getString(R.string.achievement_2));
            }
        } catch (NullPointerException e) {
            return;
        }
    }
}
