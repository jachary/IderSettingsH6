package com.rk_itvui.settings.network;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxy.idersettings.R;

public class WirelessNetworkFragment extends Fragment {

	private View wirelessNetworkFragmentLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		wirelessNetworkFragmentLayout = inflater.inflate(
				R.layout.wirelessnetwork_fragment, container, false);
		return wirelessNetworkFragmentLayout;
	}
	
	public WirelessNetworkFragment (){
		
	}
	public WirelessNetworkFragment (int index){
		
	}
	public void updateView() {

	}

	public void checkNetworkMode() {
		TextView ipAddr = (TextView) wirelessNetworkFragmentLayout
				.findViewById(R.id.ipAddrValue);
		TextView netMask = (TextView) wirelessNetworkFragmentLayout
				.findViewById(R.id.netMaskValue);
		TextView gateWay = (TextView) wirelessNetworkFragmentLayout
				.findViewById(R.id.gateWayValue);
		TextView dns1 = (TextView) wirelessNetworkFragmentLayout
				.findViewById(R.id.masterDNSValue);
		TextView dns2 = (TextView) wirelessNetworkFragmentLayout
				.findViewById(R.id.backupDNSValue);
	}

}
