/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rk_itvui.settings.network;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
//for 5.0
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.wifi.WifiManager;
import android.net.StaticIpConfiguration;
import android.net.NetworkUtils;
import android.net.LinkAddress;
import android.net.LinkProperties;
import java.util.regex.Pattern;
import java.lang.Integer;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.rk_itvui.settings.MyNetUtil;


public class EthernetIP {
    private Context context;
    public ContentResolver contentResolver;
    public EthernetManager mEthManager;

    public static final String defaultIPAdress = "192.168.1.100";
    public static final String defaultIPNetMask = "255.255.255.0";
    public static final String defaultGateWay = "192.168.1.1";
    public static final String defaultDNS1 = "8.8.8.8";
    public static final String defaultDNS2 = "202.38.64.1";

    public String mEthIpAddress;
    public String mEthNetmask;
    public String mEthGateway;
    public String mEthdns1;
    public String mEthdns2;
    public String mIface = "eth0";

    public static final int ETHER_DHCP=0;
    public static final int ETHER_STATIC=1;

    public String TAG="EthernetIP";
    /*
     * tools
    */
    private void log(String s) {
        Log.d(TAG, s);
    }
    /*-------------------------------------------------------*/
//    private String[] mSettingNames = { System.ETHERNET_STATIC_IP,
//        System.ETHERNET_STATIC_GATEWAY, System.ETHERNET_STATIC_NETMASK,
//        System.ETHERNET_STATIC_DNS1, System.ETHERNET_STATIC_DNS2
//    };

	/*-------------------------------------------------------*/
    public boolean isUsingStaticIp() {
    	if(mEthManager!=null){
    	 return mEthManager.getConfiguration().getIpAssignment() == IpAssignment.STATIC?true:false;
    	}
    	return false;
    }

    public boolean isConnected() {
//        EthernetManager mEthManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
//        int state = mEthManager.getEthernetConnectState();
//        if (state == EthernetManager.ETHER_STATE_CONNECTED)
//            return true;
//        else
//            return false;
    	return NetUtil.isEthernetConnect(context);
        
    }



    public boolean switchEthernetMode(int mode) {
    	Log.i("EthernetIP",Thread.currentThread().getStackTrace()[2].getMethodName());
        switch(mode) {
        case ETHER_DHCP:
           log("switch to dhcp");
           mEthManager.setConfiguration(new IpConfiguration(IpAssignment.DHCP, ProxySettings.NONE,null,null));
        break;
        case ETHER_STATIC:
           log("switch to static IP");
           mEthManager.setConfiguration(setStaticIpConfiguration());
        break;
        }
    return true;
    }
    public boolean enableEthernetStaticIP(IpConfiguration mIpConfiguration) {

        log("switch to staticIP"); 
        mEthManager.setConfiguration(mIpConfiguration);
   
        return true;
    }


    public void transContext(Context context) { // 娑撴槒顩﹂弰顖欒礋娴滃棔绱堕柅鎶峯ntext閸滃苯鍨垫慨瀣mEthManager閿涳拷
        this.context = context;
        this.contentResolver = context.getContentResolver();
        try{
        	if(Build.VERSION.SDK_INT >= 21){
        	this.mEthManager = (EthernetManager) context
				.getSystemService(Context.ETHERNET_SERVICE);
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        if (mEthManager == null) {
            Log.e("ehernetIP", "get ethernet manager failed");
            return;
        }
    }

	/*
	 * 閼惧嘲褰嘔P閸︽澘娼� ipType==0:閼惧嘲褰囬惃鍕Ц闂堟瑦锟戒浮P ipType==1:閼惧嘲褰囬惃鍕ЦDHCP閸︽澘娼� ipType缁涘绨崗鏈电铂閿涘矂顣╅悾锟�
	 */
	public String getIPAddress(char ipType) {
		//Log.d("getIPAddress", System.getString(contentResolver, System.ETHERNET_STATIC_IP)+  SystemProperties.get("dhcp." + mIface + ".ipaddress"));
//		if (ipType == 0) { // 婵″倹鐏夌拋鍓х枂閻ㄥ嫰娼ら幀涓
//			return System.getString(contentResolver, System.ETHERNET_STATIC_IP);
//		} else if (ipType == 1) {// 閸斻劍锟戒浮P閸︽澘娼�
//			Log.i("MyNetUtil", "...."+MyNetUtil.getHostIP());
//			return MyNetUtil.getHostIP();
//		} else {
//			return null;
//		}
		return null;
	}

	public String getGateWay(char ipType) {
		if (ipType == 0) {// 婵″倹鐏夌拋鍓х枂閻ㄥ嫰娼ら幀涓
			return null;
					//System.getString(contentResolver,
					//System.ETHERNET_STATIC_GATEWAY);
		} else if (ipType == 1) {// 閸斻劍锟戒浮P閸︽澘娼�
			return SystemProperties.get("dhcp." + mIface + ".gateway");
		} else {
			return null;
		}
	}

	public String getNetMask(char ipType) {
		if (ipType == 0) {// 婵″倹鐏夌拋鍓х枂閻ㄥ嫰娼ら幀涓
			return null ;
			//System.getString(contentResolver,
				//	System.ETHERNET_STATIC_NETMASK);
		} else if (ipType == 1) {// 閸斻劍锟戒浮P閸︽澘娼�
			return SystemProperties.get("dhcp." + mIface + ".mask");
		} else {
			return null;
		}
	}

	public String getDNS1(char ipType) {
		if (ipType == 0) {// 婵″倹鐏夌拋鍓х枂閻ㄥ嫰娼ら幀涓
			return null ;/*System.getString(contentResolver,
					System.ETHERNET_STATIC_DNS1);*/
		} else if (ipType == 1) {// 閸斻劍锟戒浮P閸︽澘娼�
			return SystemProperties.get("dhcp." + mIface + ".dns1");

		} else {
			return null;
		}
	}

	public String getDNS2(char ipType) {
		if (ipType == 0) {// 婵″倹鐏夌拋鍓х枂閻ㄥ嫰娼ら幀涓
			return null ;/*System.getString(contentResolver,
					System.ETHERNET_STATIC_DNS2);*/
		} else if (ipType == 1) { // 閸斻劍锟戒浮P閸︽澘娼�
			return SystemProperties.get("dhcp." + mIface + ".dns2");
		} else {
			return null;
		}
	}

	public String getEthMac() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"sys/class/net/eth0/address"));
			return reader.readLine();
		} catch (Exception e) {
			Log.e("EthernetIP", "open sys/class/net/eth0/address failed : " + e);
			return "";
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				Log.e("EthernetIP",
						"close sys/class/net/eth0/address failed : " + e);
			}
		}
	}



	public boolean setIPAddress(String value) {
		if (isValidIpAddress(value)) { // 婵″倹鐏塈P閸︽澘娼冨锝団�橀敍灞藉灟娣囨繂鐡�
//			System.putString(contentResolver, System.ETHERNET_STATIC_IP, value);
//                        mEthIpAddress=value;
			return true;
		} else {
			return false;
		}

	}

	public boolean setGateWay(String value) {
		if (isValidIpAddress(value)) {
//			System.putString(contentResolver, System.ETHERNET_STATIC_GATEWAY,
//					value);
//                        mEthGateway=value;
			return true;
		} else {
			return false;
		}

	}

	public boolean setNetMask(String value) {
		if (isValidIpAddress(value)) {
//			System.putString(contentResolver, System.ETHERNET_STATIC_NETMASK,
//					value);
//                        mEthNetmask=value;
			return true;
		} else {
			return false;
		}
	}

	public boolean setDNS1(String value) {
		if (isValidIpAddress(value)) {
//			System.putString(contentResolver, System.ETHERNET_STATIC_DNS1,
//					value);
//                        mEthdns1=value;
			return true;
		} else {
			return false;
		}
	}

	public boolean setDNS2(String value) {
		if (isValidIpAddress(value)) {
//			System.putString(contentResolver, System.ETHERNET_STATIC_DNS2,
//					value);
//                        mEthdns2=value;
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 鏉╂柨娲� 閹稿洤鐣鹃惃锟� String 閺勵垰鎯侀弰锟� 閺堝鏅ラ惃锟� IP 閸︽澘娼�.
	 */
	private boolean isValidIpAddress(String value) {
		int start = 0;
		int end = value.indexOf('.');
		int numBlocks = 0;

		while (start < value.length()) {

			if (-1 == end) {
				end = value.length();
			}

			try {
				int block = Integer.parseInt(value.substring(start, end));
				if ((block > 255) || (block < 0)) {
					Log.w("EthernetIP",
							"isValidIpAddress() : invalid 'block', block = "
									+ block);
					return false;
				}
			} catch (NumberFormatException e) {
				Log.w("EthernetIP", "isValidIpAddress() : e = " + e);
				return false;
			}

			numBlocks++;

			start = end + 1;
			end = value.indexOf('.', start);
		}
		return numBlocks == 4;
	}



    private IpConfiguration setStaticIpConfiguration() {
    	StaticIpConfiguration mStaticIpConfiguration;
        mStaticIpConfiguration =new StaticIpConfiguration();
		 /*
		  * get ip address, netmask,dns ,gw etc.
		  */	 
        Inet4Address inetAddr = getIPv4Address(this.mEthIpAddress);
        int prefixLength = maskStr2InetMask(this.mEthNetmask); 
        InetAddress gatewayAddr =getIPv4Address(this.mEthGateway); 
        InetAddress dnsAddr = getIPv4Address(this.mEthdns1);
		 
        if (inetAddr.getAddress().toString().isEmpty() || prefixLength ==0 || gatewayAddr.toString().isEmpty()
		  || dnsAddr.toString().isEmpty()) {
              log("ip,mask or dnsAddr is wrong");
			  return null;
	}
		  
        String dnsStr2=this.mEthdns2;  
        mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
        mStaticIpConfiguration.gateway=gatewayAddr;
        mStaticIpConfiguration.dnsServers.add(dnsAddr);
  
        if (!dnsStr2.isEmpty()) {
            mStaticIpConfiguration.dnsServers.add(getIPv4Address(dnsStr2));
	} 
	  
        return (new IpConfiguration(IpAssignment.STATIC, ProxySettings.NONE,mStaticIpConfiguration,null));
    }

    /*
     * convert subMask string to prefix length
     */
    private int maskStr2InetMask(String maskStr) {
    	StringBuffer sb ;
    	String str;
    	int inetmask = 0; 
    	int count = 0;
    	/*
    	 * check the subMask format
    	 */
      	Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
    	if (pattern.matcher(maskStr).matches() == false) {
    		Log.e(TAG,"subMask is error");
    		return 0;
    	}
    	
    	String[] ipSegment = maskStr.split("\\.");
    	for(int n =0; n<ipSegment.length;n++) {
    		sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
    		str = sb.reverse().toString();
    		count=0;
    		for(int i=0; i<str.length();i++) {
    			i=str.indexOf("1",i);
    			if(i==-1)  
    				break;
    			count++;
    		}
    		inetmask+=count;
    	}
    	return inetmask;
    }

    private Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (Exception e) {
            return null;
        }
    }

}
