package ru.gawk.historygeocachingdemo.fragment_quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import ru.gawk.historygeocachingdemo.QuestActivity;
import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.adapters.HelpsAdapter;
import ru.gawk.historygeocachingdemo.adapters.InstanceDB;
import ru.gawk.historygeocachingdemo.adapters.TestStatistics;
import ru.gawk.historygeocachingdemo.models.HelpElement;
import ru.gawk.historygeocachingdemo.models.HistoryPoint;

/**
 * Created by GAWK on 05.03.2017.
 */

public class QuestFragmentHelp extends Fragment {
    private QuestActivity mainActivity;
    private HistoryPoint point;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mTextViewEmpty;

    private InstanceDB mInstanceDB;

    public QuestFragmentHelp() {
        // Required empty public constructor
    }

    public QuestFragmentHelp(QuestActivity mainActivity) {
        // Required empty public constructor
        this.mainActivity = mainActivity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_quest_help, null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHelps);
        mTextViewEmpty = (TextView) view.findViewById(R.id.textViewHelpsEmpty);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mainActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        refresh();

        mInstanceDB = InstanceDB.getInstance(getContext());
        return view;
    }

    public void setActiveHelp(int position) {
        Log.e("GAWK_ERR", "setActiveHelp() position = " + position);

        if (point.getHelps().size() != 0) {
            mInstanceDB.getCurrentQuestHistory().getPoints().get((int)point.getNumber()).getHelps().get(position).setActive(true);
            point.getHelps().get(position).setActive(true);
            mAdapter.notifyItemChanged(position);
        }

        String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String str = "Активация новой подсказки - Quest(number=" +
                mainActivity.getQuestNumber() +  "); Point(number=" + point.getNumber() + "); " +
                point.getHelps().get(position).toString();
        new TestStatistics.saveActions().execute(android_id, str);
    }

    public void refresh() {
        point = this.mainActivity.getCurrentPoint();
        if (point.getHelps().size() > 0) {
            // specify an adapter (see also next example)
            mAdapter = new HelpsAdapter(point.getHelps(),this);
            mRecyclerView.setAdapter(mAdapter);
            mTextViewEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTextViewEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

}