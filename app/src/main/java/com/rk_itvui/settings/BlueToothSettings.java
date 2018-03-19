package com.rk_itvui.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rk_itvui.settings.network.WifiApFragment;
import com.rk_itvui.settings.network.WifiFragment;
import com.rk_itvui.settings.network.WireNetworkFragment;
import com.rk_itvui.settings.network.wifi.AccessPoint;
import com.rk_itvui.settings.network.wifi.WifiScanListViewAdapter;
import com.rk_itvui.settings.network.wifi.Wifi_Enabler;
import com.rk_itvui.settings.network.wifi.Wifi_setting;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.zxy.idersettings.R;
import com.rk_itvui.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static com.rk_itvui.settings.LogUtils.LOGD;

/**
 * Created by guoliang.wgl on 18/3/18.
 */
public class BlueToothSettings extends FullScreenActivity implements  DialogInterface.OnClickListener {
    private String SUBTAG = "BlueToothSettings";
    private ListView mListView = null;
    ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
    WifiScanListViewAdapter mAdapter = null;
    private LayoutInflater flater = null;
    private LinearLayout mView = null;
    private Bluetooth_Enabler mBluetooth_Enabler;


    ///private Switch BluetoothSwitcher;
    private Button BluetoothSwitcher;
    private LinearLayout mWifiContentLayout;
    private LinearLayout mWifiDisconnectedLayout;
    private LinearLayout mWifiOpenningLayout;
    private void initSwitcher(){
        BluetoothSwitcher= (Button) findViewById(R.id.switcher);
//		BluetoothSwitcher.setOnCheckedChangeListener(listener);
        mWifiContentLayout = (LinearLayout) findViewById(R.id.wifi_list_content);
        mWifiDisconnectedLayout = (LinearLayout) findViewById(R.id.wifi_list_content_closed);
        mWifiOpenningLayout = (LinearLayout) findViewById(R.id.wifi_enable_wait);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
        createListView();
        getBluetoothCurrentState();
        ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
    }


    private void createListView() {
        flater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new WifiScanListViewAdapter(this, accessPoints);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListItemClickListener);
        mListView.requestFocus();
    }

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            if (position == 0) {
                LOGD(SUBTAG,"AdapterView.OnItemClickListener mListItemClickListener, position = 0, showDialog(null, true)");
                //showDialog(null, true);
            } else {
//                mSelected = accessPoints.get(position);
//                showDialog(mSelected, false);
            }
        }
    };

    private void getBluetoothCurrentState() {
//        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
//            BluetoothAdapter btAdapter  = BluetoothAdapter.getDefaultAdapter();
//            if(btAdapter != null && !btAdapter.isEnabled()){
//
//            }else {
//                LOGD(SUBTAG,"bluetooth is opened !");
//            }

            mBluetooth_Enabler = new Bluetooth_Enabler(this, mHandler);
            if (mBluetooth_Enabler != null) {
                mBluetooth_Enabler.resume();
                initSwitcher();
                //BluetoothSwitcher.setChecked(mBluetooth_Enabler.getCurrentState());
                if(mBluetooth_Enabler.getCurrentState())
                    BluetoothSwitcher.setBackgroundResource(R.drawable.switch_on);
                else
                    BluetoothSwitcher.setBackgroundResource(R.drawable.switch_off);
            }

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

//            LOGD("mHandler,handleMessage() msg.what = " + msg.what);
//            boolean mOpen;
//            int hint;
//            switch (msg.what) {
//                case WifiManager.WIFI_STATE_ENABLING: //娑擃參妫块悩鑸碉拷锟�
//                    //wifiSwitcher.setChecked(true);
//                    wifiSwitcher.setBackgroundResource(R.drawable.switch_on);
//                    wifiSwitcher.setEnabled(false);
//                    mWifiContentLayout.setVisibility(View.GONE);
//                    mWifiDisconnectedLayout.setVisibility(View.GONE);
//                    mWifiOpenningLayout.setVisibility(View.VISIBLE);
//
//                    hint = R.string.wifi_starting;
//                    break;
//                case WifiManager.WIFI_STATE_ENABLED:
//                    //wifiSwitcher.setChecked(true);
//                    wifiSwitcher.setBackgroundResource(R.drawable.switch_on);
//                    wifiSwitcher.setEnabled(true);
//                    mWifiContentLayout.setVisibility(View.VISIBLE);
//                    mWifiDisconnectedLayout.setVisibility(View.GONE);
//                    mWifiOpenningLayout.setVisibility(View.GONE);
//
//                    mOpen = true; // 閹垫挸绱戦幋鎰
//                    hint = R.string.turn_on;
//                    break;
//                case WifiManager.WIFI_STATE_DISABLING:
//                    //wifiSwitcher.setChecked(false);
//                    wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
//                    wifiSwitcher.setEnabled(false);
//                    mWifiContentLayout.setVisibility(View.VISIBLE);
//                    mWifiDisconnectedLayout.setVisibility(View.GONE);
//                    mWifiOpenningLayout.setVisibility(View.GONE);
//                    mOpen = false; // 閸忔娊妫存稉锟�
//                    hint = R.string.wifi_stopping;
//                    break;
//                case WifiManager.WIFI_STATE_DISABLED:
//                    //wifiSwitcher.setChecked(false);
//                    wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
//                    wifiSwitcher.setEnabled(true);
//                    mWifiContentLayout.setVisibility(View.GONE);
//                    mWifiDisconnectedLayout.setVisibility(View.VISIBLE);
//                    mWifiOpenningLayout.setVisibility(View.GONE);
//
//                    mOpen = false; // 瀹告彃鍙ч梻锟�
//                    hint = R.string.turn_off;
//                    break;
//                default:
//                    mWifiContentLayout.setVisibility(View.GONE);
//                    mWifiDisconnectedLayout.setVisibility(View.VISIBLE);
//                    mWifiOpenningLayout.setVisibility(View.GONE);
//                    //wifiSwitcher.setChecked(false);
//                    wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
//                    wifiSwitcher.setEnabled(true);
//                    mOpen = false;
//                    hint = R.string.wifi_error;
//                    break;
//            }
//
        }
    };



    public void onClickWifi(View view) {
        this.BluetoothSwitcher.setEnabled(false);
        if (mBluetooth_Enabler != null)
            mBluetooth_Enabler.onWiFiClick();
        mAdapter.notifyDataSetInvalidated();
    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
