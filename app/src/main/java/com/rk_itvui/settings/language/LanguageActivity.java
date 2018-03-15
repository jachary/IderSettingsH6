/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��25�� ����4:24:03  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��25��      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.rk_itvui.settings.BaseActivity;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.ReflectionUtils;
import com.rk_itvui.settings.WindowHelper;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class LanguageActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	private ListView mLangListView;
	private ArrayList<Language> mLocales;
	private String[] mSpecialLocaleCodes;
	private String[] mSpecialLocaleNames;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_language);
		mLangListView = (ListView)findViewById(R.id.list_view);
		mLangListView.requestFocus();
		mLangListView.setSelection(0);
		mLangListView.setOnItemClickListener(this);

		buildLangListItem();
		LanguageArrayAdapter upgradeAdapter = new LanguageArrayAdapter(this, mLocales);
		mLangListView.setAdapter(upgradeAdapter);

		ViewGroup.LayoutParams  lp = mLangListView.getLayoutParams();
		lp.width = (int)(0.6*WindowHelper.getWinWidth(this));
		lp.height = (int)(0.8*WindowHelper.getWinHeight(this));
		mLangListView.setLayoutParams(lp);
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}
	
	
	private void buildLangListItem() {
		mSpecialLocaleCodes = getResources().getStringArray(R.array.lang_speciale_codes);
		mSpecialLocaleNames = getResources().getStringArray(R.array.lang_special_names);
		String[] locales = getAssets().getLocales();
		Arrays.sort(locales);
		final int origSize = locales.length;
		Language[] preprocess = new Language[origSize];
		int finalSize = 0;
		for (int i = 0 ; i < origSize; i++ ) {
		    String s = locales[i];
		    int len = s.length();
		    if (len == 5) {
		        String language = s.substring(0, 2);
		        String country = s.substring(3, 5);
		        Locale l = new Locale(language, country);

		        if (finalSize == 0) {
		            preprocess[finalSize++] = new Language(toTitleCase(l.getDisplayLanguage(l)), l);
		        } else {
		            if (preprocess[finalSize-1].getLocale().getLanguage().equals(language)) {
		                preprocess[finalSize-1].setLabel(toTitleCase(getDisplayName(preprocess[finalSize-1].getLocale())));
		                preprocess[finalSize++] = new Language(toTitleCase(getDisplayName(l)), l);
		            } else {
		                String displayName;
		                if (s.equals("zz_ZZ")) {
		                    displayName = "Pseudo...";
		                } else {
		                    displayName = toTitleCase(l.getDisplayLanguage(l));
		                }
		                preprocess[finalSize++] = new Language(displayName, l);
		            }
		        }
		        setIconRes(preprocess[finalSize-1]);//Set Icon
		    }
		}
		Language mLocales2[] = new Language[finalSize];
		for (int i = 0; i < finalSize ; i++) {
			mLocales2[i] = preprocess[i];
		}
		Arrays.sort(mLocales2);
		
		//Arrays.sort(preprocess);
		mLocales = new ArrayList<Language>(Arrays.asList(mLocales2));
	}

	private static String toTitleCase(String s) 
	{
		if (s.length() == 0){
			return s;
		}

		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private String getDisplayName(Locale l)
	{
		String code = l.toString();

		for (int i = 0; i < mSpecialLocaleCodes.length; i++) {
			if (mSpecialLocaleCodes[i].equals(code)) {
				return mSpecialLocaleNames[i];
			}
		}

		return l.getDisplayName(l);
	}

	private void setIconRes(Language lang){
		String str = lang.getLocale().toString();
		for(int i=0; i<mIconResourceIDs.length; i++){
			if(mIconResourceIDs[i][0].equals(str)){
				lang.setIconRes((Integer)mIconResourceIDs[i][1]);
				return;
			}
		}
		lang.setIconRes(R.drawable.lang_default);
	}
	
	static Object[][] mIconResourceIDs = {
			{"zh_CN", R.drawable.lang_zh_rcn},
			{"zh_HK", R.drawable.lang_zh_rhk},
			{"ja_JP", R.drawable.lang_ja_rjp},
			{"en_CA", R.drawable.lang_en_rca},
			{"en_NZ", R.drawable.lang_en_rnz},
			{"ko_KR", R.drawable.lang_ko_rkr},
			{"en_US", R.drawable.lang_en_rus}
	};
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		try 
		{
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();

            Language loc = mLocales.get(position);
            config.locale = loc.getLocale();

            // indicate this isn't some passing default - the user wants this remembered
            config.userSetLocale = true;

            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged("com.android.providers.settings");
        } 
		catch (RemoteException e) 
        {
        }
		finish();
	}

}