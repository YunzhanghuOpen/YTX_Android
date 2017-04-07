package com.yuntongxun.ecdemo.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luhuashan on 16/3/25.
 */
public class RichTextBean implements Parcelable {

    private String title;
    private String desc;
    private String url;
    private String picUrl="";


    protected RichTextBean(Parcel in) {
        title = in.readString();
        desc = in.readString();
        url = in.readString();
        picUrl=in.readString();
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public RichTextBean(){
        super();
    }

    public static final Creator<RichTextBean> CREATOR = new Creator<RichTextBean>() {
        @Override
        public RichTextBean createFromParcel(Parcel in) {
            return new RichTextBean(in);
        }

        @Override
        public RichTextBean[] newArray(int size) {
            return new RichTextBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(url);
        dest.writeString(picUrl);

    }
}
