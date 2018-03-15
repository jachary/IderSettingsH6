package com.rk_itvui.settings.network.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import com.rk_itvui.settings.FullScreenActivity;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.developer.ListViewAdapter;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

public class WifiAp_Settings extends FullScreenActivity implements
		AdapterView.OnItemSelectedListener, OnClickListener, TextWatcher {

	public static final int OPEN_INDEX = 0;
	public static final int WPA_INDEX = 1;
	public static final int WPA2_INDEX = 2;

	private TextView mSsid;
	private int mSecurityType = OPEN_INDEX;
	private EditText mPassword;
	private Spinner mSecurity;
	private WifiManager mWifiManager;
	WifiConfiguration mWifiConfig;
	private boolean mOpen = false;
	private Switch apSwitcher;
	private Button mSwitch = null;
	private TextView netState;
    private Wifi_ApEnabler mWifi_ApEnabler;

	private LinearLayout ApContentLayout;
	private LinearLayout ApDisconnectedLayout;
	private LinearLayout ApOpenningLayout;

	boolean isClickButton = false;

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 瑕佸仛鐨勪簨鎯�
			setSwitchState(1000);// 鍙戠敓浜嗛敊璇�
		}
	};

	// ListView鐨凙dapter
	private ListViewAdapter mListViewAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifiap_layout);
		init();
		getWifiApCurrentState();
		getStoredAp();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}

	private void getWifiApCurrentState() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)
				&& !getPackageManager().hasSystemFeature(
						"android.setting.portable_hotspot")) {
			mWifi_ApEnabler = new Wifi_ApEnabler(this, mHandler);
			mWifi_ApEnabler.resume();
		}
	}

	public void setSwitchState(int state) {
		handler.removeCallbacks(runnable);// 鍏抽棴瀹氭椂鍣�
		switch (state) {
		case WifiManager.WIFI_AP_STATE_ENABLING:
			// 寮�鍚腑锛岀姸鎬佷负寮�锛屼絾鏄鑹蹭负鐏拌壊
			//this.apSwitcher.setChecked(true);
			mSwitch.setBackgroundResource(R.drawable.switch_off);
			this.mSwitch.setEnabled(false);
			/* 涓荤晫闈㈠簲涓烘墦寮�绛夊緟鐘舵�� */
			ApContentLayout.setVisibility(View.GONE);
			ApDisconnectedLayout.setVisibility(View.GONE);
			ApOpenningLayout.setVisibility(View.VISIBLE);
			break;
		case WifiManager.WIFI_AP_STATE_ENABLED:
			//this.apSwitcher.setChecked(true);
			mSwitch.setBackgroundResource(R.drawable.switch_on);
			this.mSwitch.setEnabled(true);
			/* 涓荤晫闈㈠簲涓烘樉绀洪厤缃姸鎬� */
			ApContentLayout.setVisibility(View.VISIBLE);
			ApDisconnectedLayout.setVisibility(View.GONE);
			ApOpenningLayout.setVisibility(View.GONE);
			mOpen = true;
			break;
		case WifiManager.WIFI_AP_STATE_DISABLING:
			//this.apSwitcher.setChecked(false);
			mSwitch.setBackgroundResource(R.drawable.switch_off);
			this.mSwitch.setEnabled(false);
		  mOpen = false;
			/* 鍜屽紑鍚姸鎬佺浉鍚� */
			ApContentLayout.setVisibility(View.VISIBLE);
			ApDisconnectedLayout.setVisibility(View.GONE);
			ApOpenningLayout.setVisibility(View.GONE);

			break;
		case WifiManager.WIFI_AP_STATE_DISABLED:
			if (!isClickButton) {          //涓轰簡澶勭悊鐐瑰嚮纭鎸夐挳浠ュ悗鍑虹幇鍏抽棴椤甸潰鐨勬儏鍐�
				//this.apSwitcher.setChecked(false);
				mSwitch.setBackgroundResource(R.drawable.switch_off);
				this.mSwitch.setEnabled(true);
				/* 鍏抽棴鐘舵�� */
				ApContentLayout.setVisibility(View.GONE);
				ApDisconnectedLayout.setVisibility(View.VISIBLE);
				ApOpenningLayout.setVisibility(View.GONE);
				mOpen = false;
			}else{
				isClickButton=false;
			}
			break;
		default:
			// Log.d("blb","============================================ap妯″紡閿欒");
			//this.apSwitcher.setChecked(false);
			mSwitch.setBackgroundResource(R.drawable.switch_off);
			this.mSwitch.setEnabled(true);
			/* 鍏抽棴鐘舵�� */
			ApContentLayout.setVisibility(View.GONE);
			ApDisconnectedLayout.setVisibility(View.VISIBLE);
			ApOpenningLayout.setVisibility(View.GONE);
			mOpen = false;
		}
	}

	void init() {
		mSecurity = ((Spinner) findViewById(R.id.wifi_securityValue));
		mSwitch = (Button) findViewById(R.id.switcher);
		//apSwitcher.setChecked(false); //榛樿涓哄叧闂姸鎬�
		mSwitch.setBackgroundResource(R.drawable.switch_off);
		mOpen = false;
		
		netState = (TextView) findViewById(R.id.netState);
		mSsid = (TextView) findViewById(R.id.netSsidValue);
		mPassword = (EditText) findViewById(R.id.passwordValue);
		/* 浠ヤ笅涓夎涓轰簡鍔犲叆涓棿鐘舵�� */
		ApContentLayout = (LinearLayout) findViewById(R.id.wifi_ap_setting_content);
		ApDisconnectedLayout = (LinearLayout) findViewById(R.id.tv_wifi_ap_content);
		ApOpenningLayout = (LinearLayout) findViewById(R.id.wifi_ap_wait);
		
		setSwitchState(WifiManager.WIFI_AP_STATE_DISABLED);//榛樿涓哄叧闂姸鎬�

	}

	void getStoredAp() {
		initStoredInfo();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			setSwitchState(msg.what);
		}
	};

	public void onClickAP(View view) {
		handler.postDelayed(runnable, 10000);// 鎵撳紑瀹氭椂鍣�,10绉掓敹涓嶅埌骞挎挱锛屽彂鍑哄け璐ヤ俊鎭�
		if (/*this.apSwitcher.isChecked()*/mOpen) { // 濡傛灉鏄墦寮�鐘舵�佷笅锛屽嵆鐐瑰嚮鎸夐挳鍚庢槸涓轰簡鍏抽棴
			ApContentLayout.setVisibility(View.GONE);
			ApDisconnectedLayout.setVisibility(View.GONE);
			ApOpenningLayout.setVisibility(View.VISIBLE);
		} else {// 濡傛灉鍏抽棴鐘舵�佷笅锛岀偣鍑绘寜閽槸涓轰簡鎵撳紑

		}
     	mWifi_ApEnabler.onPreferenceChange();
	}

	public WifiConfiguration getConfig() {

		WifiConfiguration config = new WifiConfiguration();

		config.SSID = mSsid.getText().toString();

		switch (mSecurityType) {
		case OPEN_INDEX:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			return config;

		case WPA_INDEX:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (mPassword.length() != 0) {
				String password = mPassword.getText().toString();
				config.preSharedKey = password;
			}
			return config;
		case WPA2_INDEX:
			config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (mPassword.length() != 0) {
				String password = mPassword.getText().toString();
				config.preSharedKey = password;
			}
			return config;
		}
		return null;
	}

	public static int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return WPA_INDEX;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return WPA2_INDEX;
		}
		return OPEN_INDEX;
	}

	protected void initStoredInfo() {
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiConfig = mWifiManager.getWifiApConfiguration();
		mSecurityType = getSecurityTypeIndex(mWifiConfig);

		if (mWifiConfig != null) {
			mSsid.setText(mWifiConfig.SSID);
			mSecurity.setSelection(mSecurityType);
			if (mSecurityType == WPA_INDEX || mSecurityType == WPA2_INDEX) {
				mPassword.setText(mWifiConfig.preSharedKey);
			}
			mSsid.addTextChangedListener(this);
			mPassword.addTextChangedListener(this);
			mSecurity.setOnItemSelectedListener(this);
			((CheckBox) findViewById(R.id.show_password))
					.setOnClickListener(this);
			showSecurityFields();
		}
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void afterTextChanged(Editable editable) {
		validate();
	}

	private void validate() {
		if ((mSsid != null && mSsid.length() == 0)
				|| (((mSecurityType == WPA_INDEX) || (mSecurityType == WPA2_INDEX)) && mPassword
						.length() < 8)) {

			findViewById(R.id.confirm).setEnabled(false);
			// getButton(BUTTON_SUBMIT).setEnabled(false);
		} else {
			findViewById(R.id.confirm).setEnabled(true);
			// getButton(BUTTON_SUBMIT).setEnabled(true);
		}
	}

	public void onItemSelected(AdapterView parent, View view, int position,
			long id) {
		mSecurityType = position;
		showSecurityFields();
		validate();
	}

	public void onNothingSelected(AdapterView parent) {
	}

	public void onClick(View view) {
		mPassword
				.setInputType(InputType.TYPE_CLASS_TEXT
						| (((CheckBox) view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
								: InputType.TYPE_TEXT_VARIATION_PASSWORD));
	}

	private void showSecurityFields() {
		/*
		if (mSecurityType == Access_Point.SECURITY_NONE) {
			findViewById(R.id.passwordRow).setVisibility(View.GONE);
			// findViewById(R.id.passwordValue).setVisibility(View.GONE);
			return;
		}*/
		findViewById(R.id.passwordRow).setVisibility(View.VISIBLE);
		findViewById(R.id.passwordValue).setVisibility(View.VISIBLE);
	}

	public void onButtonSave(View view) {
		isClickButton=true;
		mWifiConfig = this.getConfig();	
		if (mWifiConfig != null) {
			/**
			 * if soft AP is running, bring up with new config else update the
			 * configuration alone
			 */
			if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
				mWifiManager.setWifiApEnabled(null, false);
				mWifiManager.setWifiApEnabled(mWifiConfig, true);
				/**
				 * There is no tether notification on changing AP configuration.
				 * Update status with new config.
				 */
				updateConfigSummary(mWifiConfig);
			} else {
				mWifiManager.setWifiApConfiguration(mWifiConfig);
			}
		}
	}

	public void onButtonCancel(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	   mWifi_ApEnabler.pause();
	}

	void updateConfigSummary(WifiConfiguration wifiConfig) {
		String s = getString(com.android.internal.R.string.wifi_tether_configure_ssid_default);
		String summary = String.format(
				getString(R.string.wifi_tether_enabled_subtext),
				(wifiConfig == null) ? s : wifiConfig.SSID);
	}

}