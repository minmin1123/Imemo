package com.minmin.imemo.util;

import com.minmin.imemo.model.Memo;

import java.util.List;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/15
 *   desc:
 *   version:1.0
 * </pre>
 */

public class MemoListManager {

    //向dataList增添一条memo
    public static List<Memo> insertMemo(List<Memo> mMemoList,Memo memo){
        int index = -1;
        for(int i=0;i<mMemoList.size();i++){
            //如果比前者大后者小记录后者位置
            if(mMemoList.get(i).getId().compareTo(memo.getId())==1){
                index=i;
                break;
            }
        }
        if(index!=-1){
            mMemoList.add(index,memo);
        }else{
            mMemoList.add(memo);
        }
        //每插入一条memo就对dataList的Is_first进行一次重新赋值
        String cursor=null;
        for(Memo eachMemo:mMemoList){
            if(!eachMemo.getDay().equals(cursor)){
                eachMemo.setIs_first(1);
                cursor = eachMemo.getDay();
            }else{
                eachMemo.setIs_first(0);
            }
        }
        return mMemoList;
    }

    //从dataList删除一条memo
    public static List<Memo> deleteMemo(List<Memo> mMemoList,Memo memo){
        for(Memo eachMemo:mMemoList){
            if(eachMemo.getId().equals(memo.getId())){
                mMemoList.remove(eachMemo);
                break;
            }
        }
        //每删除一条memo就对dataList的Is_first进行一次重新赋值
        String cursor=null;
        for(Memo eachMemo:mMemoList){
            if(!eachMemo.getDay().equals(cursor)){
                eachMemo.setIs_first(1);
                cursor = eachMemo.getDay();
            }else{
                eachMemo.setIs_first(0);
            }
        }
        return mMemoList;
    }
}
