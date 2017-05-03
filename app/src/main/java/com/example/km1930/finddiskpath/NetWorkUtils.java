package com.example.km1930.finddiskpath;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by djf on 2017/4/26.
 */

public class NetWorkUtils {

    private static String TAG="NetWorkUtils";
    public static String getMAC(){
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                byte[] addr = networkInterface.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }

                /*

                 */
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02x:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                String mac = buf.toString();
                Log.e(TAG,"网卡信息："+networkInterface.toString()
                        +"   getName="+networkInterface.getName()
                        +"   getDisplayName="+networkInterface.getDisplayName()
                        +"   getMTU="+networkInterface.getMTU()
                        +"   mac="+mac);
                return mac;
            }

            return "null";
        } catch (final SocketException e) {
            e.printStackTrace();
            return "null";
        }
    }
    public static String getIp( ) {

//        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        //检查网络状态
//        if (!wm.isWifiEnabled())
//            wm.setWifiEnabled(true);
//        WifiInfo wi = wm.getConnectionInfo();
//        //获取32位整型IP地址
//        int ipAdd = wi.getIpAddress();
//        //把整型地址转换成“*.*.*.*”地址
//        return intToIp(ipAdd);


        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface
                        .getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

            return "null";
        } catch (final SocketException e) {
            e.printStackTrace();
            return "null";
        }

    }
}
