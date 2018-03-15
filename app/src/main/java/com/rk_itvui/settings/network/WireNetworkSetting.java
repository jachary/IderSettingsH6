package com.rk_itvui.settings.network;

import java.util.HashMap;
import com.rk_itvui.settings.FullScreenActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
//import android.net.pppoe.PppoeManager;
import android.net.EthernetManager;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.network.pppoe.EthernetPppoeSetting;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

public class WireNetworkSetting extends FullScreenActivity implements OnItemClickListener {
//	private PppoeManager mPppoeMgr;
//	private int mPppoeState;
//	private String mPppoeSetting;
//	private String mDHCPSetting;
	private String mStaticIPSetting;
	private ListView listView;
	
	private String[] list;
	private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	final RadioAdapter adapter = new RadioAdapter(this);
	
	public void init(){		
//		mPppoeSetting=this.getResources().getString(R.string.pppoe_setting);
//		mDHCPSetting=this.getResources().getString(R.string.DHCP_setting);
		mStaticIPSetting=this.getResources().getString(R.string.StatiIP_setting);
//                String[] list_1 = { mPppoeSetting, mDHCPSetting, mStaticIPSetting, };
		String[] list_1 = {mStaticIPSetting };
		list=list_1;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wirenetwork_setting);
		init();
		listView = (ListView) findViewById(R.id.wireNetworkListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);//监听listview点击事件
		initRadioChecked();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}

	void initRadioChecked() { //
		EthernetIP ethernetIP = new EthernetIP();
		ethernetIP.transContext(this);
//		mPppoeMgr = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);
//		mPppoeState = mPppoeMgr.getPppoeState();
//		if(PppoeManager.PPPOE_STATE_CONNECTED == mPppoeState)
//		{
//			map.put(0, 100);
//			return;
//		}
		
		if (ethernetIP.isUsingStaticIp()) {
			map.put(2, 100);
		} else {
			map.put(1, 100);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		// TODO Auto-generated method stub
		map.clear();
		map.put(arg2, 100);
		adapter.notifyDataSetChanged();
		switch (arg2) {
//		case 0:
//			Intent intent_pppoeSetting = new Intent(this,
//					EthernetPppoeSetting.class);
//			startActivity(intent_pppoeSetting);
//			break;
//		case 1:
//			Intent intent_DHCPSetting = new Intent(this,
//					EthernetDHCPInfoSetting.class);
//			startActivity(intent_DHCPSetting);
//			break;
		case 0:
			Intent intent_staticIPSetting = new Intent(this,
					EthernetStaticIPSetting.class);
			startActivity(intent_staticIPSetting);
			break;
		}
	}

	class RadioHolder {
		private RadioButton radio;
		private TextView item;

		public RadioHolder(View view) {
			this.radio = (RadioButton) view.findViewById(R.id.item_radio);
			this.item = (TextView) view.findViewById(R.id.item_text);
		}
	}

	class RadioAdapter extends BaseAdapter {

		private Context context;

		public RadioAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list[arg0];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RadioHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.wirenetwork_item, null);
				holder = new RadioHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (RadioHolder) convertView.getTag();
			}
			holder.radio.setChecked(map.get(position) == null ? false : true);
			holder.item.setText(list[position]);
			return convertView;
		}
	}

}
