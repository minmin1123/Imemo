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
import android.widget.BaseAdapter;
import android.widget.Button;
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
 * Created by minmin on 2017/10/1.
 */

public class MemoListAdapter extends ArrayAdapter<Memo>{

    private int resourceId;
    private ViewHolder holder;
    private Context mContext;
    private List<Memo> memoList;
    private final int REQUESTCODE_MAIN = 1;//跳转页面请求code

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
//        Log.i("Main", "该item所处的的位置是："+String.valueOf(position));
        View view;
//        if(convertView==null){
//            Log.i("Main", "该item初次被记录");
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder=new ViewHolder();
            holder.item_tv_date=view.findViewById(R.id.item_tv_date);
            holder.item_tv_start_time=view.findViewById(R.id.item_tv_start_time);
            holder.item_tv_finish_time=view.findViewById(R.id.item_tv_finish_time);
            holder.item_tv_text=view.findViewById(R.id.item_tv_text);
            holder.item_rl_date=view.findViewById(R.id.item_rl_date);
            holder.item_rl_paper=view.findViewById(R.id.item_rl_paper);
            holder.item_iv_incomplete=view.findViewById(R.id.item_iv_incomplete);
            holder.item_iv_add=view.findViewById(R.id.item_iv_add);
            view.setTag(holder);
//        }else{
//            Log.i("Main", "该item已经存在");
//            view=convertView;
//            holder = (ViewHolder) view.getTag();
//        }
//        Log.i("Main", "该memo是不是第一个："+memo.getIs_first());
//        Log.i("Main", "该memo日期："+memo.getDay());
        //该项item是当日第一项->有日期头
        if(memo.getIs_first()==1){
            holder.item_tv_date.setText(memo.getMonth()+"月"+memo.getDay()+"日,"+memo.getWeek());
            holder.item_tv_start_time.setText(memo.getStart_hour()+":"+memo.getStart_minute());
            holder.item_tv_finish_time.setText(memo.getFinish_hour()+":"+memo.getFinish_minute());
            holder.item_tv_text.setText(memo.getText());
        }else{
            holder.item_rl_date.setVisibility(View.GONE);
            holder.item_tv_start_time.setText(memo.getStart_hour()+":"+memo.getStart_minute());
            holder.item_tv_finish_time.setText(memo.getFinish_hour()+":"+memo.getFinish_minute());
            holder.item_tv_text.setText(memo.getText());
        }
        //该项item已被完成->圆圈打勾
        if(memo.getIs_completed()==0){
            holder.item_iv_incomplete.setBackgroundResource(R.drawable.incomplete);
            holder.item_iv_incomplete.setTag("incomplete");
        }else{
            holder.item_iv_incomplete.setBackgroundResource(R.drawable.complete);
            holder.item_iv_incomplete.setTag("complete");
        }
        //该项item已被选中->背景由灰变为黄
        if(memo.getIs_chosen()==0){
            holder.item_tv_text.setBackgroundResource(R.drawable.paper);
            holder.item_tv_text.setTag("unselected");
        }else{
            holder.item_tv_text.setTag("selected");
            holder.item_tv_text.setBackgroundResource(R.drawable.paper_select);
        }
        //该项item是否被完成注册的监听事件
        holder.item_iv_incomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memo selected_memo=memoList.get(position);
                if(view.getTag().equals("incomplete")){
                    view.setTag("complete");
                    view.setBackgroundResource(R.drawable.complete);
                    Toast.makeText(mContext, "你很棒棒o~", Toast.LENGTH_SHORT).show();
                    MemoDatabase.getInstance(mContext).updateMemoCompleteStatus(selected_memo, 1);
                    selected_memo.setIs_completed(1);
                }else{
                    view.setTag("incomplete");
                    view.setBackgroundResource(R.drawable.incomplete);
                    Toast.makeText(mContext, "还要继续加油o~", Toast.LENGTH_SHORT).show();
                    MemoDatabase.getInstance(mContext).updateMemoCompleteStatus(selected_memo, 0);
                    selected_memo.setIs_completed(0);
                }
            }
        });
        //如果该项有日期头，对该项item是否点击了"+"新建新备忘录注册的监听事件，若是则跳转到edit活动
        holder.item_iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memo selected_memo=memoList.get(position);
                Intent intent_addSelectDayMemo = new Intent(mContext, EditMemoActivity.class);
                intent_addSelectDayMemo.putExtra("year", selected_memo.getYear());
                intent_addSelectDayMemo.putExtra("month", selected_memo.getMonth());
                intent_addSelectDayMemo.putExtra("day", selected_memo.getDay());
                intent_addSelectDayMemo.putExtra("week", selected_memo.getWeek());
                ((Activity)mContext).startActivityForResult(intent_addSelectDayMemo,REQUESTCODE_MAIN);
            }
        });
//        Log.i("Main","*********************");
        return view;
    }

    class ViewHolder{
        TextView item_tv_date;
        TextView item_tv_start_time;
        TextView item_tv_finish_time;
        TextView item_tv_text;
        RelativeLayout item_rl_date;
        RelativeLayout item_rl_paper;
        ImageView item_iv_incomplete;
        ImageView item_iv_add;

    }
}