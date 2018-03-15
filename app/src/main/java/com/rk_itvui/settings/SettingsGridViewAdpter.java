package com.rk_itvui.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class SettingsGridViewAdpter extends PagerAdapter
{
       private static final String TAG = "SettingsGridViewAdpter:BaseAdapter";

	private Activity mActivity = null;
	private ArrayList<SettingItems> mList = null;
	private final int EDGE_PADDING = 2;
	private int mTextColor = Color.WHITE;
	private LayoutInflater flater = null;
	
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected int mItemCountPerPage = 1;
    /**
     * The listener that receives notifications when an item is clicked.
     */
    OnItemClickListener mOnItemClickListener;
    /**
     * The listener that receives notifications when an item is long clicked.
     */
    OnItemLongClickListener mOnItemLongClickListener;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected Context mContext;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected ChangeObserver mChangeObserver;
    
	public SettingsGridViewAdpter(Context context,Settings activity, ArrayList<SettingItems> list)
	{
		mActivity = activity;
		mList = list;
		flater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}
	

	void setTextColor(int color)
	{
		mTextColor = color;
	}
	
	public int getListCount() {
		// TODO Auto-generated method stub
		if(mList != null)
			return mList.size();		
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mList != null) {
			return mList.size() % mItemCountPerPage > 0 ? mList.size()
					/ mItemCountPerPage + 1 : mList.size() / mItemCountPerPage;
		}
		return 0;
	}
	
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(mList != null)
			return mList.get(position);

		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO 
		return arg0 == arg1;
	}


    public void setCountPerPage(int num) {
    	mItemCountPerPage = num > 0 ? num : 1;
    }
    
    public int getCountPerPage() {
    	return mItemCountPerPage;
    }
    
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    
    /**
     * Makes a new view to hold the data pointed to by cursor.
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is already
     * moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @param pageindex current page position.
     * @return the newly created view.
     */
    public abstract View newView(Context context, ViewGroup parent, int pageindex);
    
    /**
     * Bind an existing view to the data pointed to by cursor
     * @param view Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is already
     * moved to the correct position.
     * @param pageindex current page position.
     */
    public abstract void bindView(View view, Context context,int pageindex);
    
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(ViewPager parent, View view, int position);
    }
    
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in this view has been
         * clicked and held.
         *
         * Implementers can call getItemAtPosition(position) if they need to access
         * the data associated with the selected item.
         *
         * @param parent The AbsListView where the click happened
         * @param view The view within the AbsListView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         *
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(ViewPager parent, View view, int position);
    }
    
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }
    
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		View view = getView(position, null, container);
		container.addView(view);
		return view;
	}
	
    /**
     * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(mContext, parent,position);
        } else {
            v = convertView;
        }
        bindView(v,mContext,position);
        return v;
    }
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}
	
    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * The default implementation provides the auto-requery logic, but may be overridden by
     * sub classes.
     * 
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }
}


