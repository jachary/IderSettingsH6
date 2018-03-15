package com.rk_itvui.settings.developer;


import android.view.View;
import android.graphics.Bitmap;

import com.rk_itvui.settings.developer.SettingItemClick;

// author:hh@rock-chips.com
public class SettingItem
{
	public int mLevel = 0;

	public int mParentId = -1;

	public int mId = -1;

	public boolean mClick = true;

	public View mView = null;
	
	public SettingItemClick mSettingItemClick = null;

	public boolean mAdd = false; 

	private String mSummary = null;
	private String mStatusText = null;
//	private Drawable mDrawable = null;
	private Bitmap mBitmap = null;

	public String mText = null;
	
	public SettingItem(int level,int parent,int id)
	{
		mLevel = level;
		mParentId = parent;
		mId = id;
	}

	public SettingItem(int level,int parent,int id,String text,boolean add)
	{
		mLevel = level;
		mParentId = parent;
		mId = id;
		mText = text;
		mAdd = add;
	}

	public SettingItem(int level,int parent,int id,String status,String summary,Bitmap drawable)
	{
		mLevel = level;
		mParentId = parent;
		mId = id;
		mStatusText = status;
		mSummary = summary;
		mBitmap = drawable;
	}
	
	public void setClickable(boolean canClick)
	{
		mClick = canClick;
	}

	public boolean getClickable()
	{
		return mClick;
	}

	public void setTitle(String text)
	{
		mText = text;
	}
	
	public SettingItem setView(View view)
	{
		mView = view;
		mView.setTag(mId);
		return this;
	}

	public View getView()
	{
		return mView;
	}


	public boolean isAdd()
	{
		return mAdd;
	}

	public void setAddFlag(boolean add)
	{
		mAdd = add;
	}

	public void setOnSettingItemClick(SettingItemClick itemClick)
	{
		mSettingItemClick = itemClick;
	}

	public boolean onSettingItemClick(int id)
	{
		if((mClick) && (mSettingItemClick != null))
		{
			mSettingItemClick.onItemClick(this,id);
			return true;
		}

		return false;
	}

	public boolean onSettingItemLongClick(int id)
	{
		if((mClick) && (mSettingItemClick != null))
		{
			mSettingItemClick.onItemLongClick(this,id);
			return true;
		}

		return false;
	}
	
	public int getId()
	{
		return mId;
	}

	public String getStatus()
	{
		return mStatusText;
	}

	public void setStatus(String    text)
	{
		mStatusText = text;
	}

	public void setSummary(String summary)
	{
		mSummary = summary;
	}

	public String getSummary()
	{
		return mSummary;
	}
	
	public Bitmap getDrawable()
	{
		return mBitmap;
	}

	public void setDrawable(Bitmap drawable)
	{
		mBitmap = drawable;
	}
}
