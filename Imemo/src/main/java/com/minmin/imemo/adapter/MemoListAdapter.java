package com.minmin.imemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.activity.EditMemoActivity;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;

import java.util.List;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:通过隐藏实现双布局并存的Adapter
 *   version:1.0
 */

public class MemoListAdapter extends ArrayAdapter<Memo> {

    private int resourceId;

    private ViewHolder holder;

    private Context mContext;

    private List<Memo> memoList;

    private final int REQUESTCODE_MAIN = 1;//跳转页面请求code

    private final String UNSELECTED="unselected";
    private final String SELECTED="selected";
    private final String INCOMPLETE = "incomplete";
    private final String COMPLETE = "complete";
    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String WEEK = "week";

    public MemoListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Memo> objects) {
        super(context, resource, objects);
        resourceId = resource;//获取子项布局id
        mContext = context;//获取上下文
        memoList = objects;//获取当前数据源dataList
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Memo memo = getItem(position);
        Log.i("Main", "该item所处的的位置是："+String.valueOf(position));
        View view;
        if(convertView==null){
            Log.i("Main", "该item初次被记录");
        view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        holder = new ViewHolder();
        holder.mDateTv = view.findViewById(R.id.dateTv);
        holder.mStartTimeTv = view.findViewById(R.id.startTimeTv);
        holder.mFinishTimeTv = view.findViewById(R.id.finishTimeTv);
        holder.mContextTv = view.findViewById(R.id.contextTv);
        holder.mDateRl = view.findViewById(R.id.dateRl);
        holder.mPaperRl = view.findViewById(R.id.mPaperRl);
        holder.mCompleteIv = view.findViewById(R.id.completeIv);
        holder.mAddIv = view.findViewById(R.id.addIv);
        view.setTag(holder);
        }else{
            view=convertView;
            holder = (ViewHolder) view.getTag();
        }
//        Log.i("Main", "该memo是不是第一个："+memo.getIs_first());
        //该项item是当日第一项->有日期头
//        if (memo.getIs_first() == 1) {
//            holder.mDateTv.setText(memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
//            holder.mStartTimeTv.setText(memo.getStart_hour() + ":" + memo.getStart_minute());
//            holder.mFinishTimeTv.setText(memo.getFinish_hour() + ":" + memo.getFinish_minute());
//            holder.mContextTv.setText(memo.getText());
//        } else {
//            holder.mDateRl.setVisibility(View.GONE);
//            holder.mStartTimeTv.setText(memo.getStart_hour() + ":" + memo.getStart_minute());
//            holder.mFinishTimeTv.setText(memo.getFinish_hour() + ":" + memo.getFinish_minute());
//            holder.mContextTv.setText(memo.getText());
//        }
        //该项item已被完成->圆圈打勾
        if (memo.getIs_completed() == 0) {
            holder.mCompleteIv.setBackgroundResource(R.drawable.incomplete);
            holder.mCompleteIv.setTag(INCOMPLETE);
        } else {
            holder.mCompleteIv.setBackgroundResource(R.drawable.complete);
            holder.mCompleteIv.setTag(COMPLETE);
        }
        //该项item已被选中->背景由灰变为黄
        if (memo.getIs_chosen() == 0) {
            holder.mContextTv.setBackgroundResource(R.drawable.paper_not_select);
            holder.mContextTv.setTag(UNSELECTED);
        } else {
            holder.mContextTv.setTag(SELECTED);
            holder.mContextTv.setBackgroundResource(R.drawable.paper_select);
        }
        //该项item是否被完成注册的监听事件
        holder.mCompleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memo selectMemo = memoList.get(position);
                if (view.getTag().equals(INCOMPLETE)) {
                    view.setTag(COMPLETE);
                    view.setBackgroundResource(R.drawable.complete);
                    Toast.makeText(mContext, R.string.complete, Toast.LENGTH_SHORT).show();
                    MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 1);
                    selectMemo.setIs_completed(1);
                } else {
                    view.setTag(INCOMPLETE);
                    view.setBackgroundResource(R.drawable.incomplete);
                    Toast.makeText(mContext,R.string.incomplete, Toast.LENGTH_SHORT).show();
                    MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 0);
                    selectMemo.setIs_completed(0);
                }
            }
        });
        //如果该项有日期头，对该项item是否点击了"+"新建新备忘录注册的监听事件，若是则跳转到edit活动
        holder.mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memo selectMemo = memoList.get(position);
                Intent addSelectDayMemoIntent = new Intent(mContext, EditMemoActivity.class);
                addSelectDayMemoIntent.putExtra(YEAR, selectMemo.getYear());
                addSelectDayMemoIntent.putExtra(MONTH, selectMemo.getMonth());
                addSelectDayMemoIntent.putExtra(DAY, selectMemo.getDay());
                addSelectDayMemoIntent.putExtra(WEEK,selectMemo.getWeek());
                ((Activity) mContext).startActivityForResult(addSelectDayMemoIntent, REQUESTCODE_MAIN);
            }
        });
//        Log.i("Main","*********************");
        return view;
    }

    class ViewHolder {
        TextView mDateTv;
        TextView mStartTimeTv;
        TextView mFinishTimeTv;
        TextView mContextTv;
        RelativeLayout mDateRl;
        RelativeLayout mPaperRl;
        ImageView mCompleteIv;
        ImageView mAddIv;

    }
}