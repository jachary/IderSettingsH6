package com.rk_itvui.settings.network.wifi;

import com.rk_itvui.settings.FullScreenActivity;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.util.Log;
import android.content.pm.PackageManager;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Bundle;
import android.security.Credentials;
import android.security.KeyStore;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;
import android.widget.Button;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiInfo;
import android.net.wifi.ScanResult;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WpsInfo;
import android.os.Build;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.zxy.idersettings.R;

public class Wifi_setting  extends FullScreenActivity implements
                              DialogInterface.OnClickListener {

	private static final String TAG = "WifiSettings";
	private static final int MENU_ID_SCAN = Menu.FIRST;
	private static final int MENU_ID_ADVANCED = Menu.FIRST + 1;
	private static final int MENU_ID_CONNECT = Menu.FIRST + 2;
	private static final int MENU_ID_FORGET = Menu.FIRST + 3;
	private static final int MENU_ID_MODIFY = Menu.FIRST + 4;

	private final IntentFilter mFilter;
	private final BroadcastReceiver mReceiver;
	private final Scanner mScanner;

	private boolean mP2pSupported;
	private WifiManager mWifiManager;
	private Wifi_Enabler mWifi_Enabler;

	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private int mLastPriority;

	private boolean mResetNetworks = false;
	private int mKeyStoreNetworkId = -1;
	private AccessPoint mSelected;
	Runnable mTimeRunnable;
	private Wifi_Dialog mDialog;

	private ListView mListView = null;
	ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
	WifiScanListViewAdapter mAdapter = null;
	private LayoutInflater flater = null;
	private LinearLayout mView = null;

	private WifiManager.ActionListener mConnectListener;
	private WifiManager.ActionListener mSaveListener;
	private WifiManager.ActionListener mForgetListener;
	// private boolean mP2pSupported;
	private WifiUICallBack mCallBack;


	// should activity finish once we have a connection?
	private boolean mAutoFinishOnConnection;

	private AtomicBoolean mConnected = new AtomicBoolean(false);

	// this boolean extra specifies whether to auto finish when connection is
	// established
	private static final String EXTRA_AUTO_FINISH_ON_CONNECT = "wifi_auto_finish_on_connect";

	public Wifi_setting() {
		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(intent);
				LOGD("wifi setting handleevent");
			}
		};

		mScanner = new Scanner();
	}

	private void createListView() {
		flater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new WifiScanListViewAdapter(this, accessPoints);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mListItemClickListener);
		mListView.requestFocus();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_layout);

		mP2pSupported = getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_WIFI_DIRECT);
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		mConnectListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Toast.makeText(Wifi_setting.this,
						R.string.wifi_failed_connect_message,
						Toast.LENGTH_SHORT).show();
			}
		};

		mSaveListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Toast.makeText(Wifi_setting.this,
						R.string.wifi_failed_save_message, Toast.LENGTH_SHORT)
						.show();
			}
		};

		mForgetListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Toast.makeText(Wifi_setting.this,
						R.string.wifi_failed_forget_message, Toast.LENGTH_SHORT)
						.show();
			}
		};
		mCallBack = new WifiUICallBack();
		createListView();
		getWifiCurrentState();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}

	private void updateAccessPoints() {
		LOGD("updateAccessPoints****************");
		if (accessPoints != null) {
			accessPoints.clear();
		}
		List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		if (configs != null) {
			mLastPriority = 0;
			for (WifiConfiguration config : configs) {
				if (config.priority > mLastPriority) {
					mLastPriority = config.priority;
				}

				// Shift the status to make enableNetworks() more efficient.
				if (config.status == Status.CURRENT) {
					config.status = Status.ENABLED;
				} else if (mResetNetworks && config.status == Status.DISABLED) {
					config.status = Status.CURRENT;
				}

				AccessPoint accessPoint = new AccessPoint(this, config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoint.setCallBack(mCallBack);
				accessPoints.add(accessPoint);
			}
		}

		List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : accessPoints) {
					if (accessPoint.update(result)) {
						found = true;
					}
				}
				if (!found) {
					accessPoints.add(new AccessPoint(this, result));
				}
			}
		}

		Collections.sort(accessPoints);

		AccessPoint access = new AccessPoint(this, getResources().getString(
				R.string.wifi_add_network));
		if (accessPoints.size() > 0) {
			accessPoints.add(0, access);
		} else {
			accessPoints.add(access);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void LOGD(String msg) {
		if (true)
			Log.d("Wifi Settings", msg);
	}

	private void handleEvent(Intent intent) {
		LOGD("handleEvent(), action = " + intent.getAction());
		String action = intent.getAction();
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
				|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
						.equals(action)
				|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
			updateAccessPoints();
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			// Ignore supplicant state changes when network is connected
			// TODO: we should deprecate SUPPLICANT_STATE_CHANGED_ACTION and
			// introduce a broadcast that combines the supplicant and network
			// network state change events so the apps dont have to worry about
			// ignoring supplicant state change when network is connected
			// to get more fine grained information.
			SupplicantState state = (SupplicantState) intent
					.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
				updateConnectionState(WifiInfo.getDetailedStateOf(state));
			}
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mConnected.set(info.isConnected());
			// changeNextButtonState(info.isConnected());
			updateAccessPoints();
			updateConnectionState(info.getDetailedState());
			if (mAutoFinishOnConnection && info.isConnected()) {
				// if (activity != null)
				{
					Wifi_setting.this.setResult(Activity.RESULT_OK);
					Wifi_setting.this.finish();
				}
				return;
			}
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			updateConnectionState(null);
		}
	}

	private void updateWifiState(int state) {
		if (state == WifiManager.WIFI_STATE_ENABLED) {
			mScanner.resume();
			updateAccessPoints();
		} else {
			mScanner.pause();
			// mAccessPoints.removeAll();
		}
	}

	private void updateConnectionState(DetailedState state) {
		/* sticky broadcasts can call this when wifi is disabled */
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}

		mLastInfo = mWifiManager.getConnectionInfo();
		if (state != null) {
			mLastState = state;
		}

		for (int i = accessPoints.size() - 1; i >= 0; --i) {
			accessPoints.get(i).update(mLastInfo, mLastState);
		}

		if (mResetNetworks
				&& (state == DetailedState.CONNECTED
						|| state == DetailedState.DISCONNECTED || state == DetailedState.FAILED)) {
			updateAccessPoints();
			enableNetworks();
		}
	}

	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if (position == 0) {
				LOGD("AdapterView.OnItemClickListener mListItemClickListener, position = 0, showDialog(null, true)");
				showDialog(null, true);
			} else {
				mSelected = accessPoints.get(position);
				showDialog(mSelected, false);
			}
		}
	};

	void submit() {

		final WifiConfiguration config = mDialog.getConfig();

		if (config == null) {
			if (mSelected != null && mSelected.networkId != INVALID_NETWORK_ID) {
				mWifiManager.connect(mSelected.networkId, mConnectListener);
			}
		} else if (config.networkId != INVALID_NETWORK_ID) {
			if (mSelected != null) {
				mWifiManager.save(config, mSaveListener);
			}
		} else {
			if (mDialog.isEdit()) {
				mWifiManager.save(config, mSaveListener);
			} else {
				mWifiManager.connect(config, mConnectListener);
			}
		}

		if (mWifiManager.isWifiEnabled()) {
			mScanner.resume();
		}
		updateAccessPoints();

	}

	void forget() {
		if (mSelected.networkId == INVALID_NETWORK_ID) {
			// Should not happen, but a monkey seems to triger it
			Log.e(TAG,
					"Failed to forget invalid network " + mSelected.getConfig());
			return;
		}

		mWifiManager.forget(mSelected.networkId, mForgetListener);

		if (mWifiManager.isWifiEnabled()) {
			mScanner.resume();
		}
		updateAccessPoints();
	}

	private void enableNetworks() {
		mResetNetworks = false;
	}

	private void saveNetworks() {
		// Always save the configuration with all networks enabled.
		enableNetworks();
		mWifiManager.saveConfiguration();
		updateAccessPoints();
	}
	
	public void onClick(DialogInterface dialogInterface, int button) {

		if (button == Wifi_Dialog.BUTTON_FORGET && mSelected != null) {
			forget();
		} else if (button == Wifi_Dialog.BUTTON_SUBMIT) {
			submit();
		}
	}

	private void showDialog(AccessPoint accessPoint, boolean edit) {

		if (mDialog != null) {
			mDialog.dismiss();
		}
		mDialog = new Wifi_Dialog(this, this, accessPoint, edit);
		mDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mFilter);
		if (mKeyStoreNetworkId != INVALID_NETWORK_ID
				&& KeyStore.getInstance().state() != KeyStore.State.UNLOCKED) { // KeyStore.getInstance().test()
												// ==
												// KeyStore.NO_ERROR
			mWifiManager.connect(mKeyStoreNetworkId, mConnectListener);
		}
		mKeyStoreNetworkId = INVALID_NETWORK_ID;

		updateAccessPoints();
	}

	protected void onPause() {
		super.onPause();

		unregisterReceiver(mReceiver);
		mScanner.pause();
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}

		if (mResetNetworks) {
			enableNetworks();
		}
	}

	public class WifiUICallBack implements CallBack {
		public void onCallBack() {
			if ((mListView != null) && (mAdapter != null)) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void pause() {
			mRetry = 0;
			// mAccessPoints.setProgress(false);
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			if (mWifiManager.startScan()) {
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(Wifi_setting.this, R.string.wifi_fail_to_scan,
						Toast.LENGTH_LONG).show();
				return;
			}
			// mAccessPoints.setProgress(mRetry != 0);
			sendEmptyMessageDelayed(0, 6000);
		}
	}
	
/***add by blb***/
    private LinearLayout mWifiContentLayout;
    private LinearLayout mWifiDisconnectedLayout;
    private LinearLayout mWifiOpenningLayout;
	
	///private Switch wifiSwitcher;  
    private Button wifiSwitcher;
    private void initSwitcher(){
        wifiSwitcher= (Button) findViewById(R.id.switcher);  	
//		wifiSwitcher.setOnCheckedChangeListener(listener);  
		/* 娴犮儰绗呮稉澶庮攽娑撹桨绨￠崝鐘插弳娑擃參妫块悩鑸碉拷锟� */
	mWifiContentLayout = (LinearLayout) findViewById(R.id.wifi_list_content);
	mWifiDisconnectedLayout = (LinearLayout) findViewById(R.id.wifi_list_content_closed);
	mWifiOpenningLayout = (LinearLayout) findViewById(R.id.wifi_enable_wait);
    }
	

    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {      
        @Override  
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
        } 
    };
	private void getWifiCurrentState() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
			mWifi_Enabler = new Wifi_Enabler(this, mHandler);
			if (mWifi_Enabler != null) {
				mWifi_Enabler.resume();
				initSwitcher();
				//wifiSwitcher.setChecked(mWifi_Enabler.getCurrentState());//閸掓繂顫愰崠鏍х磻閸忓磭濮搁幀锟�
				if(mWifi_Enabler.getCurrentState())
				  wifiSwitcher.setBackgroundResource(R.drawable.switch_on);
				else
				  wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
			}
		}
	}

	public void onClickWifi(View view) {
		this.wifiSwitcher.setEnabled(false);//缁涘绶熼悩鑸碉拷浣呵旂�规矮浜掗崥搴㈠娴兼瓱nable
		if (mWifi_Enabler != null)
			mWifi_Enabler.onWiFiClick();
			mAdapter.notifyDataSetInvalidated();
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		mWifi_Enabler.pause();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			LOGD("mHandler,handleMessage() msg.what = " + msg.what);	
			boolean mOpen;
			int hint;
			switch (msg.what) {
			case WifiManager.WIFI_STATE_ENABLING: //娑擃參妫块悩鑸碉拷锟�
				//wifiSwitcher.setChecked(true); 
				wifiSwitcher.setBackgroundResource(R.drawable.switch_on);
				wifiSwitcher.setEnabled(false); 
				mWifiContentLayout.setVisibility(View.GONE);
				mWifiDisconnectedLayout.setVisibility(View.GONE);
				mWifiOpenningLayout.setVisibility(View.VISIBLE);
				
				hint = R.string.wifi_starting;
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				//wifiSwitcher.setChecked(true);
				wifiSwitcher.setBackgroundResource(R.drawable.switch_on);
				wifiSwitcher.setEnabled(true);
				mWifiContentLayout.setVisibility(View.VISIBLE);
				mWifiDisconnectedLayout.setVisibility(View.GONE);
				mWifiOpenningLayout.setVisibility(View.GONE);
				
				mOpen = true; // 閹垫挸绱戦幋鎰
				hint = R.string.turn_on;
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				//wifiSwitcher.setChecked(false);
				wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
				wifiSwitcher.setEnabled(false);
				mWifiContentLayout.setVisibility(View.VISIBLE);
				mWifiDisconnectedLayout.setVisibility(View.GONE);
				mWifiOpenningLayout.setVisibility(View.GONE);
				mOpen = false; // 閸忔娊妫存稉锟�
				hint = R.string.wifi_stopping;
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				//wifiSwitcher.setChecked(false);
				wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
				wifiSwitcher.setEnabled(true);
				mWifiContentLayout.setVisibility(View.GONE);
				mWifiDisconnectedLayout.setVisibility(View.VISIBLE);
				mWifiOpenningLayout.setVisibility(View.GONE);
				
				mOpen = false; // 瀹告彃鍙ч梻锟�
				hint = R.string.turn_off;
				break;
			default:
				mWifiContentLayout.setVisibility(View.GONE);
				mWifiDisconnectedLayout.setVisibility(View.VISIBLE);
				mWifiOpenningLayout.setVisibility(View.GONE);
				//wifiSwitcher.setChecked(false);
				wifiSwitcher.setBackgroundResource(R.drawable.switch_off);
				wifiSwitcher.setEnabled(true);
				mOpen = false;
				hint = R.string.wifi_error;
				break;
			}

		}
	};
	
	public void updateWifiConnectionState(String text)  //閺囧瓨鏌婃潻鐐村复閻樿埖锟斤拷
	{
		TextView netState;
		netState=(TextView)this.findViewById(R.id.netState);
		netState.setText(text);		
	}
	public void AlertOpenError(){
		Toast.makeText(this, R.string.wifi_open_error,
				Toast.LENGTH_SHORT).show();
	}
}
