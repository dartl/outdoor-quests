package ru.gawk.historygeocachingdemo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.fragment_quest.QuestFragmentHelp;
import ru.gawk.historygeocachingdemo.models.HelpElement;

/**
 * Created by GAWK on 20.03.2017.
 */

public class HelpsAdapter extends RecyclerView.Adapter<HelpsAdapter.ViewHolder> {
    private ArrayList<HelpElement> mDataset;
    private QuestFragmentHelp mFragment;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView mImageViewHelp;
        TextView mTextView;
        Button buttonOpenHelp;
        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            mImageViewHelp = (ImageView) v.findViewById(R.id.imageViewHelp);
            buttonOpenHelp = (Button) v.findViewById(R.id.buttonOpenHelp);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HelpsAdapter(ArrayList<HelpElement> myDataset, QuestFragmentHelp myFragment) {
        mDataset = myDataset;
        mFragment = myFragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HelpsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset.get(position).isActive()) {
            holder.mTextView.setText(mDataset.get(position).getData());
            if (holder.mImageViewHelp.getVisibility() == View.GONE && !mDataset.get(position).getImage().equals("")) new AsyncImgDownload().execute(new StringAndHolder(mDataset.get(position).getImage(), holder));
            holder.buttonOpenHelp.setVisibility(View.GONE);
        } else {
            holder.mTextView.setVisibility(View.GONE);
            holder.mImageViewHelp.setVisibility(View.GONE);
            holder.buttonOpenHelp.setVisibility(View.VISIBLE);
            holder.buttonOpenHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.setActiveHelp(position);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class StringAndHolder {
        private String mStr;
        private ViewHolder mHolder;

        public StringAndHolder(String mStr, ViewHolder mHolder) {
            this.mStr = mStr;
            this.mHolder = mHolder;
        }

        public String getStr() {
            return mStr;
        }

        public void setStr(String mStr) {
            this.mStr = mStr;
        }

        public ViewHolder getHolder() {
            return mHolder;
        }

        public void setHolder(ViewHolder mHolder) {
            this.mHolder = mHolder;
        }
    }

    class AsyncImgDownload extends AsyncTask<StringAndHolder, Void, Bitmap> {
        StringAndHolder mStringAndHolder;
        @Override
        protected Bitmap doInBackground(StringAndHolder... arg) {
            mStringAndHolder = arg[0];
            URL url = null;
            try {
                url = new URL(mStringAndHolder.getStr());
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
            mStringAndHolder.getHolder().mImageViewHelp.setImageBitmap(s);
            mStringAndHolder.getHolder().mImageViewHelp.setVisibility(View.VISIBLE);
        }
    }
}