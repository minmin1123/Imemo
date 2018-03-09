package com.minmin.imemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minmin.imemo.R;
import com.minmin.imemo.model.Func;

import java.util.ArrayList;
import java.util.List;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2018/1/19
 *   desc:侧滑菜单栏适配器
 *   version:1.0
 */

public class FunctionListAdapter extends BaseAdapter {

    private final static int TYPE_HEAD=0;
    private final static int TYPE_FONT=1;

    private final static String HEAD="head";
    private final static String FONT="font";
    
    private Context mContext;
    
    private static List<Func> mFunctionList=new ArrayList<>();

    public FunctionListAdapter(@NonNull Context context, @NonNull List<Func> objects) {
        super();
        mContext = context;
        mFunctionList=objects;
    }

    @Override
    public int getCount() {
        return mFunctionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFunctionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFunctionList.get(position).getType().equals(HEAD)) {
            return TYPE_HEAD;
        } else{
            return TYPE_FONT;
        }
    }

    @Override//子item两种布局
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Func function = (Func) getItem(position);
        HeadViewHolder headViewHolder;
        final FontViewHolder fontViewHolder;
        switch (getItemViewType(position)) {
            case TYPE_HEAD:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_swipe_head, null);
                    headViewHolder = new HeadViewHolder();
                    headViewHolder.mHeadIv = convertView.findViewById(R.id.headIv);
                    convertView.setTag(headViewHolder);
                } else {
                    headViewHolder = (HeadViewHolder) convertView.getTag();
                }
                headViewHolder.mHeadIv.setImageBitmap(function.getBitmap());
                break;
            case TYPE_FONT:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_swipe_font, null);
                    fontViewHolder = new FontViewHolder();
                    fontViewHolder.mFontTv = convertView.findViewById(R.id.fontTv);
                    convertView.setTag(fontViewHolder);
                } else {
                    fontViewHolder = (FontViewHolder) convertView.getTag();
                }
                fontViewHolder.mFontTv.setText(function.getFont());
            default:
                break;
        }
        return convertView;
    }

    class HeadViewHolder {
        ImageView mHeadIv;
    }

    class FontViewHolder {
        TextView mFontTv;

    }
}
