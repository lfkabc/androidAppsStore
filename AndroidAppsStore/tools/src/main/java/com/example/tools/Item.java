package com.example.tools;

/**
 * Created by luo on 2017/6/2.
 */

public class Item {
    private String name;
    private String icon;
    private String url;
    private String md5sum;
    private String version;
    private String pkgName;

    public Item(String name, String icon, String url, String version, String pkgName, String md5sum){
        this.name = name;
        this.icon = icon;
        this.url = url;
        this.md5sum = md5sum;
        this.version = version;
        this.pkgName = pkgName;

    }

}
