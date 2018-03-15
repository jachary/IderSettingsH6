/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.adapter;

import java.util.List;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.model.ListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingGridAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater mLayoutInflater;
	private List<ListItem> mItemList;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public SettingGridAdapter(Context context, List<ListItem> itemList) {
		super(context, 0, itemList);
		mItemList = itemList;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public List<ListItem> getmItemList() {
		return mItemList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridHoder gridHoder;
		if(convertView==null){
			convertView = mLayoutInflater.inflate(R.layout.setting_griditem, null);
			gridHoder = new GridHoder();
			gridHoder.imgIcon = (ImageView)convertView.findViewById(R.id.list_icon);
			gridHoder.txtTitle = (TextView)convertView.findViewById(R.id.list_title);
			convertView.setTag(gridHoder);
		}else{
			gridHoder = (GridHoder)convertView.getTag();
		}
		ListItem item = getItem(position);
		gridHoder.imgIcon.setImageResource(item.getIconRes());
		gridHoder.txtTitle.setText(item.getTitle());
		return convertView;
	}
	
	public static class GridHoder{
		public ImageView imgIcon;
		public TextView txtTitle;
	}

}
