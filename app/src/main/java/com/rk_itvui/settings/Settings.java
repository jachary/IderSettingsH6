/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     huangjc@rock-chips.com
* Create at:   2014骞�7鏈�9鏃� 涓嬪崍4:42:08  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014骞�7鏈�9鏃�      huangjc         1.0         create
*******************************************************************/
package com.rk_itvui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.Intent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.rk_itvui.settings.tranlete.FoldingEffect;
import com.rk_itvui.settings.upgrade.UpgradeActivity;
import com.rk_itvui.settings.developer.DevelopmentSettings;
import com.rk_itvui.settings.deviceversion.Deviceversion;
import com.rk_itvui.settings.dialog.ApplicationSetting;
import com.rk_itvui.settings.datetime.DateTimeSetting;
import com.rk_itvui.settings.factoryreset.Factoryreset;
import com.rk_itvui.settings.language.LanguageInputmethod;
import com.rk_itvui.settings.sound.SoundSetting;
import com.rk_itvui.settings.storeinfo.Storage;
import com.rk_itvui.utils.FocusScaleUtils;
import com.rk_itvui.utils.FocusUtils;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridView;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.content.ComponentName;
import android.widget.AdapterView;
import android.view.View;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.media.AudioManager;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import android.widget.Button;

public class Settings extends Activity {
	private static final String TAG = "Settings2";
	
	private ViewPager mViewPager;
	private MyGridViewAdpter mGridViewAdapter;
	private ContentResolver mContentResolver;
	private Handler mViewPageHandler;
	//the count of each page 
	private final int COUNT_PER_PAGE = 11;

	//the count of settings2 items
	private final int COUNT_PER_iTEMS = 11;
	
	private PageIndicator mPageIndicator;
	private long mCurrentFocusPosition;

	private AlwaysMarqueeTextView mTextView = null;
	private Button mLeftArrow = null;
	private Button mRightArrow = null;
	// private ImageView whiteBorder;
	private int focusposition = -1;//
	private ScaleAnimEffect animEffect;

	private final static int UPDATA_UI = 0;

	private static ArrayList<SettingItems> mItemInformation = null;
    //name of settings items
	private final static int NETWORK_SETTINGS = 0;
	private final static int TIME_AND_DATE = 1;
	private final static int SOUND_SETTINGS = 2;
	private final static int DISPLAYS_SETTINGS = 3;
	private final static int STORE_SETTINGS = 4;
	private final static int LANGRAGE_SETTINGS = 5;
//	private final static int DUOPING_SETTINGS = 6;
	private final static int DEVICES_MESSAGE = 6;
	private final static int FACTORY_RESET = 7;
//	private final static int SYSTEM_UPDATE = 9;
	private final static int APP_MANERGE = 8;
	private final static int DEVELOPER_SETTINGS = 9;
	private final static int MORE_SETTING = 10;
	/*application manager*/
	ApplicationSetting mApplicationSetting = null;
	//focus img
	private FocusScaleUtils focusScaleUtils;
    private FocusUtils focusUtils;
    public boolean isNeedInitMoveHideFocus = true;
    private View view;
	private ViewGroup mViewGroup = null;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "==================================Activity:onCreate");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		ActivityAnimationTool.init(new FoldingEffect());
		view = LayoutInflater.from(this).inflate(R.layout.settings_activity,null);
		setContentView(view);
		focusScaleUtils = new FocusScaleUtils();
        focusUtils = new FocusUtils(this, view, R.drawable.focusiv, R.drawable.function_back_focus, this.isNeedInitMoveHideFocus);
		mViewGroup = (ViewGroup) findViewById(R.id.app_layout);

		mLeftArrow = (Button) findViewById(R.id.arrow_left);
		mRightArrow = (Button) findViewById(R.id.arrow_right);
		mLeftArrow.setOnClickListener(mOnClickListener);
		mRightArrow.setOnClickListener(mOnClickListener);

		animEffect = new ScaleAnimEffect();

		mItemInformation = new ArrayList<SettingItems>();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		ScreenInfo.DENSITY = displayMetrics.densityDpi;
		ScreenInfo.WIDTH = displayMetrics.widthPixels;
		ScreenInfo.HEIGHT = displayMetrics.heightPixels;

		ScreenInformation.mScreenWidth = displayMetrics.widthPixels;
		ScreenInformation.mScreenHeight = displayMetrics.heightPixels;
		ScreenInformation.mDensityDpi = displayMetrics.densityDpi;
		ScreenInformation.mDpiRatio = ((float) ScreenInformation.mDefaultDpi)
				/ (float) displayMetrics.densityDpi;

		new Thread(getInfoRunnable).start();
		mApplicationSetting = new ApplicationSetting(this, mHandler);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mGridViewAdapter != null) {
			mViewPager.setAdapter(null);
			mGridViewAdapter = null;
		}
		super.onDestroy();
	}
  // This snippet hides the system bars.
	@TargetApi(19)
	private void hideSystemUI() {
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		if (Build.VERSION.SDK_INT >= 19) {
			Log.d(TAG, "*********hideSystemUI************");
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
																	// bar
							| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
							| View.SYSTEM_UI_FLAG_IMMERSIVE);
			getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
					new View.OnSystemUiVisibilityChangeListener() {

						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							// TODO Auto-generated method stub
							Log.d(TAG,
									"**********onSystemUiVisibilityChange**********");
							int fullscreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navbar
									| View.SYSTEM_UI_FLAG_FULLSCREEN // hide statusbar
									| View.SYSTEM_UI_FLAG_IMMERSIVE;
							if (visibility != fullscreenFlags)
								;
							Handler mH = new Handler();
							mH.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									hideSystemUI();
								}
							}, 1000);

						}
					});
		}
	}
	@Override
	public void onResume() {
		Log.d(TAG, "==================================Activity:onResume");
		if (Build.VERSION.SDK_INT >= 19)
			hideSystemUI();
		super.onResume();
	}

	private void createGridView() {
		mViewPager = (ViewPager) findViewById(R.id.app_viewpager);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mGridViewAdapter = new MyGridViewAdpter(getApplicationContext(), this,
				mItemInformation);
		mGridViewAdapter.setCountPerPage(COUNT_PER_PAGE);
		mGridViewAdapter.setOnItemClickListener(mOnItemClickListener);
		mViewPager.setAdapter(mGridViewAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mContentResolver = getContentResolver();
		mViewPageHandler = new Handler();

//		mPageIndicator.setVisibility(View.VISIBLE);
		mPageIndicator.setDotCount(mGridViewAdapter.getCount());
		mPageIndicator.setActiveDot(0);
		mLeftArrow.setVisibility(View.GONE);
		mRightArrow.setBackgroundResource(R.drawable.right_icon_select);
		if (1 == mGridViewAdapter.getCount()) {
			mLeftArrow.setVisibility(View.GONE);
			mRightArrow.setVisibility(View.GONE);
		}

	}
	
	public void startIntentWithTranlete(Activity act,Intent intent){
		//ActivityAnimationTool.startActivity(act, intent);
		startActivity(intent);
	}
	// start settings items
	void startItems(int position) {
		Log.d(TAG, "startItems:" + mItemInformation.get(position).getItemName());
		switch (position) {
		case NETWORK_SETTINGS:
			Intent intent_network = new Intent(Settings.this, network_settingnew.class);
//			Intent intent_network = new Intent(Settings.this, NetSettings.class);
			startIntentWithTranlete(Settings.this,intent_network);
			break;
		case TIME_AND_DATE:
			Intent intent_datetime = new Intent(Settings.this, DateTimeSetting.class);
			startIntentWithTranlete(Settings.this,intent_datetime);
			break;
		case SOUND_SETTINGS:
			Intent intent_sound_new = new Intent(Settings.this, SoundSetting.class);
			startIntentWithTranlete(Settings.this,intent_sound_new);
			break;
		case LANGRAGE_SETTINGS:
			Intent intent_language = new Intent(Settings.this, LanguageInputmethod.class);
			startIntentWithTranlete(Settings.this,intent_language);
			break;
		case DISPLAYS_SETTINGS:
			// 鏄剧ず璁剧疆
			Intent intentdisplay = new Intent (Settings.this,ScreensSettings.class);
			startIntentWithTranlete(Settings.this,intentdisplay);
//			Intent intentdisplay = new Intent();
//			intentdisplay.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DisplaySettingsActivity"));
//	        startActivity(intentdisplay);
			break;
		case STORE_SETTINGS:
			// 瀛樺偍淇℃伅
			Intent intentstore = new Intent(Settings.this, Storage.class);
			startIntentWithTranlete(Settings.this,intentstore);
			break;
		case MORE_SETTING:
//			Intent intent_duoping = new Intent(Settings.this, PersonalSetting.class);
//			startActivity(intent_duoping);
			Intent intent_more = new Intent("android.settings.SETTINGS");
			startActivity(intent_more);
			break;
		case DEVICES_MESSAGE:
			Intent intentdevices = new Intent(Settings.this, Deviceversion.class);
			startIntentWithTranlete(Settings.this,intentdevices);
			break;
		case FACTORY_RESET:
			// 鐠佹儳顦穱鈩冧紖
			Intent intentfactoryreset = new Intent(Settings.this, Factoryreset.class);
			startIntentWithTranlete(Settings.this,intentfactoryreset);
			break;
//		case SYSTEM_UPDATE:
//			 /*try
//             {
//                     Intent intent_update = new Intent(Intent.ACTION_MAIN);
//                     intent_update.setComponent(new ComponentName("android.rockchip.update.service",
//                                     "android.rockchip.update.service.Setting"));
//                     intent_update.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                     startActivity(intent_update);
//             }
//             catch (ActivityNotFoundException e)
//             {
//                     Toast.makeText(this, R.string.activity_not_found,Toast.LENGTH_SHORT).show();
//             }
//             catch (SecurityException e)
//             {
//                     Toast.makeText(this, R.string.activity_not_found,Toast.LENGTH_SHORT).show();
//             }*/
//			Intent intent_update = new Intent(Settings.this, UpgradeActivity.class);
//			startIntentWithTranlete(Settings.this,intent_update);
//			break;
		case APP_MANERGE:
			// 搴旂敤
////			mApplicationSetting.SettingApplication();
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//        	intent.setClass(Settings.this, com.rk_itvui.settings.dialog.ManageApplications.class);
//			startIntentWithTranlete(Settings.this,intent);
			break;
		case DEVELOPER_SETTINGS:
			// 寮�鍙戣�呴�夐」
//			Intent intent_develop = new Intent(Settings.this, DevelopmentSettings.class);
//			startIntentWithTranlete(Settings.this,intent_develop);
			Intent intent1 =new Intent(Settings.this,UpgradeActivity.class);
			startActivity(intent1);
			break;

		default:
			break;
		}
	}

	public void getSettingsItems() {

		mItemInformation.clear();
		Resources res = getResources();
		String[] items = res.getStringArray(R.array.settings_items);
		for (int i = 0; i < COUNT_PER_iTEMS; i++) {
			SettingItems Infor = new SettingItems();
			Infor.setItemName(items[i]);
			mItemInformation.add(Infor);
		}
	}

	private Runnable getInfoRunnable = new Runnable() {
		public void run() {
			getSettingsItems();

			Message msg = new Message();
			msg.what = UPDATA_UI;
			msg.arg1 = mItemInformation.size();
			mHandler.sendMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATA_UI: {
				Log.d("Settings", "UPDATA_UI");
				createGridView();
			}
				break;
			}
		}
	};

	private SettingsGridViewAdpter.OnItemClickListener mOnItemClickListener = new SettingsGridViewAdpter.OnItemClickListener() {

		public void onItemClick(ViewPager parent, View view, int position) {
			startItems(position);
		}
	};

	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int pageindex) {
			// TODO Auto-generated method stub
			mPageIndicator.setActiveDot(pageindex);
			//focusUtils.startMoveFocus(mViewPager.getChildAt(0).findViewById(R.id.view_item0), true, 1.2F);
			if (0 == pageindex) {
				mLeftArrow.setVisibility(View.GONE);
				mRightArrow.setVisibility(View.VISIBLE);
				mRightArrow.setBackgroundResource(R.drawable.right_icon_select);
			} else if ((mGridViewAdapter.getCount() - 1) == pageindex) {
				mLeftArrow.setBackgroundResource(R.drawable.left_icon_select);
				mRightArrow.setVisibility(View.GONE);
				mLeftArrow.setVisibility(View.VISIBLE);
			} else {
				mLeftArrow.setVisibility(View.VISIBLE);
				mRightArrow.setVisibility(View.VISIBLE);
				mLeftArrow.setBackgroundResource(R.drawable.left_icon_select);
				mRightArrow.setBackgroundResource(R.drawable.right_icon_select);
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
	};

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			switch (id) {
			case R.id.arrow_left:
				mViewPager.arrowScroll(View.FOCUS_LEFT);
				break;

			case R.id.arrow_right:
				mViewPager.arrowScroll(View.FOCUS_RIGHT);
				break;

			default:
				break;
			}
		}
	};

	private class MyGridViewAdpter extends SettingsGridViewAdpter {
		private final String TAG = "MyCursorPagerAdapter";
		private LayoutInflater mInflater;
		private Settings mSettings;

		private int mImageIdIdx;
		private int mImageNameIdx;
		private int randomTemp = -1;

		class ViewHolder {
			LinearLayout[] mLayoutItems = new LinearLayout[COUNT_PER_PAGE];
			LinearLayout[] mBgItems = new LinearLayout[COUNT_PER_PAGE];
			ImageView[] mImageItems = new ImageView[COUNT_PER_PAGE];
			AlwaysMarqueeTextView[] mTextItems = new AlwaysMarqueeTextView[COUNT_PER_PAGE];

		}

		private ViewHolder mView;

		Integer[] mBackground = { R.drawable.zxy_net, R.drawable.zxy_date,
				R.drawable.zxy_sound, R.drawable.zxy_diplay,
				R.drawable.zxy_flash, R.drawable.zxy_ime,
				 R.drawable.zxy_info, R.drawable.zxy_factory,
				 R.drawable.zxy_appmanager,R.drawable.zxy_developer,R.drawable.zxy_more};

		class ViewItemHolder {
			int position;
		}

		public void setActivity(Settings newactivity) {
			mSettings = newactivity;
		}

		public MyGridViewAdpter(Context context, Settings activity,
				ArrayList<SettingItems> list) {
			super(context, activity, list);
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater) LayoutInflater.from(mContext);
			mSettings = activity;
		}

		@Override
		public View newView(Context context, ViewGroup parent, int pageindex) {
			// TODO Auto-generated method stub
			ViewHolder vh = new ViewHolder();
			View view = mInflater.inflate(R.layout.item_view, null);
			vh.mLayoutItems[0] = (LinearLayout) view
					.findViewById(R.id.view_item0);
			vh.mLayoutItems[1] = (LinearLayout) view
					.findViewById(R.id.view_item1);
			vh.mLayoutItems[2] = (LinearLayout) view
					.findViewById(R.id.view_item2);
			vh.mLayoutItems[3] = (LinearLayout) view
					.findViewById(R.id.view_item3);
			vh.mLayoutItems[4] = (LinearLayout) view
					.findViewById(R.id.view_item4);
			vh.mLayoutItems[5] = (LinearLayout) view
					.findViewById(R.id.view_item5);
			vh.mLayoutItems[6] = (LinearLayout) view
					.findViewById(R.id.view_item6);
			vh.mLayoutItems[7] = (LinearLayout) view
					.findViewById(R.id.view_item7);
			vh.mLayoutItems[8] = (LinearLayout) view
					.findViewById(R.id.view_item8);
			vh.mLayoutItems[9] = (LinearLayout) view
					.findViewById(R.id.view_item9);
			vh.mLayoutItems[10] = (LinearLayout) view
					.findViewById(R.id.view_item10);
			

			vh.mBgItems[0] = (LinearLayout) view.findViewById(R.id.view_bg0);
			vh.mBgItems[1] = (LinearLayout) view.findViewById(R.id.view_bg1);
			vh.mBgItems[2] = (LinearLayout) view.findViewById(R.id.view_bg2);
			vh.mBgItems[3] = (LinearLayout) view.findViewById(R.id.view_bg3);
			vh.mBgItems[4] = (LinearLayout) view.findViewById(R.id.view_bg4);
			vh.mBgItems[5] = (LinearLayout) view.findViewById(R.id.view_bg5);
			vh.mBgItems[6] = (LinearLayout) view.findViewById(R.id.view_bg6);
			vh.mBgItems[7] = (LinearLayout) view.findViewById(R.id.view_bg7);
			vh.mBgItems[8] = (LinearLayout) view.findViewById(R.id.view_bg8);
			vh.mBgItems[9] = (LinearLayout) view.findViewById(R.id.view_bg9);
			vh.mBgItems[10] = (LinearLayout) view.findViewById(R.id.view_bg10);

			vh.mImageItems[0] = (ImageView) view
					.findViewById(R.id.item_imageview0);
			vh.mImageItems[1] = (ImageView) view
					.findViewById(R.id.item_imageview1);
			vh.mImageItems[2] = (ImageView) view
					.findViewById(R.id.item_imageview2);
			vh.mImageItems[3] = (ImageView) view
					.findViewById(R.id.item_imageview3);
			vh.mImageItems[4] = (ImageView) view
					.findViewById(R.id.item_imageview4);
			vh.mImageItems[5] = (ImageView) view
					.findViewById(R.id.item_imageview5);
			vh.mImageItems[6] = (ImageView) view
					.findViewById(R.id.item_imageview6);
			vh.mImageItems[7] = (ImageView) view
					.findViewById(R.id.item_imageview7);
			vh.mImageItems[8] = (ImageView) view
					.findViewById(R.id.item_imageview8);
			vh.mImageItems[9] = (ImageView) view
					.findViewById(R.id.item_imageview9);
			vh.mImageItems[10] = (ImageView) view
					.findViewById(R.id.item_imageview10);

			vh.mTextItems[0] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview0);
			vh.mTextItems[1] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview1);
			vh.mTextItems[2] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview2);
			vh.mTextItems[3] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview3);
			vh.mTextItems[4] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview4);
			vh.mTextItems[5] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview5);
			vh.mTextItems[6] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview6);
			vh.mTextItems[7] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview7);
			vh.mTextItems[8] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview8);
			vh.mTextItems[9] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview9);
			vh.mTextItems[10] = (AlwaysMarqueeTextView) view
					.findViewById(R.id.item_textview10);

			for (LinearLayout v : vh.mLayoutItems) {
				v.setOnClickListener(mOnClickListener);
				v.setOnLongClickListener(mOnLongClickListener);
				v.setOnFocusChangeListener(mOnFocusChangeListener);
			}
			view.setTag(vh);
			return view;
		}

		@Override
		public void bindView(View view, Context context, int pageindex) {
			// TODO Auto-generated method stub 设置item
			ViewHolder vh = (ViewHolder) view.getTag();
			mView = vh;
			int i = 0;
			final int StartPostition = pageindex * COUNT_PER_PAGE;
			int position = StartPostition + i;
			while (i < COUNT_PER_PAGE && position < getListCount()) {
				ViewItemHolder holder = new ViewItemHolder();
				holder.position = position;
				vh.mLayoutItems[i].setClickable(true);
				vh.mLayoutItems[i].setFocusable(true);
				vh.mLayoutItems[i].setTag(holder);
				if (position == mCurrentFocusPosition) {
					vh.mLayoutItems[i+1].requestFocus();
				}
//				vh.mBgItems[i].setBackgroundResource(R.drawable.zxy_itembg);
				vh.mImageItems[i].setBackgroundResource(mBackground[position]);
				// vh.mImageItems[i].setImageDrawable(mItemInformation
				// .get(position).getIcon());
				vh.mTextItems[i].setText(mItemInformation.get(position)
						.getItemName());
				vh.mTextItems[i].setTextColor(Color.WHITE);
				;
				i++;
				position = StartPostition + i;
			}
		}

		private void focusSearch(View view, long position) {
			if (view != null) {
				LinkedList<View> linkList = new LinkedList<View>();
				linkList.offer(view);
				ArrayList<View> hasSearchList = new ArrayList<View>();
				hasSearchList.add(view);
				while (!linkList.isEmpty()) {
					View v = linkList.poll();
					if (v != null) {
						View focus = v.focusSearch(View.FOCUS_LEFT);
						if (focus != null && !hasSearchList.contains(focus)) {
							ViewItemHolder holder = (ViewItemHolder) focus
									.getTag();
							if (holder != null) {
								if (holder.position == position) {
									focus.requestFocus();
									return;
								}
							}
							linkList.offer(focus);
							hasSearchList.add(focus);
						}
						focus = v.focusSearch(View.FOCUS_UP);
						if (focus != null && !hasSearchList.contains(focus)) {
							ViewItemHolder holder = (ViewItemHolder) focus
									.getTag();
							if (holder != null) {
								if (holder.position == position) {
									focus.requestFocus();
									return;
								}
							}
							linkList.offer(focus);
							hasSearchList.add(focus);
						}
						focus = v.focusSearch(View.FOCUS_RIGHT);
						if (focus != null && !hasSearchList.contains(focus)) {
							ViewItemHolder holder = (ViewItemHolder) focus
									.getTag();
							if (holder != null) {
								if (holder.position == position) {
									focus.requestFocus();
									return;
								}
							}
							linkList.offer(focus);
							hasSearchList.add(focus);
						}
						focus = v.focusSearch(View.FOCUS_DOWN);
						if (focus != null && !hasSearchList.contains(focus)) {
							ViewItemHolder holder = (ViewItemHolder) focus
									.getTag();
							if (holder != null) {
								if (holder.position == position) {
									focus.requestFocus();
									return;
								}
							}
							linkList.offer(focus);
							hasSearchList.add(focus);
						}
					}
				}
			}
		}

		private void showOnFocusAnimation(View mFocusView, final int position) {
			mFocusView.bringToFront();
			animEffect.setAttributs(1.0f, 1.20f, 1.0f, 1.20f, 100);
			mFocusView.startAnimation(animEffect.createAnimation());
		}

		private void showLooseFocusAinimation(View mFocusView,
				final int position) {
			animEffect.setAttributs(1.30f, 1.0f, 1.30f, 1.0f, 100);
			mFocusView.startAnimation(animEffect.createAnimation());
			// bgs[position].setVisibility(View.GONE);
		}

		@Override
		protected void onContentChanged() {
			// TODO Auto-generated method stub
			super.onContentChanged();
			Log.d(TAG, "onContentChanged");
			mPageIndicator.setDotCount(mGridViewAdapter.getCount());
			mPageIndicator.setActiveDot(mViewPager.getCurrentItem());
			mCurrentFocusPosition = 0;
		}

		private View.OnClickListener mOnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getTag() != null && mOnItemClickListener != null) {
					ViewItemHolder holder = (ViewItemHolder) v.getTag();
					mOnItemClickListener.onItemClick(null, v, holder.position);
				}
			}
		};

		private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		};
        // 鐒︾偣璁＄畻
		private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View mFocusView, boolean hasFocus) {
				// TODO Auto-generated method stub
				switch (mFocusView.getId()) {
				case R.id.view_item0:
					focusposition = 0;
					break;
				case R.id.view_item1:
					focusposition = 1;
					break;
				case R.id.view_item2:
					focusposition = 2;
					break;
				case R.id.view_item3:
					focusposition = 3;
					break;
				case R.id.view_item4:
					focusposition = 4;
					break;
				case R.id.view_item5:
					focusposition = 5;
					break;
				case R.id.view_item6:
					focusposition = 6;
					break;
				case R.id.view_item7:
					focusposition = 7;
					break;
				case R.id.view_item8:
					focusposition = 8;
					break;
				case R.id.view_item9:
					focusposition = 9;
					break;
				case R.id.view_item10:
					focusposition = 10;
					break;
				}
				if (hasFocus) {
					ViewItemHolder holder = (ViewItemHolder) mFocusView
							.getTag();
					if (holder != null) {
						int currentPage = (int) (mCurrentFocusPosition / COUNT_PER_PAGE);
						int nextPage = holder.position / COUNT_PER_PAGE;
						if (currentPage == nextPage) {
							mCurrentFocusPosition = holder.position;
						} 
						else {
							if (currentPage > nextPage)
								mCurrentFocusPosition = mCurrentFocusPosition - 5;
							else
								mCurrentFocusPosition = mCurrentFocusPosition + 5;

							if (mCurrentFocusPosition >= mGridViewAdapter
									.getListCount()
									|| mCurrentFocusPosition < 0)
								mCurrentFocusPosition = holder.position;

							if (mCurrentFocusPosition != holder.position)
								focusSearch(mFocusView, mCurrentFocusPosition);
						}
					}
					if (mFocusView.isFocused()) {
//						showOnFocusAnimation(mFocusView,
//								(int) (mCurrentFocusPosition % COUNT_PER_PAGE));
						mFocusView.bringToFront();	
						Log.i("zzz", "mFocusView ="+mFocusView.getWidth()+"***"+mFocusView.getHeight());
						focusUtils.startMoveFocus(mFocusView, true, 1.2F);
						focusScaleUtils.scaleToLarge(mFocusView);	
					}

				} else {
					if (focusposition == mCurrentFocusPosition % COUNT_PER_PAGE)
						//showLooseFocusAinimation(mFocusView, focusposition);
						focusScaleUtils.scaleToNormal();	
				}
			}
		};
	}

}
