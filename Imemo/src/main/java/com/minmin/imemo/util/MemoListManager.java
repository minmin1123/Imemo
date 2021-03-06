package com.minmin.imemo.util;

import com.minmin.imemo.model.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2017/10/15
 * desc:操作memolist的管理类
 * version:1.0
 */

public class MemoListManager {

    private final static int TYPE_DATE = 1;

    private final static int TYPE_PAPER = 2;

    private static List<Memo> mMemoWithTitleList = new ArrayList<>();

    //向dataList增添一条memo
    public static List<Memo> insertMemo(List<Memo> mMemoList, Memo memo) {
        int index = -1;
        for (int i = 0; i < mMemoList.size(); i++) {
            //如果比前者大后者小记录后者位置
            if (mMemoList.get(i).getId().compareTo(memo.getId()) > 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            mMemoList.add(index, memo);
        } else {
            mMemoList.add(memo);
        }
        return mMemoList;
    }

    //从dataList删除一条memo
    public static List<Memo> deleteMemo(List<Memo> mMemoList, Memo memo) {
        for (Memo eachMemo : mMemoList) {
            if (eachMemo.getId().equals(memo.getId())) {
                mMemoList.remove(eachMemo);
                break;
            }
        }
        return mMemoList;
    }

    //将纯净的memolist转化成不纯净的memowithtitlelist
    public static List<Memo> addDateTitle(List<Memo> mMemoList) {
        mMemoWithTitleList.clear();
        mMemoWithTitleList.addAll(mMemoList);
        String cursor = null;
        for (int i = 0; i < mMemoWithTitleList.size(); i++) {
            if (!mMemoWithTitleList.get(i).getDay().equals(cursor)) {
                Memo title = new Memo();
                title.setType(TYPE_DATE);
                title.setYear(mMemoWithTitleList.get(i).getYear());
                title.setMonth(mMemoWithTitleList.get(i).getMonth());
                title.setDay(mMemoWithTitleList.get(i).getDay());
                title.setWeek(mMemoWithTitleList.get(i).getWeek());
                cursor = mMemoWithTitleList.get(i).getDay();
                mMemoWithTitleList.add(i, title);
            }
        }
        return mMemoWithTitleList;
    }
}
