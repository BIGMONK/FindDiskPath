package com.example.km1930.finddiskpath;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by djf on 2017/5/4.
 */

public class MyFileFilter implements FileFilter {

    private String endStr;
    private String[] endStrs;

    public MyFileFilter(String endStr) {
        this.endStr = endStr;
    }

    public MyFileFilter(String[] endStrs) {
        this.endStrs = endStrs;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory())
            return true;
        else {
            String name = file.getName();
            if (endStrs != null && endStrs.length > 0) {
                for (String s : endStrs) {
                    if (name.endsWith(s)||name.endsWith(s.toUpperCase())||name.endsWith(s.toLowerCase())) {
                        return true;
                    }
                }
            }
            if ((TextUtils.isEmpty(endStr) && name.endsWith(endStr)))
                return true;
            else
                return false;
        }

    }

}
