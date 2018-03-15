package com.rk_itvui.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import com.android.internal.app.AlertActivity;

public class FullScreenAlertActivity extends AlertActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	// This snippet hides the system bars.
	@TargetApi(19)
	protected void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
		if (Build.VERSION.SDK_INT >= 19){
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().getDecorView().setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					// TODO Auto-generated method stub
					int fullscreenFlags =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
					if (visibility != fullscreenFlags);
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
}
