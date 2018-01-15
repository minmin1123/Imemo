package com.minmin.imemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.adapter.MemoryListAdapter;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memory;
import com.minmin.imemo.swipemenulistview.SwipeMenu;
import com.minmin.imemo.swipemenulistview.SwipeMenuCreator;
import com.minmin.imemo.swipemenulistview.SwipeMenuItem;
import com.minmin.imemo.swipemenulistview.SwipeMenuListView;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MemoryListManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2018/01/14
 *   desc:纪念日主界面
 *   version:1.0
 * </pre>
 */

public class MemoryMainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mAddIv;

    private TextView mNothingTv;

    private SwipeMenuListView mListLv;

    private List<Memory> mMemoryList = new ArrayList<>();

    private MemoryListAdapter mMemoryListAdapter;

    private MemoDatabase mMemoryDatabase = MemoDatabase.getInstance(this);

    private final static int REQUESTCODE_MAIN = 1;

    private final static int RESULTCODE_EDIT = 2;

    private final static int RESULTCODE_UPDATE = 3;

    private final static String RETURN_NEWMEMORY = "newMemory";

    private final static String RETURN_OLDMEMORY = "oldMemory";

    private final static String RETURN_UPDATEMEMORY = "updateMemory";

    private final static String RETURN_MEMORY = "memory";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_memory);

        initView();

    }

    //初始化页面
    public void initView() {
        mAddIv = findViewById(R.id.addIv);
        mNothingTv = findViewById(R.id.nothingTv);
        mListLv = findViewById(R.id.listLv);
        mAddIv.setOnClickListener(this);
        mListLv.setOnItemClickListener(this);
        initList();
        mMemoryListAdapter = new MemoryListAdapter(this, R.layout.item_memory, mMemoryList);
        mListLv.setAdapter(mMemoryListAdapter);

        //侧滑删除菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
                item.setBackground(R.color.red);
                item.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
                        getResources().getDisplayMetrics()));
                item.setIcon(R.drawable.delete);
                menu.addMenuItem(item);

            }
        };
        mListLv.setMenuCreator(creator);
        mListLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Memory deleteMemory = mMemoryList.get(position);
                switch (index) {
                    case 0: //点击了删除键
                        mMemoryList = MemoryListManager.deleteMemory(mMemoryList, deleteMemory);
                        MemoDatabase.getInstance(MemoryMainActivity.this).deleteMemory(deleteMemory);
                        Toast.makeText(MemoryMainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                        mMemoryListAdapter.notifyDataSetChanged();
                        if(mMemoryList.size()==0){
                            mNothingTv.setVisibility(View.VISIBLE);
                            mListLv.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了“添加”图标
            case R.id.addIv:
                Intent toEditMemoryIntent = new Intent(MemoryMainActivity.this, EditMemoryActivity.class);
                startActivityForResult(toEditMemoryIntent, REQUESTCODE_MAIN);
                break;
        }
    }

    //初始化dataList
    public void initList() {
        mMemoryList = mMemoryDatabase.quaryAllMemoryList();
        if (mMemoryList.size() != 0) {
            mNothingTv.setVisibility(View.GONE);
            mListLv.setVisibility(View.VISIBLE);
            for (Memory memory : mMemoryList) {
                String year = memory.getYear();
                String month = memory.getMonth();
                String day = memory.getDay();
                memory.setIs_arrived(DateUtils.isArrived(year + month + day) ? 1 : 0);
                memory.setCount(DateUtils.countSpanDays(year, month, day));
            }
        }
    }

    //ListView的子项点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

        Intent toCheckMemoryIntent = new Intent(MemoryMainActivity.this, CheckMemoryActivity.class);
        Memory memory = mMemoryList.get(position);
        toCheckMemoryIntent.putExtra(RETURN_MEMORY, memory);
        startActivityForResult(toCheckMemoryIntent, REQUESTCODE_MAIN);

    }

    //添加、查看纪念日
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_EDIT) {
            if(mNothingTv.getVisibility()==View.VISIBLE){
                mNothingTv.setVisibility(View.GONE);
                mListLv.setVisibility(View.VISIBLE);
            }
            Memory newMemory = (Memory) data.getSerializableExtra(RETURN_NEWMEMORY);
            mMemoryList = MemoryListManager.insertMemory(mMemoryList, newMemory);
            MemoDatabase.getInstance(this).insertMemory(newMemory);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            mMemoryListAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_UPDATE) {
            Memory updateMemo = (Memory) data.getSerializableExtra(RETURN_UPDATEMEMORY);
            Memory oldMemo = (Memory) data.getSerializableExtra(RETURN_OLDMEMORY);
            mMemoryList = MemoryListManager.deleteMemory(mMemoryList, oldMemo);
            mMemoryList = MemoryListManager.insertMemory(mMemoryList, updateMemo);
            MemoDatabase.getInstance(this).updateMemory(oldMemo, updateMemo);
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            mMemoryListAdapter.notifyDataSetChanged();

        }
    }
}
