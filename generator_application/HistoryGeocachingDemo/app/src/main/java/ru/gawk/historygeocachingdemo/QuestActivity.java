package ru.gawk.historygeocachingdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.gawk.historygeocachingdemo.adapters.LiteMessage;
import ru.gawk.historygeocachingdemo.adapters.MarkersInfoAdapter;
import ru.gawk.historygeocachingdemo.adapters.ViewPagerAdapter;
import ru.gawk.historygeocachingdemo.fragment_quest.QuestFragmentHelp;
import ru.gawk.historygeocachingdemo.fragment_quest.QuestFragmentImages;
import ru.gawk.historygeocachingdemo.fragment_quest.QuestFragmentInfo;
import ru.gawk.historygeocachingdemo.models.HistoryPoint;

/**
 * Created by GAWK on 05.03.2017.
 */

public class QuestActivity extends ParentActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private MarkersInfoAdapter points;
    private ImageButton imageButton_InfoNext, imageButton_InfoPrev;
    private TextView mTextViewNamePoint, mTextViewStatusPoint;
    private QuestFragmentHelp mQuestFragmentHelp;
    private QuestFragmentInfo mQuestFragmentInfo;
    private QuestFragmentImages mQuestFragmentImages;
    private long mQuestNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_quest_main);

        points = (MarkersInfoAdapter) getIntent().getSerializableExtra("points");
        mQuestNumber = getIntent().getLongExtra("quest_number",-1);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        imageButton_InfoNext = (ImageButton) findViewById(R.id.imageButton_InfoNext);
        imageButton_InfoPrev = (ImageButton) findViewById(R.id.imageButton_InfoPrev);
        mTextViewNamePoint = (TextView) findViewById(R.id.textViewNamePoint);
        mTextViewStatusPoint = (TextView) findViewById(R.id.textViewStatusPoint);

        imageButton_InfoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("GAWK_ERR","points.getNext()");
                if (points.isLastActive()) {
                    LiteMessage mLiteMessage = new LiteMessage(getApplicationContext(),getString(R.string.quest_error_next_point));
                    mLiteMessage.show();
                } else {
                    points.getNext();
                    refresh();
                }
            }
        });

        imageButton_InfoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("GAWK_ERR","points.getPrev()");
                if (points.getSelectIndex() == 0) {
                    LiteMessage mLiteMessage = new LiteMessage(getApplicationContext(),getString(R.string.quest_error_prev_point));
                    mLiteMessage.show();
                } else {
                    points.getPrev();
                    refresh();
                }
            }
        });
        setTextNameAndStatus();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mQuestFragmentHelp = new QuestFragmentHelp(this);
        mQuestFragmentInfo = new QuestFragmentInfo(this);
        mQuestFragmentImages= new QuestFragmentImages(this);
        adapter.addFragment(mQuestFragmentInfo, getResources().getString(R.string.quest_info_header));
        adapter.addFragment(mQuestFragmentHelp, getResources().getString(R.string.quest_help_header));
        adapter.addFragment(mQuestFragmentImages, getResources().getString(R.string.quest_images_header));
        viewPager.setAdapter(adapter);
    }

    private void refresh() {
        Log.e("GAWK_ERR","refresh() = " + points.getCurrentHistoryPoint());
        setTextNameAndStatus();
        mQuestFragmentHelp.refresh();
        mQuestFragmentInfo.refresh();
        mQuestFragmentImages.refresh();
    }

    private void setTextNameAndStatus() {
        mTextViewNamePoint.setText(points.getCurrentHistoryPoint().getName());
        if (points.isLastActive()) {
            int count = points.getSelectIndex()+1;
            mTextViewStatusPoint.setText(getText(R.string.quest_status_search) + "("+ count + "/" + points.size() + ")");
            mTextViewStatusPoint.setTextColor(ContextCompat.getColor(this, R.color.colorRed500));
        } else {
            int count = points.getSelectIndex()+1;
            mTextViewStatusPoint.setText(getText(R.string.quest_status_ok) + "("+ count + "/" + points.size() + ")");
            mTextViewStatusPoint.setTextColor(ContextCompat.getColor(this, R.color.colorGren500));
        }
    }

    public MarkersInfoAdapter getPoints() {
        return points;
    }

    public HistoryPoint getCurrentPoint() {
        return points.getCurrentHistoryPoint();
    }

    public long getQuestNumber() {
        return mQuestNumber;
    }

    public void setQuestNumber(long mQuestNumber) {
        this.mQuestNumber = mQuestNumber;
    }
}
