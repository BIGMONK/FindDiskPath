package com.example.km1930.finddiskpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by djf on 2017/5/4.
 */

public class FileUtils {

    public static ArrayList<String> getDiskPath() {
        ArrayList<String> list = new ArrayList<>();
        File file = new File("/proc/mounts");
        try {
            if (file.canRead()) {
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    String[] parts = lines.split("\\s+");
                    //                    list.add(lines);
                    if (parts.length >= 2 && parts[0].contains("vold") ||//一般情况下vold 外部存储  fuse内部存储
                            parts[0].contains("fuse")) {
                        list.add("详情：" + lines);
                        list.add(parts[1]);
                    }
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getFilePath(File file) {
        ArrayList<String> list = new ArrayList<>();
        if (file.isFile() && (file.getName().endsWith("mp4") || file.getName().endsWith("MP4"))) {
            list.add(file.getAbsolutePath());
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    list.addAll(getFilePath(files[i]));
                }
            }
        }
        return list;
    }
    private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
    private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");
    public static String formatFileSize(long size, boolean isInteger) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        String fileSizeString = "0M";
        if (size < 1024 && size > 0) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1024 * 1024) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "G";
        }
        return fileSizeString;
    }
}
