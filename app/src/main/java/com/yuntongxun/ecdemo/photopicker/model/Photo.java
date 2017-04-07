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
 */package com.yuntongxun.ecdemo.photopicker.model;


/**
 * 图片实体信息
 * 
 * @author 容联•云通讯
 * @since 2016-4-6
 * @version 5.0
 */
public class Photo {

	private int id;
	private String path;

	public Photo(int id, String path) {
		this.id = id;
		this.path = path;
	}

	public Photo() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Photo))
			return false;

		Photo photo = (Photo) o;

		return id == photo.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
