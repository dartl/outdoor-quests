package ru.gawk.historygeocachingdemo.adapters.main_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by GAWK on 15.11.2017.
 */

public abstract class ListArrayRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Context mContext;
    private ArrayList mArrayList;

    public ListArrayRecyclerViewAdapter(Context mContext, ArrayList mArrayList) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, mArrayList, position);
    }

    @Override
    public int getItemCount() {
        if (mArrayList == null) return 0;
        return mArrayList.size();
    }

    public abstract void onBindViewHolder(VH viewHolder, ArrayList arrayList, int position);

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList getArrayList() {
        return mArrayList;
    }

    public void setArrayList(ArrayList mArrayList) {
        this.mArrayList = mArrayList;
    }
}
