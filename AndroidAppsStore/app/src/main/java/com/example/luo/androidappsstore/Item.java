package com.example.luo.androidappsstore;

/**
 * Created by luo on 2017/6/2.
 */

public class Item {
    private String name;
    private String icon;
    private String url;
    private String md5sum;

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item(){

    }
    public Item(String name, String icon, String url, String md5sum){
        this.name = name;
        this.icon = icon;
        this.url = url;
        this.md5sum = md5sum;

    }

}
