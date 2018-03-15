package com.rk_itvui.settings;

import android.graphics.drawable.Drawable;

public class SettingItems
{
	//app name
	private String mItemName = null;
	//pack name
	private String mPackageName = null;
	//activity name
	private String mActivityName = null;
	//icon
	private Drawable mIcon = null;
	private String mVersionName = null;
	private int mVersionCode = 0;
	
	public void setItemName(String name)
	{
		mItemName = name;
	}
	
	public void setPackageName(String packageName)
	{
		mPackageName = packageName;
	}	
	
	public void setIcon(Drawable drawable)
	{
		mIcon = drawable;
	}
	
	public String getItemName()
	{
		return mItemName;
	}
	
	public Drawable getIcon()
	{
		return mIcon;
	}
	

}