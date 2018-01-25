package com.minmin.imemo.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.adapter.MemoryListAdapter;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memory;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MemoryListManager;
import com.minmin.imemo.util.Utils;
import com.minmin.imemo.view.swipemenulistview.SwipeMenu;
import com.minmin.imemo.view.swipemenulistview.SwipeMenuCreator;
import com.minmin.imemo.view.swipemenulistview.SwipeMenuItem;
import com.minmin.imemo.view.swipemenulistview.SwipeMenuListView;

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
public class MemoryMainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {

    private Toolbar mTitleTb;

    private ImageView mAddIv;

    private TextView mNothingTv;

    private SwipeMenuListView mListLv;

    private List<Memory> mMemoryList = new ArrayList<>();

    private MemoryListAdapter mMemoryListAdapter;

    private MemoDatabase mMemoryDatabase = MemoDatabase.getInstance(this);

    private boolean scrollFlag;

    private int mTouchSlop;

    private float mFirstY;

    private float mCurrentY;

    private boolean mShow = true;

    private ObjectAnimator mAnimator;

    private final static int REQUESTCODE_MAIN = 1;

    private final static int RESULTCODE_EDIT = 2;

    private final static int RESULTCODE_UPDATE = 3;

    private final static String RETURN_NEWMEMORY = "newMemory";

    private final static String RETURN_OLDMEMORY = "oldMemory";

    private final static String RETURN_UPDATEMEMORY = "updateMemory";

    private final static String RETURN_MEMORY = "memory_body";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_memory);
        initView();

    }

    //初始化页面
    public void initView() {
        mTitleTb = (Toolbar) findViewById(R.id.titleTb);
        setSupportActionBar(mTitleTb);
        getSupportActionBar().setTitle("");

        mAddIv = (ImageView) findViewById(R.id.addIv);
        mNothingTv = (TextView) findViewById(R.id.nothingTv);
        mListLv = (SwipeMenuListView) findViewById(R.id.listLv);
        mAddIv.setOnClickListener(this);
        mListLv.setOnItemClickListener(this);
        mListLv.setOnTouchListener(this);
        mListLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                //滑动到顶部
                if (i == 0) {
                    View firstVisibleItemView = mListLv.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        if (!mShow) {
                            animToolBar(1);
                            mShow = !mShow;
                        }
                    }
                }
            }
        });

        //使用一个HeaderView防止toolbar盖住第一个item
        View headView = new View(this);
        headView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.abc_action_bar_default_height_material)));//abc_action_bar_default_height_material属性获取系统actionBar的高度
        mListLv.addHeaderView(headView);

        //获取系统定义的最低滑动距离
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        //添加图标的旋转动画
        Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.add_rotate);
        mAddIv.startAnimation(rotateAnim);

        initList();
        mMemoryListAdapter = new MemoryListAdapter(this, R.layout.item_memory, mMemoryList);
        mListLv.setAdapter(mMemoryListAdapter);

        //创建侧滑删除菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
                item.setBackground(R.color.red);
                item.setWidth(Utils.dp2px(MemoryMainActivity.this, 60));
                item.setIcon(R.drawable.delete);
                menu.addMenuItem(item);

            }
        };
        mListLv.setMenuCreator(creator);
        mListLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final Memory deleteMemory = mMemoryList.get(position);
                switch (index) {
                    case 0:
                        //点击了删除键
                        mMemoryList = MemoryListManager.deleteMemory(mMemoryList, deleteMemory);
                        MemoDatabase.getInstance(MemoryMainActivity.this).deleteMemory(deleteMemory);
                        mMemoryListAdapter.notifyDataSetChanged();
                        Snackbar.make(mListLv, R.string.delete_success, Snackbar.LENGTH_SHORT)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (mNothingTv.getVisibility() == View.VISIBLE) {
                                            mNothingTv.setVisibility(View.GONE);
                                            mListLv.setVisibility(View.VISIBLE);
                                        }
                                        mMemoryList = MemoryListManager.insertMemory(mMemoryList, deleteMemory);
                                        MemoDatabase.getInstance(MemoryMainActivity.this).insertMemory(deleteMemory);
                                        mMemoryListAdapter.notifyDataSetChanged();
                                    }
                                }).show();
                        if (mMemoryList.size() == 0) {
                            mNothingTv.setVisibility(View.VISIBLE);
                            mListLv.setVisibility(View.GONE);
                        }
                        break;
                    default:
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
            default:
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

            if (mNothingTv.getVisibility() == View.VISIBLE) {
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

    //设置lstview的滑动监听时间，实现toolbar跟随listview运动
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentY = motionEvent.getY();
                if (mCurrentY - mFirstY > mTouchSlop) {
                    //手指向下滑动，隐藏toolbar
                    if (mShow) {
                        animToolBar(0);
                        mShow = !mShow;
                    }
                } else if (mFirstY - mCurrentY > mTouchSlop) {
                    //手指向上滑动，显示toolbar
                    if (!mShow) {
                        animToolBar(1);
                        mShow = !mShow;
                    }
                }
                if(mListLv.getFirstVisiblePosition()==0){
                    if (!mShow) {
                        animToolBar(1);
                        mShow = !mShow;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return false;
    }

    //控制toolbar显示或隐藏的动画
    private void animToolBar(int flag) {

        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (flag == 0) {
            mAnimator = ObjectAnimator.ofFloat(mTitleTb, "translationY", mTitleTb.getTranslationY(), -mTitleTb.getHeight());
        } else {
            mAnimator = ObjectAnimator.ofFloat(mTitleTb, "translationY", mTitleTb.getTranslationY(), 0);
        }
        mAnimator.start();
    }
}
