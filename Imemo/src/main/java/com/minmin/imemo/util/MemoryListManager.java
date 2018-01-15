package com.minmin.imemo.util;

import com.minmin.imemo.model.Memory;

import java.util.List;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/15
 *   desc:操作memorylist的管理类
 *   version:1.0
 * </pre>
 */

public class MemoryListManager {
    

    //向dataList增添一条memory
    public static List<Memory> insertMemory(List<Memory> mMemoryList,Memory memory){
        int index = -1;
        for(int i=0;i<mMemoryList.size();i++){
            //如果比前者小后者大记录后者位置
            if(mMemoryList.get(i).getId().compareTo(memory.getId())<0){
                index=i;
                break;
            }
        }
        String year=memory.getYear();
        String month=memory.getMonth();
        String day=memory.getDay();
        memory.setIs_arrived(DateUtils.isArrived(year + month +day)?1:0);
        memory.setCount(DateUtils.countSpanDays(year,month,day));
        if(index!=-1){
            mMemoryList.add(index,memory);
        }else{
            mMemoryList.add(memory);
        }
        return mMemoryList;
    }

    //从dataList删除一条memory
    public static List<Memory> deleteMemory(List<Memory> mMemoryList,Memory memory) {
        for (Memory eachMemory : mMemoryList) {
            if (eachMemory.getId().equals(memory.getId())) {
                mMemoryList.remove(eachMemory);
                break;
            }
        }
        return mMemoryList;
    }
}
