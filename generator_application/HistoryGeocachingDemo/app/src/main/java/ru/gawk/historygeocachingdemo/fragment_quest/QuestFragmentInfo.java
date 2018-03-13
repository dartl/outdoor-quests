package ru.gawk.historygeocachingdemo.fragment_quest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.gawk.historygeocachingdemo.QuestActivity;
import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.models.HelpElement;
import ru.gawk.historygeocachingdemo.models.HistoryPoint;

/**
 * Created by GAWK on 05.03.2017.
 */

public class QuestFragmentInfo extends Fragment {
    private QuestActivity mainActivity;
    private WebView mWebViewInfo;
    private TextView mTextViewInfoEmpty;

    public QuestFragmentInfo() {
        // Required empty public constructor
    }

    public QuestFragmentInfo(QuestActivity mainActivity) {
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
        View view = inflater.inflate(R.layout.content_quest_info, null);
        mWebViewInfo = (WebView) view.findViewById(R.id.webViewInfo);
        mTextViewInfoEmpty = (TextView) view.findViewById(R.id.textViewInfoEmpty);
        refresh();
        return view;
    }

    public void refresh() {
        HistoryPoint point = this.mainActivity.getCurrentPoint();
        String content = "";
        if (point.getDescription().equals("") && point.getShort_description().equals("")) {
            mTextViewInfoEmpty.setVisibility(View.VISIBLE);
            mWebViewInfo.setVisibility(View.GONE);
            return;
        }
        mTextViewInfoEmpty.setVisibility(View.GONE);
        mWebViewInfo.setVisibility(View.VISIBLE);
        if (this.mainActivity.getPoints().isLastActive()) {
            content = point.getShort_description() + "<hr/><center>" + getText(R.string.quest_info_not_open) + "</center>";
        } else {
            content = point.getDescription();
        }
        mWebViewInfo.loadData(content, "text/html; charset=utf-8", "UTF-8");
    }

}
