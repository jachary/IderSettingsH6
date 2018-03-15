package com.rk_itvui.settings;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/20.
 */

public class MyNetUtil {

    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;

    }
    public static String getEthMac() {
        String mac = "";
        File file = new File("/sys/class/net/eth0/address");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            mac = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac;
    }
    public static void  getlist() throws IOException {
        Process pro = Runtime.getRuntime().exec("ipconfig");
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
        List<String> rowList = new ArrayList();
        String temp;
        while((temp = br.readLine()) != null){
            rowList.add(temp );
        }
        for (String string : rowList) {
            if(string.indexOf("Subnet Mask") != -1){
                Matcher mc = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(string);
                if(mc.find()){
                    System.out.println("瀛愭帺鐮侊細" + mc.group());
                    Log.i("System1",mc.group());
                }else{
                    System.out.println("瀛愭帺鐮佷负绌�");
                    Log.i("System1",mc.group());
                }
            };
            if(string.indexOf("Default Gateway") != -1){
                Matcher mc = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(string);
                if(mc.find()){
                    System.out.println("榛樿缃戝叧锛�" + mc.group());
                    Log.i("System",mc.group());
                }else{
                    System.out.println("榛樿缃戝叧涓虹┖");
                    Log.i("System",mc.group());
                }
                return;
            };
        }
    }
        }
