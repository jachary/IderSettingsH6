package com.rk_itvui.settings.factoryreset;


import com.rk_itvui.settings.FullScreenActivity;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.Settings;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Factoryreset extends  FullScreenActivity{
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.factoryreset_setting);
			ActivityAnimationTool.prepareAnimation(this);
			ActivityAnimationTool.animate(this, 1000);
	}	
	public void onClick(View v) {
		int id = v.getId();
		switch (id){
		case R.id.resetsure: {
			sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
		}
		break;
		case R.id.resetcancel: {   
            Factoryreset.this.finish(); 			
		}
		break;		
		}
	}
}

