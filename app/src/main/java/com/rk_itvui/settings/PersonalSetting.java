package com.rk_itvui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ComponentName;
import android.widget.Button;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.os.SystemProperties;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.zxy.idersettings.R;

public class PersonalSetting extends FullScreenActivity {
	private String mDlnaName = "eHomeMediaCenter";
	Button dlna;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Context dlnaContext = getApplicationContext()
					.createPackageContext("com.rockchip.mediacenter",
							Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences sp = dlnaContext.getSharedPreferences("external",
					Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE
							| Context.BIND_AUTO_CREATE);
			mDlnaName = sp.getString("devicename", "eHomeMediaCenter");

			// Log.d("hjc","--------intentdlna00000:"+mDlnaName);
		} catch (Exception e) {
		}

		// Log.d("hjc","--------intentdlna1111"+mDlnaName);
		setContentView(R.layout.personal_setting);

		//createTitle();
		dlna = getButton();
	}

	private void createTitle() {
		ImageView image = (ImageView) findViewById(R.id.title_img);
		Bitmap resize = bitMapScale(R.drawable.personal,
				ScreenInformation.mDpiRatio);
		image.setScaleType(ImageView.ScaleType.CENTER);
		image.setImageBitmap(resize);

//		TextView title = (TextView) findViewById(R.id.sound_title_text);
//		title.setTextSize(ScreenInformation.mScreenWidth / 25f
//				* ScreenInformation.mDpiRatio);
	}

	private Bitmap bitMapScale(int id, float scaleParameter) {
		Bitmap map = BitmapFactory.decodeResource(this.getResources(), id);
		float scale = ScreenInformation.mScreenWidth / 1280f * scaleParameter;
		int width = (int) ((float) map.getWidth() * scale);
		int height = (int) ((float) map.getHeight() * scale);

		Bitmap resize = Bitmap.createScaledBitmap(map, width, height, true);
		return resize;
	}

	private Button getButton() {
		dlna = (Button) findViewById(R.id.dlna_name);
		dlna.setText(mDlnaName);
		Bitmap map = bitMapScale(R.drawable.dlna_icon, 1f);
		Drawable drawable = new BitmapDrawable(map);
		dlna.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
		return dlna;
	}

	public void showSetMaxDialog() {
		final EditText editText = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle(R.string.btn_setting)
				.setView(editText)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (!editText.getText().toString().trim()
										.equals("")) {
									mDlnaName = editText.getText().toString();
									getButton().setText(mDlnaName);
									Intent dlnaIntent = new Intent();
									dlnaIntent
											.setAction("com.rockchip.mediacenter.action.SystemDeviceService");
									dlnaIntent.putExtra("command", 6);
									dlnaIntent.putExtra("friendlyname",
											mDlnaName);
									// Log.d("hjc","--------intentdlna");
									startService(dlnaIntent);

								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}

						}).show();
	}

	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.dlna_name: {
			showSetMaxDialog();
		}
			break;
		}
	}
}
