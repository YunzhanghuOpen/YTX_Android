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
 */package com.yuntongxun.ecdemo.photopicker.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 图片工具类
 * @author 容联•云通讯
 * @since 2016-4-6
 * @version 5.0
 */
public class PhotoUtils {

	/**
	 * 判断外部存储卡是否可用
	 * @return 是否可用
	 */
	public static boolean isExternalStorageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static int getHeightInPx(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getWidthInPx(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getHeightInDp(Context context) {
		final float height = context.getResources().getDisplayMetrics().heightPixels;
		return px2dip(context, height);
	}

	public static int getWidthInDp(Context context) {
		final float width = context.getResources().getDisplayMetrics().widthPixels;
		return px2dip(context, width);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 资源格式化字符串
	 * @param context 上下文
	 * @param resource 资源
	 * @param args 参数
	 * @return 字符串
	 */
	public static String formatResourceString(Context context, int resource,
			Object... args) {
		String str = context.getResources().getString(resource);
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		return String.format(str, args);
	}

	/**
	 * 获取拍照相片存储文件
	 * @param context  上下文
	 * @return 文件对象
	 */
	public static File createFile(Context context) {
		File file;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String timeStamp = String.valueOf(new Date().getTime());
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + timeStamp + ".jpg");
		} else {
			File cacheDir = context.getCacheDir();
			String timeStamp = String.valueOf(new Date().getTime());
			file = new File(cacheDir, timeStamp + ".jpg");
		}
		return file;
	}

}
