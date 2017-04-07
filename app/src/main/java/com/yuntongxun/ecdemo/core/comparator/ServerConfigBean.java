package com.yuntongxun.ecdemo.core.comparator;

import java.io.Serializable;

/**
 * Created by luhuashan on 16/3/17.
 */
public class ServerConfigBean implements Serializable{


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLvsip() {
        return lvsip;
    }

    public void setLvsip(String lvsip) {
        this.lvsip = lvsip;
    }

    public String getLvsport() {
        return lvsport;
    }

    public void setLvsport(String lvsport) {
        this.lvsport = lvsport;
    }

    public String getConnectip() {
        return connectip;
    }

    public void setConnectip(String connectip) {
        this.connectip = connectip;
    }

    public String getConnectport() {
        return connectport;
    }

    public void setConnectport(String connectport) {
        this.connectport = connectport;
    }

    public String getFileport() {
        return fileport;
    }

    public void setFileport(String fileport) {
        this.fileport = fileport;
    }

    public String getFileip() {
        return fileip;
    }

    public void setFileip(String fileip) {
        this.fileip = fileip;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getApptoken() {
        return apptoken;
    }

    public void setApptoken(String apptoken) {
        this.apptoken = apptoken;
    }

    private String name;
    private String lvsip;
    private String lvsport;
    private String connectip;
    private String connectport;
    private String fileport;
    private String fileip;
    private String appid;
    private String apptoken;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
