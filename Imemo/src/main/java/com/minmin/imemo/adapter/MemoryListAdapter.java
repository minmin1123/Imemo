package com.minmin.imemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minmin.imemo.R;
import com.minmin.imemo.model.Memory;

import java.util.List;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/01/15
 *   desc:纪念日的Adapter
 *   version:1.0
 */

public class MemoryListAdapter extends BaseAdapter {

    private int resourceId;

    private ViewHolder holder;

    private Context mConext;

    private List<Memory> mMemoryList;

    private final static int ARRIVED = 1;
    private final static int NOT_ARRIVED = 0;

    public MemoryListAdapter(Context context, int resource, List<Memory> objects) {
        super();
        mConext = context;
        resourceId = resource;
        mMemoryList = objects;
    }

    @Override
    public int getCount() {
        return mMemoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMemoryList.get(position).getIs_arrived()==0) {
            return NOT_ARRIVED;
        } else{
            return ARRIVED;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Memory memory = (Memory) getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mConext).inflate(resourceId, null);
            holder = new ViewHolder();
            holder.mTextTv = view.findViewById(R.id.textTv);
            holder.mDateTv = view.findViewById(R.id.dateTv);
            holder.mArrivedTv = view.findViewById(R.id.arrivedTv);
            holder.mCountTv = view.findViewById(R.id.countTv);
            holder.mArrivedIv = view.findViewById(R.id.arrivedIv);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.mTextTv.setText(memory.getText());
        holder.mDateTv.setText(memory.getYear()+"年"+memory.getMonth()+"月"+memory.getDay()+"日");
        holder.mCountTv.setText(memory.getCount()+"");

        switch (getItemViewType(position)) {
            case NOT_ARRIVED:
                holder.mArrivedTv.setText("还有");
                holder.mArrivedTv.setTextColor(mConext.getResources().getColor(R.color.theme_orange));
                holder.mArrivedIv.setBackgroundResource(R.drawable.not_arrived);
                break;
        }
        return view;
    }

    class ViewHolder {
        TextView mTextTv;
        TextView mDateTv;
        TextView mArrivedTv;
        TextView mCountTv;
        ImageView mArrivedIv;

    }
}