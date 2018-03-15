/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��28�� ����6:21:49  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��28��      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.language;

import java.util.List;

import com.zxy.idersettings.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageArrayAdapter extends ArrayAdapter<Language> {

	private LayoutInflater mLayoutInflater;
	private List<Language> mItemList;

	public LanguageArrayAdapter(Context context, List<Language> itemList) {
		super(context, 0, itemList);
		mItemList = itemList;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public List<Language> getmItemList() {
		return mItemList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LanguageHoder upgradeHoder;
		if(convertView==null){
			convertView = mLayoutInflater.inflate(R.layout.setting_language_listitem, null);
			upgradeHoder = new LanguageHoder();
			upgradeHoder.imgIcon = (ImageView)convertView.findViewById(R.id.list_icon);
			upgradeHoder.txtTitle = (TextView)convertView.findViewById(R.id.list_title);
			convertView.setTag(upgradeHoder);
		}else{
			upgradeHoder = (LanguageHoder)convertView.getTag();
		}
		Language item = getItem(position);
		upgradeHoder.imgIcon.setImageResource(item.getIconRes());
		upgradeHoder.txtTitle.setText(item.getLabel());
		return convertView;
	}
	
	public static class LanguageHoder{
		public ImageView imgIcon;
		public TextView txtTitle;
	}

}
