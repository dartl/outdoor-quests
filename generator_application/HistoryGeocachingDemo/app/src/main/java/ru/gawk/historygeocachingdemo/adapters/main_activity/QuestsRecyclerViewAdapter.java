package ru.gawk.historygeocachingdemo.adapters.main_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.gawk.historygeocachingdemo.MainActivity;
import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.adapters.InstanceDB;
import ru.gawk.historygeocachingdemo.models.QuestHistory;

/**
 * Created by GAWK on 15.11.2017.
 */

public class QuestsRecyclerViewAdapter extends ListArrayRecyclerViewAdapter<QuestsRecyclerViewAdapter.ViewHolder>  {
    private MainActivity mMainActivity;
    private InstanceDB mInstanceDB;

    public QuestsRecyclerViewAdapter(MainActivity mainActivity, ArrayList mArrayList) {
        super(mainActivity.getApplicationContext(), mArrayList);
        this.mMainActivity = mainActivity;
        mInstanceDB = InstanceDB.getInstance(mainActivity);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        View parent;
        ImageView mImageView;
        TextView mTextViewDescription, mTextViewName;
        Button mAppCompatButtonStart;

        ViewHolder(View v) {
            super(v);
            parent = v;
            mCardView = (CardView) v.findViewById(R.id.card_view_quest);
            mImageView = (ImageView) v.findViewById(R.id.imageViewImage);
            mTextViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
            mTextViewName = (TextView) v.findViewById(R.id.textViewName);
            mAppCompatButtonStart = (Button) v.findViewById(R.id.appCompatButtonStart);
        }

        void setData(final ArrayList c, final int position, final MainActivity mMainActivity) {
            QuestHistory mQuestHistory = (QuestHistory) c.get(position);
            mTextViewDescription.setText(mQuestHistory.getDescription());
            mTextViewName.setText(mQuestHistory.getName());

            switch (mQuestHistory.getImage()) {
                case "1":
                    mImageView.setBackgroundResource(R.drawable.quest_image_1);
                    break;
                case "2":
                    mImageView.setBackgroundResource(R.drawable.quest_image_2);
                    break;
                case "3":
                    mImageView.setBackgroundResource(R.drawable.quest_image_3);
                    break;
                case "4":
                    mImageView.setBackgroundResource(R.drawable.quest_image_4);
                    break;
            }

            mAppCompatButtonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestDialog(((QuestHistory) c.get(position)).getNumber(), mMainActivity);
                }
            });

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestDialog(((QuestHistory) c.get(position)).getNumber(), mMainActivity);
                }
            });
        }

        private void startQuestDialog(final long mQuestHistory, final MainActivity mMainActivity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
            builder.setTitle(R.string.alert_dialog_start_quest_title)
                    .setMessage(R.string.alert_dialog_start_quest_text)
                    .setPositiveButton(R.string.alert_dialog_start_quest_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMainActivity.startMap(mQuestHistory);
                        }
                    }).setNegativeButton(R.string.alert_dialog_start_quest_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }



    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public QuestsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_quests, parent, false);
        return new QuestsRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, ArrayList arrayList, int position) {
        viewHolder.setData(arrayList, position, mMainActivity);
    }

}
