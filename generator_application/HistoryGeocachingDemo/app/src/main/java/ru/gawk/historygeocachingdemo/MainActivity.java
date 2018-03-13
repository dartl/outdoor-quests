package ru.gawk.historygeocachingdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.gawk.historygeocachingdemo.adapters.PrefUtil;
import ru.gawk.historygeocachingdemo.adapters.TestStatistics;
import ru.gawk.historygeocachingdemo.adapters.InstanceDB;
import ru.gawk.historygeocachingdemo.adapters.main_activity.QuestsRecyclerViewAdapter;
import ru.gawk.historygeocachingdemo.models.QuestHistory;

public class MainActivity extends ParentActivity {
    private PrefUtil mPrefUtil;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean activeQuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String str = "onCreate() - " + getClass().getCanonicalName();
        new TestStatistics.saveActions().execute(android_id, str);

        setContentView(R.layout.content_main);
        setTitle(R.string.activity_name_list_quests);

        mPrefUtil = new PrefUtil(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewQuests);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new QuestsRecyclerViewAdapter(this, mInstanceDB.getListElements());
        mRecyclerView.setAdapter(mAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_list_quests).setCheckable(true).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPrefUtil.getBoolean(PrefUtil.TEST_START_MESSAGE,true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.test_start_message))
                    .setTitle(R.string.test_start_title);

            builder.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPrefUtil.saveBoolean(PrefUtil.TEST_START_MESSAGE,false);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Условия", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://platform.cofp.ru/privacy_policy.php"));
                    startActivity(browserIntent);
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });


            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void startMap(long id) {
        Log.e("GAWK_ERR", "id = " + id);
        activeQuest = mInstanceDB.getActiveQuest() == id;
        mInstanceDB.setActiveQuest(id);
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("active", activeQuest);
        startActivity(intent);
    }
}
