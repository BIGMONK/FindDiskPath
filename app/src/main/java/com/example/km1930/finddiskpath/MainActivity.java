package com.example.km1930.finddiskpath;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
        listView.setOnItemClickListener(this);
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

        Runtime rt=Runtime.getRuntime();
        long maxMemory=rt.maxMemory();
        list.add("app可使用的最大memory size=" + Long.toString(maxMemory/(1024*1024)));
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am. getMemoryClass();
        int largeMemoryClass = am. getLargeMemoryClass();
        list.add("系统对应用的内存限制的值 heapgrowthlimit=" +memoryClass);
        list.add("系统可提供给应用的最大内存 heapsize=" + largeMemoryClass);
        list.add("*****************************************");

        //TODO 获取可用存储位置和大小
        diskPath = FileUtils.getDiskPath();
        list.addAll(diskPath);
        adapter.notifyDataSetChanged();

        int lebId = Resources.getSystem()
                .getIdentifier("permlab_accessNetworkState",
                        "string", "android");
        String lab = getString(lebId);

        DecimalFormat df1 = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("#.#");
        DecimalFormat df3 = new DecimalFormat("000.000");
        DecimalFormat df4 = new DecimalFormat("###.###");

        System.out.println("数据格式刷："+df1.format(12.34));
        System.out.println("数据格式刷："+df2.format(12.34));
        System.out.println("数据格式刷："+df3.format(12.34));
        System.out.println("数据格式刷："+df4.format(12.34));
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
            sb.append("    " + "Build.MODEL:" + e.getMessage());
        }
        try {
            sb.append("    " + "Build.VERSION.SDK:" + Build.VERSION.SDK);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.SDK:" + e.getMessage());
        }
        try {
            sb.append("    " + "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.SDK_INT:" + e.getMessage());
        }
        try {
            sb.append("    " + "Build.VERSION.RELEASE:" + Build.VERSION.RELEASE);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.RELEASE:" + e.getMessage());
        }
        try {
            sb.append("    " + "Build.VERSION.CODENAME:" + Build.VERSION.CODENAME);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.CODENAME:" + e.getMessage());
        }
        try {//部分系统崩溃
//            sb.append("\n " + "Build.VERSION.BASE_OS:" + Build.VERSION.BASE_OS);
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.BASE_OS:" + e.getMessage());
        }
        try {  // 部分系统导致崩溃
        //sb.append("\n " + "Build.VERSION.SECURITY_PATCH:" + Build.VERSION.SECURITY_PATCH);//部分系统崩溃
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("    " + "Build.VERSION.SECURITY_PATCH:" + e.getMessage());
        }
        sb.append("   Product: " + android.os.Build.PRODUCT);
        sb.append("   CPU_ABI: " + android.os.Build.CPU_ABI);
        sb.append("   TAGS: " + android.os.Build.TAGS);
        sb.append("   VERSION_CODES.BASE: "
                + android.os.Build.VERSION_CODES.BASE);
        sb.append("   DEVICE: " + android.os.Build.DEVICE);
        sb.append("   DISPLAY: " + android.os.Build.DISPLAY);
        sb.append("   BOARD: " + android.os.Build.BOARD);
        sb.append("   FINGERPRINT: " + android.os.Build.FINGERPRINT);
        sb.append("   ID: " + android.os.Build.ID);
        sb.append("   MANUFACTURER: " + android.os.Build.MANUFACTURER);
        sb.append("   USER: " + android.os.Build.USER);
        sb.append("   SERIAL: " + Build.SERIAL);
              return sb.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("点击测试", "parent.getClass().getName():"+parent.getClass().getName()
                +"  parent.getId():"+parent.getId()
                +"  view.getClass().getName():"+view.getClass().getName()
                +"  view.getId():"+view.getId()
                +"  position:"+position
                +"  id："+id);
    }
}
