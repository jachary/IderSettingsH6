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

public class SettingListAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater mLayoutInflater;
	private List<ListItem> mItemList;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public SettingListAdapter(Context context, List<ListItem> itemList) {
		super(context, 0, itemList);
		mItemList = itemList;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public List<ListItem> getmItemList() {
		return mItemList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListHoder listHoder;
		if(convertView==null){
			convertView = mLayoutInflater.inflate(R.layout.setting_listitem, null);
			listHoder = new ListHoder();
			listHoder.imgIcon = (ImageView)convertView.findViewById(R.id.list_icon);
			listHoder.txtTitle = (TextView)convertView.findViewById(R.id.list_title);
			listHoder.txtDetail = (TextView)convertView.findViewById(R.id.list_detail);
			convertView.setTag(listHoder);
		}else{
			listHoder = (ListHoder)convertView.getTag();
		}
		ListItem item = getItem(position);
		listHoder.imgIcon.setImageResource(item.getIconRes());
		listHoder.txtTitle.setText(item.getTitle());
		listHoder.txtDetail.setText(item.getDetail());
		return convertView;
	}
	
	public static class ListHoder{
		public ImageView imgIcon;
		public TextView txtTitle;
		public TextView txtDetail;
	}

}
