package com.rk_itvui.settings.storeinfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.storage.IMountService;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageEventListener;
import android.provider.Settings.System;
import android.text.format.Formatter;
import java.io.File;
import android.content.Context;
import android.widget.TextView;

import com.rk_itvui.settings.FullScreenActivity;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.ScreenInformation;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import android.filterfw.core.CachedFrameManager;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.util.Log;


public class Storage extends FullScreenActivity
{
	private String mSdTotalSpace;
	private String mSdAvailableSpace;

	private String mNANDTotalSpace;
	private String mNANDTotalSpacetotal;
	private String mNANDAvailableSpace;

	private String mExternalAvailableSpace;

	private IMountService mMountService = null;
	private StorageManager mStorageManager = null;

	private TextView mSdCardTotal = null;
	private TextView mSdCardAvailable = null;
	private TextView mNandTotal = null;
	private TextView mNandTotaladd = null;
	private TextView mNandAvailable = null;
	//private TextView mInternalAvailable = null;

	/*private void createTitle()
	{
		ImageView image = (ImageView)findViewById(R.id.title_image);
		Bitmap resize = bitMapScale(R.drawable.display_small);
		image.setScaleType(ImageView.ScaleType.CENTER);
		image.setImageBitmap(resize);

		TextView title = (TextView)findViewById(R.id.title_text);
		title.setTextSize(ScreenInformation.mScreenWidth/25f*ScreenInformation.mDpiRatio);
	}*/

	private Bitmap bitMapScale(int id)
	{
		Bitmap map = BitmapFactory.decodeResource(this.getResources(),id);
		float scale = ScreenInformation.mScreenWidth/1280f*ScreenInformation.mDpiRatio;
		int width = (int)((float)map.getWidth()*scale);
		int height = (int)((float)map.getHeight()*scale);

 		Bitmap resize = Bitmap.createScaledBitmap(map, width, height, true);
		return resize;
	}

	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_CLEARABLE_FLAGS);
        setContentView(R.layout.storage);
		//createTitle();
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //ScreenInfo.DENSITY = displayMetrics.densityDpi;
        //ScreenInfo.WIDTH = displayMetrics.widthPixels;
        //ScreenInfo.HEIGHT = displayMetrics.heightPixels;

        ScreenInformation.mScreenWidth = displayMetrics.widthPixels;
        ScreenInformation.mScreenHeight = displayMetrics.heightPixels;
        ScreenInformation.mDensityDpi = displayMetrics.densityDpi;
        ScreenInformation.mDpiRatio = ((float) ScreenInformation.mDefaultDpi)
            / (float) displayMetrics.densityDpi;

		createContentTitle();
		setUpView();
		updateStatusForStorage();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
    }

	private void createContentTitle()
	{
		float size = ScreenInformation.mScreenWidth/52f*ScreenInformation.mDpiRatio;
		float off = 5f;

		TextView view_sdcard = (TextView)findViewById(R.id.sdcard);
		view_sdcard.setTextSize(size+off);

		TextView title = (TextView)findViewById(R.id.sdcard_total_title);
		title.setTextSize(size);

		TextView view = (TextView)findViewById(R.id.sdcard_available_title);
		view.setTextSize(size);

		if(getPackageManager().hasSystemFeature("android.settings.sdcard"))
		{
			view_sdcard.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		}

		view = (TextView)findViewById(R.id.nandflash);
		view.setTextSize(size+off);
		view = (TextView)findViewById(R.id.nand_total_title);
		view.setTextSize(size);
//		view = (TextView)findViewById(R.id.nand_available_title);
//		view.setTextSize(size);

		/*view = (TextView)findViewById(R.id.internal_flash);
		view.setTextSize(size+off);
		view = (TextView)findViewById(R.id.internal_available_title);
		view.setTextSize(size);*/
	}

	private void setUpView()
	{
		mSdCardTotal = (TextView)findViewById(R.id.sdcard_total);
		mSdCardAvailable = (TextView)findViewById(R.id.sdcard_available);
		mNandTotal = (TextView)findViewById(R.id.nand_total);
		mNandAvailable = (TextView)findViewById(R.id.nand_availabletotal);
		//mInternalAvailable = (TextView)findViewById(R.id.internal_available);
//		mNandTotaladd = (TextView)findViewById(R.id.nand_available);
		if(getPackageManager().hasSystemFeature("android.settings.sdcard"))
		{
			mSdCardTotal.setVisibility(View.GONE);
			mSdCardAvailable.setVisibility(View.GONE);
		}

		float size = ScreenInformation.mScreenWidth/52f*ScreenInformation.mDpiRatio;
		mSdCardTotal.setTextSize(size);
		mSdCardAvailable.setTextSize(size);
		mNandTotal.setTextSize(size);
		mNandAvailable.setTextSize(size);
		//mInternalAvailable.setTextSize(size);
	}

	StorageEventListener mStorageListener = new StorageEventListener() {
		@Override
		public void onStorageStateChanged(String path, String oldState, String newState) {
	//		Log.i(TAG, "Received storage state changed notification that " +
	//				path + " changed state from " + oldState + " to " + newState);
			updateStatusForStorage();
		}
	};

	protected void onResume()
	{
		super.onResume();
		if (mStorageManager == null) {
			mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
			mStorageManager.registerListener(mStorageListener);
		}
	}

	protected void onPause() {
		super.onPause();
		if (mStorageManager != null && mStorageListener != null) {
			mStorageManager.unregisterListener(mStorageListener);
		}
	}

	private void updateStatusForStorage()
	{
		if(!getPackageManager().hasSystemFeature("android.settings.sdcard"))
			updateStatusSDCard();

		updateStatusNandflash();
		//updateStatusInternal();
	}

	private String formatSize(long size) {
	    return Formatter.formatFileSize(this, size);
	}

	private void updateStatusSDCard(){
		String status = StorageUtils.getSDcardState();
		String readOnly = "";
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)) {
			status = Environment.MEDIA_MOUNTED;
			readOnly = getResources().getString(R.string.read_only);
		}

		// Calculate the space of SDcard
		if (Environment.MEDIA_MOUNTED.equals(status)) {
			try {
				String path = StorageUtils.getSDcardDir();
				StatFs stat = new StatFs(path);
				long blockSize = stat.getBlockSize();
				long totalBlocks = stat.getBlockCount();
				long availableBlocks = stat.getAvailableBlocks();
				mSdTotalSpace=formatSize(totalBlocks * blockSize);
				mSdAvailableSpace=formatSize(availableBlocks * blockSize) + readOnly;
			} catch (IllegalArgumentException e) {
				status = Environment.MEDIA_REMOVED;
			}
		}else{
			mSdTotalSpace=getResources().getString(R.string.status_unavailable);
			mSdAvailableSpace=getResources().getString(R.string.status_unavailable);
		}

		mSdCardTotal.setText(mSdTotalSpace);
		mSdCardAvailable.setText(mSdAvailableSpace);
	}
	void readSystem() {  
        File root = Environment.getRootDirectory();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSize();  
        long blockCount = sf.getBlockCount();  
        long availCount = sf.getAvailableBlocks();  
        
        Log.d("------", "storage system"+root.getPath());
	}
	private void updateStatusNandflash(){

        // Calculate the space of SDcard

            try {
                //String path = StorageUtils.getFlashDir();
                File root = Environment.getRootDirectory();
                StatFs stat = new StatFs(root.getPath());
                //StatFs stat = new StatFs(path);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long tmp;
                tmp = totalBlocks * blockSize;
                Log.d("hjc","SystemSpace:"+formatSize(tmp)+"; tmp is :"+ tmp);
                //long availableBlocks = stat.getAvailableBlocks();
                //NANDTotalSpace =formatSize(totalBlocks * blockSize);
                //mNANDAvailableSpace =  formatSize(availableBlocks * blockSize);
        /*data*/
                root = Environment.getDataDirectory();
                stat = new StatFs(root.getPath());
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                tmp += totalBlocks * blockSize;
                Log.d("hjc","DataSpace:"+formatSize(tmp)+"; tmp is :"+ tmp);
                //mNANDTotalSpace += formatSize(totalBlocks * blockSize);
                long availableBlocks = stat.getAvailableBlocks();
                mNANDAvailableSpace =  formatSize(availableBlocks * blockSize);

        /*cache*/
                root = Environment.getDownloadCacheDirectory();
                stat = new StatFs(root.getPath());
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                tmp += totalBlocks * blockSize;
                Log.d("hjc","DataSpace:"+formatSize(tmp)+"; tmp is :"+ tmp);
                mNANDTotalSpace = formatSize(tmp);


                mNANDTotalSpacetotal=mNANDTotalSpace;
                //add by huangjc
                if(mNANDTotalSpace!=null){
                    Log.d("hjc","mNANDTotalSpace:"+mNANDTotalSpace);
                    Log.d("hjc","mNANDTotalSpace.substring:"+mNANDTotalSpace.substring(0,4));
                    float result=Float.parseFloat(mNANDTotalSpace.substring(0,3));
                    Log.d("hjc","result:"+result);
                    if(result<4&&result>2)
                        mNANDTotalSpace = "4 GB";
                    else if(result<8&&result>4)
                        mNANDTotalSpace = "8 GB";
                    else if(result<16&&result>8)
                        mNANDTotalSpace = "16 GB";
                    else if(result<32&&result>16)
                        mNANDTotalSpace = "32 GB";
                    else if(result<0.5)
                        mNANDTotalSpace = "2 GB";
                }
                mNANDAvailableSpace= mNANDAvailableSpace + "";
            } catch (IllegalArgumentException e) {

            }


        Log.i("zzz", "mNANDTotalSpace=="+mNANDTotalSpace+"mNANDAvailableSpace=="+mNANDAvailableSpace);

        mNandTotal.setText(mNANDTotalSpace);
        mNandAvailable.setText(mNANDAvailableSpace);
//    mNandTotaladd.setText(mNANDTotalSpacetotal);
    }

	/*private void updateStatusInternal(){
		File dataPath = Environment.getDataDirectory();
		StatFs stat = new StatFs(dataPath.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		//show the space of internal storage
		mExternalAvailableSpace=formatSize(availableBlocks * blockSize);
		mInternalAvailable.setText(mExternalAvailableSpace);
	}*/
}

