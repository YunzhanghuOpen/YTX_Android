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

import java.util.ArrayList;
import java.util.List;

/**
 * 图片文件夹
 * @author 容联•云通讯
 * @since 2016-4-6
 * @version 5.0
 */
public class PhotoDirectory {

	private String id;
    /** 文件夹名 */
    private String name;
    /** 文件夹路径 */
    private String coverPath;
    /** 该文件夹下图片列表 */
    private List<Photo> photos = new ArrayList<Photo>();
    /** 标识是否选中该文件夹 */
    private boolean isSelected;
    private long  dateAdded;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PhotoDirectory))
			return false;

		PhotoDirectory directory = (PhotoDirectory) o;

		if (!id.equals(directory.id))
			return false;
		return name.equals(directory.name);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public List<String> getPhotoPaths() {
		List<String> paths = new ArrayList<String>(photos.size());
		for (Photo photo : photos) {
			paths.add(photo.getPath());
		}
		return paths;
	}

	public void addPhoto(int id, String path) {
		photos.add(new Photo(id, path));
	}
}
