/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.yuntongxun.ecdemo.photopicker;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.photopicker.model.Photo;
import com.yuntongxun.ecdemo.photopicker.model.PhotoDirectory;
import com.yuntongxun.ecdemo.photopicker.utils.MediaStoreHelper;
import com.yuntongxun.ecdemo.photopicker.utils.PhotoUtils;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择界面
 * @author 容联•云通讯
 * @since 2016-4-6
 * @version 5.0
 */
public class PhotoPickerActivity extends ECSuperActivity implements PhotoAdapter.PhotoClickCallBack ,
            View.OnClickListener{

    public final static String TAG = "PhotoPickerActivity";

    public final static String KEY_RESULT = "picker_result";
    public final static int REQUEST_CAMERA = 1;

    /** 是否显示相机 */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /** 照片选择模式 */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /** 最大选择数量 */
    public final static String EXTRA_MAX_MUN = "max_num";
    /** 单选 */
    public final static int MODE_SINGLE = 0;
    /** 多选 */
    public final static int MODE_MULTI = 1;
    /** 默认最大选择数量 */
    public final static int DEFAULT_NUM = 9;

    private final static String ALL_PHOTO = "所有图片";
    /** 是否显示相机，默认不显示 */
    private boolean mIsShowCamera = false;
    /** 照片选择模式，默认是单选模式 */
    private int mSelectMode = 0;
    /** 最大选择数量，仅多选模式有用 */
    private int mMaxNum;

    private GridView mGridView;
    private List<PhotoDirectory> mSrcFloderMap;
    private List<Photo> mPhotoLists = new ArrayList<Photo>();
    private ArrayList<String> mSelectList = new ArrayList<String>();
    private PhotoAdapter mPhotoAdapter;
    private ListView mFloderListView;

    private TextView mPhotoNumTV;
    private TextView mPhotoNameTV;
    /** 文件夹列表是否处于显示状态 */
    boolean mIsFloderViewShow = false;
    /** 文件夹列表是否被初始化，确保只被初始化一次 */
    boolean mIsFloderViewInit = false;

    /** 拍照时存储拍照结果的临时文件 */
    private File mTmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSrcFloderMap = new ArrayList<PhotoDirectory>();
        initIntentParams();
        initView();



        if (!PhotoUtils.isExternalStorageAvailable()) {
            Toast.makeText(this, "No SD card!", Toast.LENGTH_SHORT).show();
            finish();
            return ;
        }
        
		MediaStoreHelper.getPhotoDirs(this, new Bundle(),
				new MediaStoreHelper.PhotosResultCallback() {

					@Override
					public void onResultCallback(List<PhotoDirectory> dirs) {
						mSrcFloderMap.clear();
						mSrcFloderMap.addAll(dirs);
						getPhotosSuccess();
					}
				});

    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_activity_photo_picker;
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.photo_gridview);
        mPhotoNumTV = (TextView) findViewById(R.id.photo_num);
        mPhotoNameTV = (TextView) findViewById(R.id.floder_name);
        findViewById(R.id.bottom_tab_bar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //消费触摸事件，防止触摸底部tab栏也会选中图片
                return true;
            }
        });
    }

    /**
     * 初始化选项参数
     */
    private void initIntentParams() {
        mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mSelectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        mMaxNum = getIntent().getIntExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
        if(mSelectMode == MODE_MULTI) {
            //如果是多选模式，需要将确定按钮初始化以及绑定事件
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                    R.drawable.btn_style_green, null,
                    getString(R.string.dialog_ok_button),
                    getString(R.string.picker_title), null, this);
        } else {
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,
                    R.string.picker_title, this);
        }
    }

    private void getPhotosSuccess() {
        mPhotoLists.addAll(mSrcFloderMap.get(0).getPhotos());

        mPhotoNumTV.setText(PhotoUtils.formatResourceString(getApplicationContext(),
                R.string.picker_photos_num, mPhotoLists.size()));

        mPhotoAdapter = new PhotoAdapter(this.getApplicationContext(), mPhotoLists);
        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
        mPhotoAdapter.setSelectMode(mSelectMode);
        mPhotoAdapter.setMaxNum(mMaxNum);
        mPhotoAdapter.setPhotoClickCallBack(this);
        mGridView.setAdapter(mPhotoAdapter);
        final List<PhotoDirectory> folders = new ArrayList<PhotoDirectory>();
        for(int i = 0; i < mSrcFloderMap.size() ; i++) {
        	if (i == 0) {
            	PhotoDirectory folder = mSrcFloderMap.get(i);
                folder.setIsSelected(true);
                folders.add(0, folder);
            }else {
                folders.add(mSrcFloderMap.get(i));
            }
        }
        mPhotoNameTV.setOnClickListener(new View.OnClickListener() {
            //@TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                toggleFolderList(folders);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPhotoAdapter.isShowCamera() && position == 0) {
                    showCamera();
                    return;
                }
                selectPhoto(mPhotoAdapter.getItem(position));
            }
        });
    }

    /**
     * 点击选择某张照片
     * @param photo 某张照片
     */
    private void selectPhoto(Photo photo) {
        if(photo == null) {
            return;
        }
        String path = photo.getPath();
        if(mSelectMode == MODE_SINGLE) {
            mSelectList.add(path);
            returnData();
        }
    }

    @Override
    public void onPhotoClick() {
        List<String> list = mPhotoAdapter.getSelectedPhotos();
        if(list != null && list.size()>0) {
            getTopBarView().setRightBtnEnable(true);
            getTopBarView().setRightButtonText(PhotoUtils.formatResourceString(getApplicationContext(),
                    R.string.picker_commit_num, list.size(), mMaxNum));
        } else {
            getTopBarView().setRightBtnEnable(false);
            getTopBarView().setRightButtonText(getString(R.string.picker_commit));
        }
    }

    /**
     * 返回选择图片的路径
     */
    private void returnData() {
        // 返回已选择的图片数据
        Intent data = new Intent();
        data.putStringArrayListExtra(KEY_RESULT, mSelectList);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 显示或者隐藏文件夹列表
     * @param folders
     */
    private void toggleFolderList(final List<PhotoDirectory> folders) {
        //初始化文件夹列表
        if(!mIsFloderViewInit) {
            ViewStub folderStub = (ViewStub) findViewById(R.id.floder_stub);
            folderStub.inflate();
            View dimLayout = findViewById(R.id.dim_layout);
            mFloderListView = (ListView) findViewById(R.id.listview_floder);
            final FolderAdapter adapter = new FolderAdapter(this, folders);
            mFloderListView.setAdapter(adapter);
            mFloderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (PhotoDirectory folder : folders) {
                        folder.setIsSelected(false);
                    }
                    PhotoDirectory folder = folders.get(position);
                    folder.setIsSelected(true);
                    adapter.notifyDataSetChanged();

                    mPhotoLists.clear();
                    mPhotoLists.addAll(folder.getPhotos());
                    if (ALL_PHOTO.equals(folder.getName())) {
                        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
                    } else {
                        mPhotoAdapter.setIsShowCamera(false);
                    }
                    //这里重新设置adapter而不是直接notifyDataSetChanged，是让GridView返回顶部
                    mGridView.setAdapter(mPhotoAdapter);
                    mPhotoNumTV.setText(PhotoUtils.formatResourceString(getApplicationContext(),
                            R.string.picker_photos_num, mPhotoLists.size()));
                    mPhotoNameTV.setText(folder.getName());
                    toggle();
                }
            });
            dimLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIsFloderViewShow) {
                        toggle();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            initAnimation(dimLayout);
            mIsFloderViewInit = true;
        }
        toggle();
    }
    
    @Override
    public void onBackPressed() {
    	if(mIsFloderViewShow) {
    		outAnimatorSet.start();
            mIsFloderViewShow = false;
            return ;
    	}
    	super.onBackPressed();
    }

    /**
     * 弹出或者收起文件夹列表
     */
    private void toggle() {
        if(mIsFloderViewShow) {
            outAnimatorSet.start();
            mIsFloderViewShow = false;
        } else {
            inAnimatorSet.start();
            mIsFloderViewShow = true;
        }
    }


    /**
     * 初始化文件夹列表的显示隐藏动画
     */
    AnimatorSet inAnimatorSet = new AnimatorSet();
    AnimatorSet outAnimatorSet = new AnimatorSet();
    private void initAnimation(View dimLayout) {
        ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator, transOutAnimator;
        //获取actionBar的高
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        /**
         * 这里的高度是，屏幕高度减去上、下tab栏，并且上面留有一个tab栏的高度
         * 所以这里减去3个actionBarHeight的高度
         */
        int height = PhotoUtils.getHeightInPx(this) - 3*actionBarHeight;
        alphaInAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0f, 0.7f);
        alphaOutAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0.7f, 0f);
        transInAnimator = ObjectAnimator.ofFloat(mFloderListView, "translationY", height , 0);
        transOutAnimator = ObjectAnimator.ofFloat(mFloderListView, "translationY", 0, height);

        LinearInterpolator linearInterpolator = new LinearInterpolator();

        inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
        inAnimatorSet.setDuration(300);
        inAnimatorSet.setInterpolator(linearInterpolator);
        outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
        outAnimatorSet.setDuration(300);
        outAnimatorSet.setInterpolator(linearInterpolator);
    }

    /**
     * 选择文件夹
     * @param photoFolder
     */
    public void selectFolder(PhotoDirectory photoFolder) {
        mPhotoAdapter.setData(photoFolder.getPhotos());
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 选择相机
     */
    private void showCamera() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = PhotoUtils.createFile(getApplicationContext());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            Toast.makeText(getApplicationContext(),
                    R.string.picker_msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 相机拍照完成后，返回图片路径
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    mSelectList.add(mTmpFile.getAbsolutePath());
                    returnData();
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.text_right:
                mSelectList.addAll(mPhotoAdapter.getSelectedPhotos());
                returnData();
                break;
            default:
                break;
        }
    }

}
