package ru.gawk.historygeocachingdemo.fragment_quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ru.gawk.historygeocachingdemo.QuestActivity;
import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.models.HistoryPoint;
import ru.gawk.historygeocachingdemo.models.ImageHistoryPoint;

/**
 * Created by GAWK on 05.03.2017.
 */

public class QuestFragmentImages extends Fragment {
    private QuestActivity mainActivity;
    private HistoryPoint mHistoryPoint;
    protected LinearLayout mLinearLayoutImageList;
    private TextView mTextViewImagesEmpty;

    public QuestFragmentImages() {
        // Required empty public constructor
    }

    public QuestFragmentImages(QuestActivity mainActivity) {
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
        View view = inflater.inflate(R.layout.content_quest_images, null);
        mLinearLayoutImageList = (LinearLayout) view.findViewById(R.id.linearLayoutImageList);
        mTextViewImagesEmpty = (TextView) view.findViewById(R.id.textViewImagesEmpty);
        refresh();
        return view;
    }

    public void refresh() {
        Log.e("GAWK_ERR","QuestFragmentImages refresh() called");
        if (mLinearLayoutImageList == null) {
            return;
        }
        while (mLinearLayoutImageList.getChildCount() > 0) {
            mLinearLayoutImageList.removeViewAt(0);
        }
        mHistoryPoint = this.mainActivity.getCurrentPoint();
        if (mHistoryPoint.getImages().size() == 0) {
            mTextViewImagesEmpty.setVisibility(View.VISIBLE);
            mLinearLayoutImageList.setVisibility(View.GONE);
            return;
        }
        if (this.mainActivity.getPoints().isLastActive()) {
            TextView textView = new TextView(getContext());
            textView.setText(getText(R.string.quest_info_not_open));
            mLinearLayoutImageList.addView(textView);
        } else {
            ArrayList<ImageHistoryPoint> mImageHistoryPoints = mHistoryPoint.getImages();
            for (ImageHistoryPoint imageHistoryPoint : mImageHistoryPoints) {
                new AsyncImgDownload().execute(imageHistoryPoint.getImageURL());
            }
            mTextViewImagesEmpty.setVisibility(View.GONE);
            mLinearLayoutImageList.setVisibility(View.VISIBLE);
        }
    }

    public void addViewImage(Bitmap s) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(s);
        mLinearLayoutImageList.addView(imageView);
    }

    class AsyncImgDownload extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... arg) {
            URL url = null;
            try {
                url = new URL(arg[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                assert url != null;
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            addViewImage(s);
        }
    }

}