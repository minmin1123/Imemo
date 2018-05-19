package com.minmin.imemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.activity.EditMemoActivity;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.service.RemindService;
import com.minmin.imemo.view.tickview.TickView;

import java.util.ArrayList;
import java.util.List;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2017/10/18
 * desc:有两种item布局的Adapter
 * version:1.0
 */

public class MemoWithDateListAdapter extends BaseAdapter {

    private final static int TYPE_DATE = 1;
    private final static int TYPE_PAPER = 2;
    private final static int DATE = 0;
    private final static int PAPER = 1;

    private Context mContext;

    private final int REQUESTCODE_MAIN = 1;//跳转页面请求code

    private final String UNSELECTED = "unselected";
    private final String SELECTED = "selected";
    private final String INCOMPLETE = "incomplete";
    private final String COMPLETE = "complete";
    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String WEEK = "week";
    private final static String NOTREMIND = "notremind";
    private final static String REMIND = "remind";

    private static List<Memo> mMemoWithTitleList = new ArrayList<>();

    public MemoWithDateListAdapter(@NonNull Context context, @NonNull List<Memo> objects) {
        super();
        mContext = context;
        mMemoWithTitleList = objects;
    }

    @Override
    public int getCount() {
        return mMemoWithTitleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemoWithTitleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMemoWithTitleList.get(position).getType() == TYPE_DATE) {
            return DATE;
        } else {
            return PAPER;
        }
    }

    @Override//子item两种布局
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Memo memo = (Memo) getItem(position);
        DateViewHolder dateViewHolder;
        final PaperViewHolder paperViewHolder;
        switch (getItemViewType(position)) {
            case DATE://如果类型是时间标题
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_memo_date, null);
                    dateViewHolder = new DateViewHolder();
                    dateViewHolder.mDateTv = convertView.findViewById(R.id.dateTv);
                    dateViewHolder.mAddIv = convertView.findViewById(R.id.addIv);
                    convertView.setTag(dateViewHolder);
                } else {
                    dateViewHolder = (DateViewHolder) convertView.getTag();
                }
                dateViewHolder.mDateTv.setText(memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
                dateViewHolder.mAddIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Memo selectMemo = mMemoWithTitleList.get(position);
                        Intent addSelectDayMemoIntent = new Intent(mContext, EditMemoActivity.class);
                        addSelectDayMemoIntent.putExtra(YEAR, selectMemo.getYear());
                        addSelectDayMemoIntent.putExtra(MONTH, selectMemo.getMonth());
                        addSelectDayMemoIntent.putExtra(DAY, selectMemo.getDay());
                        addSelectDayMemoIntent.putExtra(WEEK, selectMemo.getWeek());
                        ((Activity) mContext).startActivityForResult(addSelectDayMemoIntent, REQUESTCODE_MAIN);
                    }
                });
                break;
            case PAPER://如果类型是纸片
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_memo_paper, null);
                    paperViewHolder = new PaperViewHolder();
                    paperViewHolder.mStartTimeTv = convertView.findViewById(R.id.startTimeTv);
                    paperViewHolder.mFinishTimeTv = convertView.findViewById(R.id.finishTimeTv);
                    paperViewHolder.mContextTv = convertView.findViewById(R.id.contextTv);
                    paperViewHolder.mPaperRl = convertView.findViewById(R.id.mPaperRl);
//                    paperViewHolder.mCompleteIv = convertView.findViewById(R.id.completeIv);
                    paperViewHolder.mTickView = convertView.findViewById(R.id.completeTv);
                    paperViewHolder.mMarkIv = convertView.findViewById(R.id.markIv);
                    convertView.setTag(paperViewHolder);
                } else {
                    paperViewHolder = (PaperViewHolder) convertView.getTag();
                }
                paperViewHolder.mStartTimeTv.setText(memo.getStart_hour() + ":" + memo.getStart_minute());
                paperViewHolder.mFinishTimeTv.setText(memo.getFinish_hour() + ":" + memo.getFinish_minute());
                paperViewHolder.mContextTv.setText(memo.getText());
                //该项是否被标注提醒
                if (memo.getIs_remind() == 0) {
                    paperViewHolder.mMarkIv.setTag(NOTREMIND);
                    paperViewHolder.mMarkIv.setVisibility(View.INVISIBLE);
                } else {
                    paperViewHolder.mMarkIv.setTag(REMIND);
                    paperViewHolder.mMarkIv.setVisibility(View.VISIBLE);
                }
                //该项item已被完成->圆圈打勾
                if (memo.getIs_completed() == 0) {
                    paperViewHolder.mTickView.setChecked(false);
                    paperViewHolder.mTickView.setTag(INCOMPLETE);

                } else {
                    paperViewHolder.mTickView.setChecked(true);
                    paperViewHolder.mTickView.setTag(COMPLETE);
                }
//                if (memo.getIs_completed() == 0) {
//                    paperViewHolder.mCompleteIv.setBackgroundResource(R.drawable.incomplete);
//                    paperViewHolder.mCompleteIv.setTag(INCOMPLETE);
//                } else {
//                    paperViewHolder.mCompleteIv.setBackgroundResource(R.drawable.complete);
//                    paperViewHolder.mCompleteIv.setTag(COMPLETE);
//                }
                //该项item已被选中->背景由灰变为黄
                if (memo.getIs_chosen() == 0) {
                    paperViewHolder.mContextTv.setBackgroundResource(R.drawable.paper_not_select);
                    paperViewHolder.mContextTv.setTag(UNSELECTED);
                } else {
                    paperViewHolder.mContextTv.setTag(SELECTED);
                    paperViewHolder.mContextTv.setBackgroundResource(R.drawable.paper_select);
                }

                //该项item是否被完成注册的监听事件
                paperViewHolder.mTickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Memo selectMemo = mMemoWithTitleList.get(position);
                        if (view.getTag().equals(INCOMPLETE)) {
                            view.setTag(COMPLETE);
                            paperViewHolder.mTickView.setChecked(true);
                            Toast.makeText(mContext, R.string.complete, Toast.LENGTH_SHORT).show();
                            MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 1);
                            selectMemo.setIs_completed(1);
                            Intent remindServiceIntent = new Intent((mContext), RemindService.class);
                            ((Activity) mContext).startService(remindServiceIntent);
                        } else {
                            view.setTag(INCOMPLETE);
                            paperViewHolder.mTickView.setChecked(false);
                            Toast.makeText(mContext, R.string.incomplete, Toast.LENGTH_SHORT).show();
                            MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 0);
                            selectMemo.setIs_completed(0);
                            Intent remindServiceIntent = new Intent(((Activity) mContext), RemindService.class);
                            ((Activity) mContext).startService(remindServiceIntent);
                        }
                    }
                });
//                paperViewHolder.mCompleteIv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Memo selectMemo = mMemoWithTitleList.get(position);
//                        if (view.getTag().equals(INCOMPLETE)) {
//                            view.setTag(COMPLETE);
//                            view.setBackgroundResource(R.drawable.complete);
//                            Toast.makeText(mContext, R.string.complete, Toast.LENGTH_SHORT).show();
//                            MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 1);
//                            selectMemo.setIs_completed(1);
//                            Intent remindServiceIntent = new Intent(((Activity) mContext), RemindService.class);
//                            ((Activity) mContext).startService(remindServiceIntent);
//                        } else {
//                            view.setTag(INCOMPLETE);
//                            view.setBackgroundResource(R.drawable.incomplete);
//                            Toast.makeText(mContext,R.string.incomplete, Toast.LENGTH_SHORT).show();
//                            MemoDatabase.getInstance().updateMemoCompleteStatus(selectMemo, 0);
//                            selectMemo.setIs_completed(0);
//                            Intent remindServiceIntent = new Intent(((Activity) mContext), RemindService.class);
//                            ((Activity) mContext).startService(remindServiceIntent);
//                        }
//                    }
//                });
                break;
            default:
                break;
        }
        return convertView;
    }

    class DateViewHolder {
        TextView mDateTv;
        ImageView mAddIv;
    }

    class PaperViewHolder {
        TextView mStartTimeTv;
        TextView mFinishTimeTv;
        TextView mContextTv;
        RelativeLayout mPaperRl;
        //        ImageView mCompleteIv;
        TickView mTickView;
        ImageView mMarkIv;

    }
}
