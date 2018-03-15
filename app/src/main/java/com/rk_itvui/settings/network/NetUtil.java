package com.rk_itvui.settings.network;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetUtil {
    private static final String TAG = "NetUtil";
    public static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static String RSSI_CHANGE = "android.net.wifi.RSSI_CHANGED";
    public static String WIFI_STATE_CHANGE = WifiManager.WIFI_STATE_CHANGED_ACTION;
    static boolean DEBUG = true;

    /**
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected() && wifiInfo.isAvailable();
    }

    /**
     * 锟叫讹拷Ethernet锟角凤拷锟斤拷锟�
     */
    @SuppressLint("InlinedApi")
    public static boolean isEthernetConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo etherInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        return etherInfo.isConnected() && etherInfo.isAvailable();
    }

    public static boolean isNetworkAvailable(Context context) {
    	Log.i("zxy", "wifi=="+isWifiConnect(context)+"************eth=="+isEthernetConnect(context));
        return isWifiConnect(context) || isEthernetConnect(context);
    }

    public static int wifiLevel(Context context) {
        WifiManager manager = (WifiManager) context
                .getSystemService(Service.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        Log.d("WifiSS", String.valueOf(Math.abs(info.getRssi())));
        return Math.abs(info.getRssi());
    }

    public static InputStream getInputStream(String strUrl) {
        InputStream in = null;
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            in = connection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    public static String getStringFromUrl(String strUrl) {
        Log.i("zxy", "getStringFromUrl: " + strUrl);
        InputStream in = null;
        String content = null;
        URL url;
        try {
            url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = null;
            content = new String();
            while ((str = br.readLine()) != null) {
                content = content + str;
                str = null;
            }
        } catch (MalformedURLException e) {
        	Log.i("zxy", "MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
        	Log.i("zxy", "IOException");
            e.printStackTrace();
        }

        return content;
//        try {
//			String result = OKhttpManager.exuteFromServer(strUrl);
//			Log.i("zxy", "result =="+result);
//			return result;
//		} catch (IOException e) {
//			Log.i("zxy", "result ==IOException");
//			e.printStackTrace();
//		}
//        return null;
    }

   
}
