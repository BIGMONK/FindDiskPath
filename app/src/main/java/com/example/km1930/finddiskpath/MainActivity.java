package com.example.km1930.finddiskpath;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.lv);
        final ArrayList<String> list = new ArrayList<>();
        adapter = new ListViewAdapter(list, this);
        listView.setAdapter(adapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                System.out.println("BroadcastReceiver getAction" + intent.getAction() +
                        "  getData=" + intent.getData());

                finddisk(listView);

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SHARED);//如果SDCard未安装,并通过USB大容量存储共享返回
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd对象是存在并具有读/写权限
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但没有被安装
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //表明对象正在磁盘检查
        filter.addAction(Intent.ACTION_MEDIA_EJECT);  //物理的拔出 SDCARD
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
//        <action android:name="android.hardware.usb.action.USB_STATE" />
        filter.addDataScheme("file"); // 必须要有此行，否则无法收到广播
        registerReceiver(receiver, filter);
        finddisk(listView);


    }

    public static String getWifiMAC(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    private void finddisk(ListView listView) {
        ArrayList<String> list = new ArrayList<>();
        try {
            File file = new File("/proc/mounts");
            if (file.canRead()) {
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    String[] parts = lines.split("\\s+");
//                    list.add(lines);
                    if (parts.length >= 2 && parts[0].contains("vold") || parts[0].contains
                            ("fuse")) {
                        File file1 = new File(parts[1]);
                        list.add(lines);
                        list.add(file1.getAbsolutePath() + "###########getFreeSpace  =" + file1
                                .getFreeSpace());
                        list.add(file1.getAbsolutePath() + "###########getUsableSpace=" + file1
                                .getUsableSpace());
                        list.add("*****************************************");
//                        adapter.notifyDataSetChanged();
                        adapter = new ListViewAdapter(list, this);
                        listView.setAdapter(adapter);
                    }
                }
                //wifi  MAC
                list.add("getWifiMAC:" + getWifiMAC(this));
                list.add("getFirstMAC:" + NetWorkUtils.getMAC()+"  "+NetWorkUtils.getIp());

                list.add("*****************************************");

                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                int screenWidth = display.getWidth();
                int screenHeight = display.getHeight();
                list.add("屏幕参数1:" + display.toString());
                list.add("*****************************************");

                // 方法2
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                float density = dm.density;
                int densityDpi = dm.densityDpi;
                float scaledDensity = dm.scaledDensity;
                float xdpi = dm.xdpi;
                float ydpi = dm.ydpi;
                int heightPixels = dm.heightPixels;
                int widthPixels = dm.widthPixels;
                list.add("屏幕参数2:" + dm.toString()+
                        "  density="+density+
                        "  densityDpi="+densityDpi+
                        "  scaledDensity="+scaledDensity+
                        "  xdpi="+xdpi+
                        "  ydpi="+ydpi+
                        "  widthPixels="+widthPixels+
                        "  heightPixels="+heightPixels+
                        "\n " + "Product Model: " +
                        "\n " + "Build.MODEL:" + Build.MODEL +
                        "\n " + "Build.VERSION.SDK: " + Build.VERSION.SDK +
                        "\n " + "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT +
                        "\n " + "Build.VERSION.RELEASE=" + Build.VERSION.RELEASE +
                        "\n " + "Build.VERSION.SDK: " + Build.VERSION.BASE_OS +
                        "\n " + "Build.VERSION.CODENAME: " + Build.VERSION.CODENAME +
                        "\n " + "Build.VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL +
                        "\n " + "Build.VERSION.SECURITY_PATCH:" + Build.VERSION.SECURITY_PATCH +
                        ""

                );


                list.add("*****************************************");
                list.add("UUID:" + DeviceIdUtils.getDeviceUuid(this).toString());
                list.add("*****************************************");
                list.add("SERIAL=" + DeviceIdUtils.getSerial());
                list.add("*****************************************");
                list.add("ANDROID_ID=" + DeviceIdUtils.getAndroidId(this));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
