package com.minmin.imemo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minmin.imemo.R;
import com.minmin.imemo.adapter.MemoWithDateListAdapter;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.service.RemindService;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MemoListManager;
import com.minmin.imemo.util.MyCalendar;
import com.minmin.imemo.util.MyThreadPoolExecutor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:主界面
 *   version:1.0
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView mAddAnyIv;

    private TextView mDateTv;

    private ImageView mLeftIv;

    private ImageView mRightIv;

    private ImageView mMoreIv;

    private TextView mNothingTv;

    private ListView mListLv;

    private TextView mChosenTv;

    private ImageView mDealIv;

    private NavigationView mViewNv;

    private ImageView mHeadIv;

    private SwipeRefreshLayout mRefreshSl;

    private File outputImage;//创建File对象，用于存储拍照后的图片

    private Uri imageUri;//选中图片的uri

    private String mSelectedYear = MyCalendar.getNow_year();

    private String mSelectedMonth = MyCalendar.getNow_month();

    private MemoDatabase mMemoDatabase = MemoDatabase.getInstance();

    private List<Memo> mMemoList = new ArrayList<>();

    private List<Memo> mMemoWithTitleList = new ArrayList<>();

    private List<Memo> mSelectedMemoList = new ArrayList<>();

    private MemoWithDateListAdapter mMemoListAdapter;

    private String[] mMoreList;

    private String[] mChooseList;

//    private List<Func> functionList = new ArrayList<>();

//    private FunctionListAdapter mFunctionListAdapter;

    private final static int REQUESTCODE_MAIN = 1;
    private final static int RESULTCODE_EDIT = 2;
    private final static int RESULTCODE_DELETE = 3;
    private final static int RESULTCODE_UPDATE = 4;
    private final static int LEFT = 5;
    private final static int RIGHT = 6;
    private final static int TYPE_DATE = 1;
    private final static int TYPE_PAPER = 2;

    private String mCopyYear = mSelectedYear;

    private String mCopyMonth = mSelectedMonth;

    private String mCopyDay = MyCalendar.getNow_day();

    private String searchYear = mSelectedYear;

    private String searchMonth = mSelectedMonth;

    private String searchDay = MyCalendar.getNow_day();

    private int SEARCH_STATUS = 0;
    private int MORE_STATUS = 0;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO = 3;

    private final static String DELETE = "delete";
    private final static String COPY = "copy";
    private final static String UNSELECTED = "unselected";
    private final static String SELECTED = "selected";
    private final static String RETURN_MEMO = "memo";
    private final static String RETURN_NEWMEMO = "newMemo";
    private final static String RETURN_OLDMEMO = "oldMemo";
    private final static String RETURN_UPDATEMEMO = "updateMemo";
    private final static String HEAD = "head";
    private final static String FONT = "font";
    private final static String OUTPUT_IMAGE = "output_image.jpg";
    public static final String HEADPORTRAITNAME = "HEADPORTRAITNAME";

    private AlertDialog.Builder mMoreMemoBuilder;

    private AlertDialog.Builder mSearchSelectDayMemoBuilder;

    private AlertDialog.Builder mSelectDayBuilder;

    private AlertDialog.Builder mIsDeleteMemoBuilder;

    private AlertDialog.Builder mIsCopyMemoBuilder;

    private AlertDialog.Builder mGetBitmapBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initList();
        initView();
        updateMemoList();
        startRemindService();
    }

    //开启前台服务
    public void startRemindService() {
        Intent remindServiceIntent = new Intent(this, RemindService.class);
        startService(remindServiceIntent);
    }

    //dataList数据初始化
    public void initList() {

        mMoreList = new String[]{getResources().getString(R.string.bulk_delete), getResources().getString(R.string.bulk_copy)};
        mChooseList = new String[]{getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_album)};

        mMemoList = mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth);
        mMemoListAdapter = new MemoWithDateListAdapter(this, mMemoWithTitleList);
//        functionList.add(new Func(HEAD, mSwipeHeadBitmap));
//        functionList.add(new Func(FONT, getResources().getString(R.string.memory)));
//        mFunctionListAdapter = new FunctionListAdapter(this, functionList);

    }

    //页面初始化
    public void initView() {
        mAddAnyIv = findViewById(R.id.addAnyIv);
        mDateTv = findViewById(R.id.dateTv);
        mNothingTv = findViewById(R.id.nothingTv);
        mLeftIv = findViewById(R.id.leftIv);
        mRightIv = findViewById(R.id.rightIv);
        mMoreIv = findViewById(R.id.moreIv);
        mListLv = findViewById(R.id.listLv);
        mChosenTv = findViewById(R.id.chosenTv);
        mDealIv = findViewById(R.id.dealIv);
//        mListMu = findViewById(R.id.listMu);
        mViewNv = findViewById(R.id.viewNv);
        mRefreshSl = findViewById(R.id.refreshSl);

        mRefreshSl.setColorSchemeResources(R.color.theme_orange);
        mRefreshSl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshMemolist();
            }
        });

        mAddAnyIv.setOnClickListener(this);
        mLeftIv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mMoreIv.setOnClickListener(this);
        mDateTv.setOnClickListener(this);
        mDealIv.setOnClickListener(this);

        mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.add_rotate);
        mAddAnyIv.startAnimation(rotateAnim);

        mListLv.setAdapter(mMemoListAdapter);
        mListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickMemo(view, i);
            }
        });

        mViewNv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //NavigationView菜单项选中事件
                switch (item.getItemId()) {
                    case R.id.memory:
                        Intent toMemoryIntent = new Intent(MainActivity.this, MemoryMainActivity.class);
                        startActivity(toMemoryIntent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        Bitmap mSwipeHeadBitmap= null;
        try {
            mSwipeHeadBitmap = BitmapFactory.decodeStream(openFileInput(HEADPORTRAITNAME));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //获取滑动菜单NavigationView的头布局
        View headView = mViewNv.getHeaderView(0);
        mHeadIv = headView.findViewById(R.id.headIv);
        if (mSwipeHeadBitmap == null) {
            mSwipeHeadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
        }
        showBitmap(mSwipeHeadBitmap);
        mHeadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGetBitmapBuilder == null) {
                    mGetBitmapBuilder = new AlertDialog.Builder(MainActivity.this);
                    mGetBitmapBuilder.setItems(mChooseList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {

                            if (position == 0) {
                                chooseTakePhoto();
                            } else {
                                chooseFromAlbum();

                            }
                        }
                    });
                }
                mGetBitmapBuilder.create().show();
            }
        });
//        mListMu.setAdapter(mFunctionListAdapter);
//        mListMu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                clickFuntion(i);
//            }
//        });
    }

    //点击了多功能菜单栏
//    public void clickFuntion(int position) {
//
//        if (functionList.get(position).getFont().equals(getResources().getString(R.string.memory))) {
//            Intent toMemoryIntent = new Intent(MainActivity.this, MemoryMainActivity.class);
//            startActivity(toMemoryIntent);
//        }else{
//
//            if (mGetBitmapBuilder == null) {
//                mGetBitmapBuilder = new AlertDialog.Builder(this);
//                mGetBitmapBuilder.setItems(mChooseList, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int position) {
//
//                        if (position == 0) {
//                            chooseTakePhoto();
//                        } else{
//                            chooseFromAlbum();
//
//                        }
//                    }
//                });
//            }
//            mGetBitmapBuilder.create().show();
//
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了大"+"，新建一条任意日期的备忘录
            case R.id.addAnyIv:
                Intent toEditMemoIntent = new Intent(MainActivity.this, EditMemoActivity.class);
                startActivityForResult(toEditMemoIntent, REQUESTCODE_MAIN);
                break;
            //点击了标题日期,通过日历选择器搜索特定时间的备忘录
            case R.id.dateTv:
                showDatePickerDialog();
                break;
            //点击了查看前一个月备忘录记录
            case R.id.leftIv:
                checkOntherMonthMemo(LEFT);
                break;
            //点击了查看后一个月备忘录记录
            case R.id.rightIv:
                checkOntherMonthMemo(RIGHT);
                break;
            //点击了更多（批量删除、批量复制）
            case R.id.moreIv:
                showMoreDialog();
                break;
            //点击了处理（批量删除、批量复制）
            case R.id.dealIv:
                checkMoreSelect();
                break;
            default:
                break;
        }
    }


    public void showBitmap(Bitmap mSwipeHeadBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mSwipeHeadBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(this).load(bytes).into(mHeadIv);
    }


    //刷新memolist，是否有不同步现象
    public void refreshMemolist() {

        //创建自定义的线程池对象
        MyThreadPoolExecutor myExecutor=MyThreadPoolExecutor.getInstance();
        //向线程池中提交线程
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (Memo memo : mMemoList) {
                        if (memo.getIs_chosen() == 1) {
                            mMemoDatabase.updateMemoChosenStatus(memo, 0);
                            memo.setIs_chosen(0);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMemoList();
                        mRefreshSl.setRefreshing(false);
                    }
                });
            }
        });
        //待线程池以及缓存队列中所有的线程任务完成后关闭线程池。
        myExecutor.shutdown();

    }

    //弹出日历选择器
    public void showDatePickerDialog() {
        mSearchSelectDayMemoBuilder = new AlertDialog.Builder(this);
        mSearchSelectDayMemoBuilder.setTitle(R.string.search_date);
        mSearchSelectDayMemoBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SEARCH_STATUS = 1;
                mSelectedMonth = searchMonth;
                mSelectedYear = searchYear;
                mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
                mMemoList = mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth);
                updateMemoList();
            }
        });
        mSearchSelectDayMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.edit_dialog, null);
        DatePicker dialogDp = view.findViewById(R.id.dialogDp);
//        final TextView weekTv = view.findViewById(R.id.weekTv);
//        weekTv.setText(DateUtils.getSelectedWeek(Integer.parseInt(searchYear), Integer.parseInt(searchMonth) - 1, Integer.parseInt(searchDay)));
        dialogDp.init(Integer.parseInt(searchYear), Integer.parseInt(searchMonth) - 1, Integer.parseInt(searchDay), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                searchYear = String.valueOf(year);
                searchMonth = DateUtils.toNormalTime(month + 1);
                searchDay = DateUtils.toNormalTime(day);
//                weekTv.setText(DateUtils.getSelectedWeek(Integer.parseInt(searchYear), Integer.parseInt(searchMonth) - 1, Integer.parseInt(searchDay)));

            }
        });
        mSearchSelectDayMemoBuilder.setView(view);
        mSearchSelectDayMemoBuilder.create().show();
    }

    //查看不同月份的memoList
    public void checkOntherMonthMemo(int flag) {
        if (flag == LEFT) {
            if (Integer.parseInt(mSelectedMonth) == 1) {
                mSelectedMonth = "12";
                mSelectedYear = Integer.parseInt(mSelectedYear) - 1 + "";
            } else {
                mSelectedMonth = DateUtils.toNormalTime(Integer.parseInt(mSelectedMonth) - 1);
            }
            mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        } else if (flag == RIGHT) {
            if (Integer.parseInt(mSelectedMonth) == 12) {
                mSelectedMonth = "01";
                mSelectedYear = Integer.parseInt(mSelectedYear) + 1 + "";
            } else {
                mSelectedMonth = DateUtils.toNormalTime(Integer.parseInt(mSelectedMonth) + 1);
            }
            mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        }
        mMemoList = mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth);
        updateMemoList();
    }

    @Override//新建、更改、删除三种操作对dataList的更改
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_EDIT) {
            Memo newMemo = (Memo) data.getSerializableExtra(RETURN_NEWMEMO);
            if (newMemo.getYear().equals(mSelectedYear) && newMemo.getMonth().equals(mSelectedMonth)) {
                mMemoList = MemoListManager.insertMemo(mMemoList, newMemo);

            }
            mMemoDatabase.insertMemo(newMemo);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
            startRemindService();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_UPDATE) {
            Memo updateMemo = (Memo) data.getSerializableExtra(RETURN_UPDATEMEMO);
            Memo oldMemo = (Memo) data.getSerializableExtra(RETURN_OLDMEMO);
            mMemoList = MemoListManager.deleteMemo(mMemoList, oldMemo);
            if (updateMemo.getYear().equals(mSelectedYear) && updateMemo.getMonth().equals(mSelectedMonth)) {
                mMemoList = MemoListManager.insertMemo(mMemoList, updateMemo);
            }
            mMemoDatabase.updateMemo(oldMemo, updateMemo);
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
            startRemindService();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_DELETE) {
            Memo oldMemo = (Memo) data.getSerializableExtra(RETURN_OLDMEMO);
            mMemoList = MemoListManager.deleteMemo(mMemoList, oldMemo);
            mMemoDatabase.deleteMemo(oldMemo);
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
            startRemindService();
        }

        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                }
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Intent intent = new Intent(MainActivity.this, CropActivity.class);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.toString());
                        startActivityForResult(intent, CROP_PHOTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_PHOTO:
                Bitmap mSwipeHeadBitmap=null;
                try {
                    mSwipeHeadBitmap = BitmapFactory.decodeStream(openFileInput(HEADPORTRAITNAME));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                showBitmap(mSwipeHeadBitmap);
//                functionList.remove(0);
//                functionList.add(0,new Func(HEAD, mSwipeHeadBitmap));
//                mFunctionListAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    //更新当前显示的dataList
    public void updateMemoList() {
        mMemoWithTitleList.clear();
        mMemoWithTitleList.addAll(MemoListManager.addDateTitle(mMemoList));
        if (mMemoWithTitleList.size() != 0) {
            mNothingTv.setVisibility(View.GONE);
            mListLv.setVisibility(View.VISIBLE);
            mMemoListAdapter.notifyDataSetChanged();
            if (SEARCH_STATUS == 0) {
                if (MyCalendar.getNow_year().equals(mSelectedYear) && (MyCalendar.getNow_month().equals(mSelectedMonth))) {
                    int existTodayMemo = 0;
                    for (int i = 0; i < mMemoWithTitleList.size(); i++) {
                        if (mMemoWithTitleList.get(i).getDay().equals(MyCalendar.getNow_day())) {
                            existTodayMemo = 1;
                            mListLv.setSelection(i);
                            break;
                        }
                    }
                    if (existTodayMemo == 0) {
                        mListLv.setSelection(0);
                    }
                } else {
                    mListLv.setSelection(0);
                }
            } else {
                int existSearchDayMemo = 0;
                for (int i = 0; i < mMemoWithTitleList.size(); i++) {
                    if (mMemoWithTitleList.get(i).getDay().equals(searchDay)) {
                        existSearchDayMemo = 1;
                        mListLv.setSelection(i);
                        break;
                    }
                }
                if (existSearchDayMemo == 0) {
                    mListLv.setSelection(0);
                    Toast.makeText(MainActivity.this, R.string.search_none, Toast.LENGTH_SHORT).show();
                }
                SEARCH_STATUS = 0;
            }
        } else {
            mNothingTv.setVisibility(View.VISIBLE);
            mListLv.setVisibility(View.GONE);
            if (SEARCH_STATUS == 1) {
                Toast.makeText(MainActivity.this, R.string.search_none, Toast.LENGTH_SHORT).show();
                SEARCH_STATUS = 0;
            }
        }
    }

    //点击了备忘录列表
    public void clickMemo(View view, int position) {
        if (MORE_STATUS == 0) {
            //非更多（批量删除、批量复制）状态，点击后查看选定子项的备忘录详情
            if (mMemoWithTitleList.get(position).getType() == TYPE_PAPER) {
                Intent toCheckMemoIntent = new Intent(MainActivity.this, CheckMemoActivity.class);
                Memo memo = mMemoWithTitleList.get(position);
                toCheckMemoIntent.putExtra(RETURN_MEMO, memo);
                startActivityForResult(toCheckMemoIntent, REQUESTCODE_MAIN);
            }
        } else {
            //更多（批量删除、批量复制）状态，点击后变化是否选中
            if (mMemoWithTitleList.get(position).getType() == TYPE_PAPER) {
                final TextView mItemContextTv = view.findViewById(R.id.contextTv);
                if (mItemContextTv.getTag().equals(UNSELECTED)) {
                    mItemContextTv.setTag(SELECTED);
                    mItemContextTv.setBackgroundResource(R.drawable.paper_select);
                    mMemoWithTitleList.get(position).setIs_chosen(1);
                    mMemoDatabase.updateMemoChosenStatus(mMemoWithTitleList.get(position), 1);
                    mSelectedMemoList.add(mMemoWithTitleList.get(position));
                } else {
                    mItemContextTv.setTag(UNSELECTED);
                    mItemContextTv.setBackgroundResource(R.drawable.paper_not_select);
                    mMemoWithTitleList.get(position).setIs_chosen(0);
                    mMemoDatabase.updateMemoChosenStatus(mMemoWithTitleList.get(position), 0);
                    mSelectedMemoList.remove(mMemoWithTitleList.get(position));
                    for (Memo selectMemo : mSelectedMemoList) {
                        if (selectMemo.getId().equals(mMemoWithTitleList.get(position).getId())) {
                            mSelectedMemoList.remove(selectMemo);
                            break;
                        }
                    }
                }
                mChosenTv.setText("已选择" + mSelectedMemoList.size() + "项");
            }
        }
    }

    //选择拍照
    public void chooseTakePhoto() {

        outputImage = new File(getExternalCacheDir(), OUTPUT_IMAGE);
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.minmin.imemo.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    //选择相册
    public void chooseFromAlbum() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    //更多选项的对话框
    public void showMoreDialog() {
        if (mMoreMemoBuilder == null) {
            mMoreMemoBuilder = new AlertDialog.Builder(this);
            mMoreMemoBuilder.setItems(mMoreList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    MORE_STATUS = 1;
                    if (position == 0) {
                        toDeleteSelectItem();
                    } else if (position == 1) {
                        toCopySelectItem();
                    }
                }
            });
        }
        mMoreMemoBuilder.create().show();
    }

    //批量删除状态
    public void toDeleteSelectItem() {
        mChosenTv.setVisibility(View.VISIBLE);
        mDealIv.setVisibility(View.VISIBLE);
        mDealIv.setBackgroundResource(R.drawable.delete);
        mDealIv.setTag(DELETE);
        mRefreshSl.setEnabled(false);
    }

    //当点击批量删除按钮，弹出确认是否删除的对话框提示
    public void showDeleteDialog() {

        if (mIsDeleteMemoBuilder == null) {
            mIsDeleteMemoBuilder = new AlertDialog.Builder(this);
            mIsDeleteMemoBuilder.setMessage(R.string.ensure_delete_selected);
            mIsDeleteMemoBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (Memo eachMemo : mSelectedMemoList) {
                        mMemoList = MemoListManager.deleteMemo(mMemoList, eachMemo);
                    }
                    mMemoDatabase.deleteSelectMemoList(mSelectedMemoList);
                    recoverBulkOperateView();
                    Toast.makeText(MainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                    startRemindService();

                }
            });
            mIsDeleteMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (Memo memo : mSelectedMemoList) {
                        memo.setIs_chosen(0);
                        mMemoDatabase.updateMemoChosenStatus(memo, 0);
                    }
                    recoverBulkOperateView();
                }
            });
        }
        mIsDeleteMemoBuilder.create().show();
    }

    //批量复制状态
    public void toCopySelectItem() {
        mChosenTv.setVisibility(View.VISIBLE);
        mDealIv.setVisibility(View.VISIBLE);
        mDealIv.setBackgroundResource(R.drawable.copy);
        mDealIv.setTag(COPY);
        mRefreshSl.setEnabled(false);
    }

    //当点击批量复制按钮，先弹出对话框让选择创建日期
    public void showSelectDayDialog() {
        mSelectDayBuilder = new AlertDialog.Builder(this);
        mSelectDayBuilder.setTitle(R.string.select_date);
        mSelectDayBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showCopyDialog();
            }
        });
        mSelectDayBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Memo memo : mSelectedMemoList) {
                    memo.setIs_chosen(0);
                    mMemoDatabase.updateMemoChosenStatus(memo, 0);
                }
                recoverBulkOperateView();
            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.edit_dialog, null);
        DatePicker dialogDp = view.findViewById(R.id.dialogDp);
//        final TextView weekTv = view.findViewById(R.id.weekTv);
//        weekTv.setText(DateUtils.getSelectedWeek(Integer.parseInt(mCopyYear), Integer.parseInt(mCopyMonth) - 1, Integer.parseInt(mCopyDay)));
        dialogDp.init(Integer.parseInt(mCopyYear), Integer.parseInt(mCopyMonth) - 1, Integer.parseInt(mCopyDay), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                mCopyYear = String.valueOf(year);
                mCopyMonth = DateUtils.toNormalTime(month + 1);
                mCopyDay = DateUtils.toNormalTime(day);
//                weekTv.setText(DateUtils.getSelectedWeek(Integer.parseInt(mCopyYear), Integer.parseInt(mCopyMonth) - 1, Integer.parseInt(mCopyDay)));
            }
        });
        mSelectDayBuilder.setView(view);
        mSelectDayBuilder.create().show();
    }

    //用户选择好日期后，弹出确认是否创建到该日期的对话框提示
    public void showCopyDialog() {

        mIsCopyMemoBuilder = new AlertDialog.Builder(this);
        mIsCopyMemoBuilder.setMessage("将被创建至" + mCopyYear + "年" + mCopyMonth + "月" + mCopyDay + "日");
        mIsCopyMemoBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mMemoDatabase.insertSelectMemoList(mSelectedMemoList, mCopyYear, mCopyMonth, mCopyDay);
                for (Memo memo : mSelectedMemoList) {
                    memo.setIs_chosen(0);
                    mMemoDatabase.updateMemoChosenStatus(memo, 0);
                }
                if (mCopyYear.equals(mSelectedYear) && mCopyMonth.equals(mSelectedMonth)) {
                    for (Memo memo : mSelectedMemoList) {
                        Memo copyMemo = new Memo();
                        copyMemo.setId(mCopyYear + mCopyMonth + mCopyDay + memo.getStart_hour() + memo.getStart_minute() + memo.getFinish_hour() + memo.getFinish_minute() + new MyCalendar().getNow_hour() + memo.getId().substring(18, 22));
                        copyMemo.setYear(mCopyYear);
                        copyMemo.setMonth(mCopyMonth);
                        copyMemo.setDay(mCopyDay);
                        copyMemo.setWeek(DateUtils.getSelectedWeek(Integer.parseInt(mCopyYear), Integer.parseInt(mCopyMonth) - 1, Integer.parseInt(mCopyDay)));
                        copyMemo.setStart_hour(memo.getStart_hour());
                        copyMemo.setStart_minute(memo.getStart_minute());
                        copyMemo.setFinish_hour(memo.getFinish_hour());
                        copyMemo.setFinish_minute(memo.getFinish_minute());
                        copyMemo.setText(memo.getText());
                        copyMemo.setIs_remind(0);
                        copyMemo.setIs_completed(0);
                        copyMemo.setIs_chosen(0);
                        mMemoList = MemoListManager.insertMemo(mMemoList, copyMemo);
                    }
                }
                recoverBulkOperateView();
                Toast.makeText(MainActivity.this, R.string.copy_success, Toast.LENGTH_SHORT).show();
                startRemindService();
            }
        });
        mIsCopyMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Memo memo : mSelectedMemoList) {
                    memo.setIs_chosen(0);
                    mMemoDatabase.updateMemoChosenStatus(memo, 0);
                }
                recoverBulkOperateView();
            }
        });
        mIsCopyMemoBuilder.create().show();
    }

    //对更多操作的合法性检验
    public void checkMoreSelect() {
        if (mDealIv.getTag().equals(DELETE)) {
            if (mSelectedMemoList.size() == 0) {
                Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
            } else {
                showDeleteDialog();
            }
        } else if (mDealIv.getTag().equals(COPY)) {
            if (mSelectedMemoList.size() == 0) {
                Toast.makeText(this, R.string.nothing_to_copy, Toast.LENGTH_SHORT).show();
            } else {
                showSelectDayDialog();
            }
        }
    }

    //批量删除、复制后的恢复
    public void recoverBulkOperateView() {
        MORE_STATUS = 0;
        mSelectedMemoList.clear();
        mChosenTv.setVisibility(View.INVISIBLE);
        mDealIv.setVisibility(View.INVISIBLE);
        mChosenTv.setText(R.string.select_item);
        mRefreshSl.setEnabled(true);
        updateMemoList();
    }

    @Override//重写释放activity的
    public void finish() {
        if (MORE_STATUS == 1) {
            //更多（批量删除、批量复制）状态，恢复之前状态
            for (Memo memo : mSelectedMemoList) {
                memo.setIs_chosen(0);
                mMemoDatabase.updateMemoChosenStatus(memo, 0);
            }
            recoverBulkOperateView();
        } else {
            finish();
        }
    }
}
