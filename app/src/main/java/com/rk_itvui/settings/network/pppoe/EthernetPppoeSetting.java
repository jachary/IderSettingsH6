package com.rk_itvui.settings.network.pppoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.net.pppoe.PppoeManager;
import android.net.EthernetManager;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.FullScreenActivity;

public class EthernetPppoeSetting extends FullScreenActivity {
	public String USERINFO = "pppoeAccounts";
	private EditText userName;
	private EditText passWord;
	private String DEFAULT_PHY_IFACE = "ehernet";
	private String mIface = DEFAULT_PHY_IFACE;
	private EthernetManager mEthMgr;
	private int mPppoeState;
	//private PppoeManager mPppoeMgr;
	Button confirm;
	Button cancel;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
//				mPppoeState = intent.getIntExtra(
//						PppoeManager.EXTRA_PPPOE_STATE,
//						PppoeManager.PPPOE_STATE_DISCONNECTED);
//			} else {
//				Log.d("EthernetPppoe", "mPppoeState = " + mPppoeState);
//			}
//			refreshView();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethernetpppoe_setting);
		readAccounts();
		init();
	}

	public void init() {
//		mPppoeMgr = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);
//		mEthMgr = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
//		confirm = (Button) findViewById(R.id.confirm);
//		cancel = (Button) findViewById(R.id.cancel);
//		// filter.addAction(EthernetDataTracker.ETHERNET_STATE_CHANGED_ACTION);
//		mPppoeState = mPppoeMgr.getPppoeState();
//		// refreshView();
//		this.registerReceiver(mReceiver, filter);
	}

	public void refreshView() {
//		String summary = null;
//		TextView netstate = (TextView) findViewById(R.id.netState);
//		if (PppoeManager.PPPOE_STATE_DISCONNECTED == mPppoeState) {
//			confirm.setEnabled(true);
//			netstate.setText(R.string.pppoe_disconnected);
//			cancel.setText(R.string.pppoe_button_cancel); //
//			// netDialog("宸叉柇寮�");
//		} else if (PppoeManager.PPPOE_STATE_DISCONNECTING == mPppoeState) {
//			confirm.setEnabled(false);
//			netstate.setText(R.string.pppoe_disconnecting);
//			cancel.setText(R.string.pppoe_button_cancel);
//		} else if (PppoeManager.PPPOE_STATE_CONNECTING == mPppoeState) {
//			confirm.setEnabled(false);
//			netstate.setText(R.string.pppoe_connecting);
//			cancel.setText(R.string.pppoe_button_disconnect);
//		} else if (PppoeManager.PPPOE_STATE_CONNECTED == mPppoeState) {
//			confirm.setEnabled(false);
//			netstate.setText(R.string.pppoe_connected);
//			cancel.setText(R.string.pppoe_button_disconnect);
//			// netDialog("宸茶繛鎺�");
//		}
	}

	public void onButtonSave(View view) {
		// 淇濆瓨pppoe璐﹀彿锛屽瘑鐮侊紝骞惰繘琛屾嫧鍙�
		try {
			SharedPreferences perference = getSharedPreferences(USERINFO,Context.MODE_PRIVATE);
			Editor editor = perference.edit();
			editor.putString("username", userName.getText().toString());
			editor.putString("password", passWord.getText().toString());
			editor.commit();// 鏈皟鐢╟ommit鍓嶏紝鏁版嵁瀹為檯鏄病鏈夊瓨鍌ㄨ繘鏂囦欢涓殑銆� 璋冪敤鍚庯紝
		} catch (Exception e) {
			Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
		}

		PppoeEnable(true, userName.getText().toString(), passWord.getText().toString(), "", "");

	}

	public void PppoeEnable(boolean enable, String user, String pass,
			String dns1, String dns2) {
//		String iface = "eth0";
//		if (enable) {// 鎷ㄥ彿
//                       /*
//			if ((mEthMgr.getEthernetIfaceState() != EthernetDataTracker.ETHER_IFACE_STATE_UP)|| (mEthMgr.getEthernetCarrierState() != 1)) {
//				Toast.makeText(this, R.string.pppoe_connect_eth_first,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}*/
//			confirm.setEnabled(false); //鎸夐挳鍙樼伆
//			mPppoeMgr.stopPppoe();
//			mPppoeMgr.setupPppoe(user, iface, dns1, dns2, pass);
//			mPppoeMgr.startPppoe();
//
//		} else { // 鏂紑
//			if (mPppoeState == PppoeManager.PPPOE_STATE_CONNECTED) {
//				mPppoeMgr.stopPppoe();
//			}
//		}
	}

	public void onButtonCancel(View view) {
//		if (mPppoeState == PppoeManager.PPPOE_STATE_CONNECTED) { // 濡傛灉褰撳墠涓鸿繛鎺ョ姸鎬侊紝鎸夐挳涓烘柇寮�
//			mPppoeMgr.stopPppoe();
//			// 涓轰簡搴斾粯绯荤粺涓殑涓�涓猙ug锛岄渶瑕佽繖鏍�
//		} else {
//			finish();
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	/*鎵撳紑搴旂敤鏃讹紝璇诲彇鏈湴鐢ㄦ埛淇℃伅璁剧疆 */
	public void readAccounts() {
		userName = (EditText) findViewById(R.id.userNameValue);
		passWord = (EditText) findViewById(R.id.passWordValue);
		SharedPreferences perference = getSharedPreferences(USERINFO,
				Context.MODE_PRIVATE);
		userName.setText(perference.getString("username", ""));
		passWord.setText(perference.getString("password", ""));
	}

	public void netDialog(String info) {
		new AlertDialog.Builder(this).setMessage(info)
				.setPositiveButton("纭畾", null).show();
	}

}
