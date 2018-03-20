package com.rk_itvui.settings.adapter;

import java.util.List;
import java.util.Set;

import com.rk_itvui.settings.bluetooth.LocalBluetoothDevice;
import com.zxy.idersettings.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothAdapter extends BaseAdapter{
	
	private LayoutInflater mLayoutInflater;
	private List<LocalBluetoothDevice> bluetoothList;
	
	
	public BluetoothAdapter(Context context, List<LocalBluetoothDevice> bluetoothList) {
		this.mLayoutInflater=LayoutInflater.from(context);
		this.bluetoothList=bluetoothList;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bluetoothList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bluetoothList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=mLayoutInflater.inflate(R.layout.bluetooth_item, null);
			viewHolder.bluetoothNameTextView=(TextView) convertView.findViewById(R.id.bluetooth_name_tv);
			viewHolder.bluetoothMacTextView=(TextView) convertView.findViewById(R.id.bluetooth_mac_tv);
			convertView.setTag(viewHolder);
			
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		LocalBluetoothDevice bluetoothInfo=bluetoothList.get(position);
		viewHolder.bluetoothNameTextView.setText(bluetoothInfo.getName());
		viewHolder.bluetoothMacTextView.setText(bluetoothInfo.getAddress());
		return convertView;
	}
	
	public static class ViewHolder{
		public TextView bluetoothNameTextView;
		public TextView bluetoothMacTextView;
		
	}
	
	

}
