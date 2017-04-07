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

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.photopicker.model.PhotoDirectory;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

/**
 * 图片加载工具类
 * @author 容联•云通讯
 * @since 2016-4-6
 * @version 5.0
 */
public class MediaStoreHelper {
	public final static int INDEX_ALL_PHOTOS = 0;

	public static void getPhotoDirs(FragmentActivity activity, Bundle args,
			PhotosResultCallback resultCallback) {
		activity.getSupportLoaderManager().initLoader(0, args,
				new PhotoDirLoaderCallbacks(activity, resultCallback));
	}

	static class PhotoDirLoaderCallbacks implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private Context context;
		private PhotosResultCallback resultCallback;

		public PhotoDirLoaderCallbacks(Context context,
				PhotosResultCallback resultCallback) {
			this.context = context;
			this.resultCallback = resultCallback;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new PhotoDirectoryLoader(context, false);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

			if (data == null)
				return;
			List<PhotoDirectory> directories = new ArrayList<PhotoDirectory>();
			PhotoDirectory photoDirectoryAll = new PhotoDirectory();
			photoDirectoryAll.setName(context.getString(R.string.picker_all_image));
			photoDirectoryAll.setId("ALL");

			while (data.moveToNext()) {

				int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
				String bucketId = data.getString(data .getColumnIndexOrThrow(BUCKET_ID));
				String name = data.getString(data .getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
				String path = data.getString(data.getColumnIndexOrThrow(DATA));

				PhotoDirectory photoDirectory = new PhotoDirectory();
				photoDirectory.setId(bucketId);
				photoDirectory.setName(name);

				if (!directories.contains(photoDirectory)) {
					photoDirectory.setCoverPath(path);
					photoDirectory.addPhoto(imageId, path);
					photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
					directories.add(photoDirectory);
				} else {
					directories.get(directories.indexOf(photoDirectory)) .addPhoto(imageId, path);
				}

				photoDirectoryAll.addPhoto(imageId, path);
			}
			if (photoDirectoryAll.getPhotoPaths().size() > 0) {
				photoDirectoryAll.setCoverPath(photoDirectoryAll .getPhotoPaths().get(0));
			}
			directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);
			if (resultCallback != null) {
				resultCallback.onResultCallback(directories);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	}

	public interface PhotosResultCallback {
		void onResultCallback(List<PhotoDirectory> directories);
	}
}
