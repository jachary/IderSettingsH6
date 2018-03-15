package com.rk_itvui.settings.datetime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.ScreenInformation;
import com.rk_itvui.settings.developer.SettingItem;
import com.rk_itvui.settings.developer.SettingMacroDefine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class RemindViewAdapter extends SimpleAdapter {  

        private List<? extends Map<String, ?>> mData;  
        private int mResource;  
        private Context mContext = null;
        private LayoutInflater flater = null;
        private String[] mFrom;  
    	private int mLevel = 0;
        private int[] mTo;  
        private int mParentId = -1;
        private LayoutInflater mInflater;  
  
  
        private ViewBinder mViewBinder;  
 
        /**  
         * @param context 
         * @param data 
         * @param resource 
         * @param from 
         * @param to 
         * @param newTextItem 
         * @see android.widget.SimpleAdapter 
         */  
        public RemindViewAdapter(Context context,  
                List<? extends Map<String, ?>> data, int resource,  
                String[] from, int[] to) {  
  
            super(context, data, resource, from, to);  
            mData = data;  
            mResource = resource;  
            mFrom = from;  
            mTo = to;  
            mInflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        }  
  
        public int getCount() {  
  
            return mData.size();  
        }  
        public Object getItem(int position) {  
  
            return mData.get(position);  
        }  
        public long getItemId(int position) {  
  
            return position;  
        }  
        
    	public int getParentId()
    	{
    		return mParentId;
    	}
        private int mIndicator = 0;
    	
        
        /*(public View getView(int position, View convertView, ViewGroup parent) 
    	{

    		return null;
    	}*/
        
    	private Bitmap bitMapScale(Bitmap map,float scaleParameter)
    	{
    		if(map == null)  return null;
    		
    		float scale = ScreenInformation.mScreenWidth/1280f*scaleParameter;
    		int width = (int)((float)map.getWidth()*scale);
    		int height = (int)((float)map.getHeight()*scale);

     		Bitmap resize = Bitmap.createScaledBitmap(map, width, height, true);
    		return resize;
    	}
  
 
  
    }  