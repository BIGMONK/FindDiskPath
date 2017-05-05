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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private ListViewAdapter adapter;
    private ArrayList<String> diskPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.lv);
        final ArrayList<String> list = new ArrayList<>();
        adapter = new ListViewAdapter(list, this);
        listView.setAdapter(adapter);
//
        final List<String> actions = new ArrayList<>();
        final List<String> mountedActions = new ArrayList<>();
        mountedActions.add(Intent.ACTION_MEDIA_MOUNTED);
        mountedActions.add(Intent.ACTION_MEDIA_CHECKING);
        final List<String> unmountedActions = new ArrayList<>();
        unmountedActions.add(Intent.ACTION_MEDIA_UNMOUNTED);
        unmountedActions.add(Intent.ACTION_MEDIA_EJECT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(MainActivity.this, "intent.getAction()=" + intent.getAction(),
//                        Toast.LENGTH_SHORT).show();
                actions.add(intent.getAction());
                if (actions.containsAll(unmountedActions) || actions.containsAll(mountedActions)) {
                    actions.clear();
                    list.removeAll(diskPath);
                    diskPath = FileUtils.getDiskPath();
                    list.addAll(diskPath);
                    adapter.notifyDataSetChanged();
                }
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


        //TODO 获取  MAC
        list.add("getWifiMAC:" + getWifiMAC(this));
        list.add("getFirstMAC:" + NetWorkUtils.getMAC() + "  " + NetWorkUtils.getIp());
        list.add("*****************************************");
        //TODO 获取屏幕参数
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        list.add("屏幕参数1:" + display.toString());
        list.add("*****************************************");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int densityDpi = dm.densityDpi;
        float scaledDensity = dm.scaledDensity;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        int heightPixels = dm.heightPixels;
        int widthPixels = dm.widthPixels;
        list.add("屏幕参数2:" + dm.toString() +
                "  density=" + density +
                "  densityDpi=" + densityDpi +
                "  scaledDensity=" + scaledDensity +
                "  xdpi=" + xdpi +
                "  ydpi=" + ydpi +
                "  widthPixels=" + widthPixels +
                "  heightPixels=" + heightPixels);
        list.add("*****************************************");


        //TODO 获取系统信息
        list.add("系统信息Product Model: " + getSysInfo());
        list.add("*****************************************");
        list.add("UUID:" + DeviceIdUtils.getDeviceUuid(this).toString());
        list.add("*****************************************");
        list.add("SERIAL=" + DeviceIdUtils.getSerial());
        list.add("*****************************************");
        list.add("ANDROID_ID=" + DeviceIdUtils.getAndroidId(this));
        list.add("*****************************************");

        //TODO 获取可用存储位置和大小
        diskPath = FileUtils.getDiskPath();
        list.addAll(diskPath);
        adapter.notifyDataSetChanged();


    }

    public static String getWifiMAC(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public String getSysInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n " + "Build.MODEL:" + Build.MODEL);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.MODEL:" + e.getMessage());
        }
        try {
            sb.append("\n " + "Build.VERSION.SDK:" + Build.VERSION.SDK);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.SDK:" + e.getMessage());
        }
        try {
            sb.append("\n " + "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.SDK_INT:" + e.getMessage());
        }
        try {
            sb.append("\n " + "Build.VERSION.RELEASE:" + Build.VERSION.RELEASE);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.RELEASE:" + e.getMessage());
        }
        try {
            sb.append("\n " + "Build.VERSION.CODENAME:" + Build.VERSION.CODENAME);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.CODENAME:" + e.getMessage());
        }
        try {
            sb.append("\n " + "Build.VERSION.BASE_OS:" + Build.VERSION.BASE_OS);//部分系统导致崩溃
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.BASE_OS:" + e.getMessage());
        }
        try {
//            sb.append("\n " + "Build.VERSION.SECURITY_PATCH:" + Build.VERSION.SECURITY_PATCH);
            // 部分系统导致崩溃
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("\n " + "Build.VERSION.SECURITY_PATCH:" + e.getMessage());
        }
        sb.append("\nProduct: " + android.os.Build.PRODUCT);
        sb.append("\nCPU_ABI: " + android.os.Build.CPU_ABI);
        sb.append("\nTAGS: " + android.os.Build.TAGS);
        sb.append("\nVERSION_CODES.BASE: "
                + android.os.Build.VERSION_CODES.BASE);
        sb.append("\nDEVICE: " + android.os.Build.DEVICE);
        sb.append("\nDISPLAY: " + android.os.Build.DISPLAY);
        sb.append("\nBRAND: " + android.os.Build.BRAND);
        sb.append("\nBOARD: " + android.os.Build.BOARD);
        sb.append("\nFINGERPRINT: " + android.os.Build.FINGERPRINT);
        sb.append("\nID: " + android.os.Build.ID);
        sb.append("\nMANUFACTURER: " + android.os.Build.MANUFACTURER);
        sb.append("\nUSER: " + android.os.Build.USER);

        return sb.toString();
    }
}
