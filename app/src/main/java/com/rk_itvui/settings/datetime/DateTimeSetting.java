package com.rk_itvui.settings.datetime;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.rk_itvui.settings.FullScreenPreferenceActivity;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.zxy.idersettings.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.ContentResolver;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Message;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public class DateTimeSetting extends FullScreenPreferenceActivity implements OnItemClickListener{
	
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
  
    private static final int DATE_DIALOG_ID = 1;    
    private static final int TIME_DIALOG_ID = 3; 
    
    private static final int TryTimes = 5;
    private static final String[] date_format_sring = {"2013-12-31","31-12-2013","12-31-2013"};

	HashMap<String, Object> map_DateTimeAuto= new HashMap<String, Object>();
	HashMap<String, Object> map_DateSet = new HashMap<String, Object>();
	HashMap<String, Object> map_TimeSet = new HashMap<String, Object>();
	HashMap<String, Object> map_TimeZoneSet = new HashMap<String, Object>();
	HashMap<String, Object> map_24HourFormat = new HashMap<String, Object>();
    HashMap<String, Object> map_DateFormat = new HashMap<String, Object>();
	
	RemindViewAdapter listItemAdapter = null;
	ListView list = null;

	Calendar calendar = null;

    private String date_format = null;
    private char date_format_click_time = 0;

	
	private int current_date_year;	
	private int current_date_month;
	private int current_date_day;
	
	private int current_time_hour;
	private int current_time_minute;
	
	private int[] return_time ;
	
	private String current_timezone ;
	String strTimeFormat;
	
	private boolean is24HourFormat;	
	private boolean isAutoDateTime ;
	private boolean isListNotReady = true;
	
    private TextView tvTime = null;
    long sysTime;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_time_setting);
		addListView();
		
        tvTime = (TextView) findViewById(R.id.mytime);
        new TimeThread().start(); 
        new UpdateThread().start();
        new BindListThread().start();
        ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}

	public void addListView() {
				
		ContentResolver cv = this.getContentResolver();
		calendar = Calendar.getInstance();
		
        isAutoDateTime = getAutoState();
        date_format = getDateFormat();
        
        if(isAutoDateTime){       	
        	if(!GetTimeFromNetTryTimes(TryTimes)){     		
    	    	current_date_year = calendar.get(Calendar.YEAR);
            	current_date_month = calendar.get(Calendar.MONTH);
            	current_date_day = calendar.get(Calendar.DAY_OF_MONTH);
            	current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
            	current_time_minute = calendar.get(Calendar.MINUTE); 

        	}
        }else{
        	current_date_year = calendar.get(Calendar.YEAR);
        	current_date_month = calendar.get(Calendar.MONTH);
        	current_date_day = calendar.get(Calendar.DAY_OF_MONTH);
        	current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
        	current_time_minute = calendar.get(Calendar.MINUTE);  
        }        
        return_time = new int[5];
        return_time[0] = current_date_year;
        return_time[1] = current_date_month;
        return_time[2] = current_date_day;
        return_time[3] = current_time_hour;
        return_time[4] = current_time_minute;
        
		current_timezone = getTimeZoneText();
		is24HourFormat = calendar.isLenient();

		map_DateTimeAuto.put("DateTimeItem", getString(R.string.date_time_auto));
		map_DateTimeAuto.put("DateTimeStatus",getString(isAutoDateTime? R.string.auto_time :R.string.not_auto_time));

		map_DateSet.put("DateTimeItem", getString(R.string.date_time_set_date));

		
		map_TimeSet.put("DateTimeItem", getString(R.string.date_time_set_time));

 
		map_TimeZoneSet.put("DateTimeItem", getString(R.string.date_time_set_timezone));
		map_TimeZoneSet.put("DateTimeStatus",current_timezone);
		 
		map_24HourFormat.put("DateTimeItem", getString(R.string.date_time_24hour));	
		
		try{
			strTimeFormat = android.provider.Settings.System.getString(cv,
                                           android.provider.Settings.System.TIME_12_24);
			if(strTimeFormat.equals("24")){
		        Log.i("activity","24");
		        is24HourFormat = true;      
		        UpdateTimeDispaly(true);
		    }else if(strTimeFormat.equals("12")){
		    	is24HourFormat = false;	  
		    	UpdateTimeDispaly(false);
		    }	
		}catch(Exception e){
			Settings.System.putString(DateTimeSetting.this.getContentResolver(),
					Settings.System.TIME_12_24,"24");

			map_24HourFormat.put("DateTimeStatus",getString(R.string.hour24));
			
		}

        if(date_format != null){
            if(date_format.equals("yyyy-MM-dd")){
                date_format_click_time = 0;
                map_DateSet.put("DateTimeStatus",Integer.toString(current_date_year)+"-"
                        +Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_day));
            }else if(date_format.equals("dd-MM-yyyy")){
                date_format_click_time = 1;
                map_DateSet.put("DateTimeStatus",Integer.toString(current_date_day)+"-"
                        +Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_year));
            }else if(date_format.equals("MM-dd-yyyy")){
                date_format_click_time = 2;
                map_DateSet.put("DateTimeStatus",Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_day)+"-"
                        +Integer.toString(current_date_year));
            }
        }else{
            date_format= "yyyy-MM-dd";
            date_format_click_time = 0;
            map_DateSet.put("DateTimeStatus",Integer.toString(current_date_year)+"-"
                    +Integer.toString((current_date_month+1))+"-"
                    +Integer.toString(current_date_day));
        }

        map_DateFormat.put("DateTimeItem",getString(R.string.choose_date_format));
        map_DateFormat.put("DateTimeStatus",date_format_sring[date_format_click_time]);

		listItem.add(map_TimeZoneSet);
		listItem.add(map_24HourFormat);
        listItem.add(map_DateFormat);
		listItem.add(map_DateTimeAuto);	
		listItem.add(map_DateSet);
		listItem.add(map_TimeSet);
		list = (ListView) findViewById(R.id.datetime_list);

	    listItemAdapter = new RemindViewAdapter(this,
											listItem,				
											R.layout.date_time_item,			
											new String[] {"DateTimeItem" ,"DateTimeStatus"},
											new int[] {R.id.DateTimeItem , R.id.DateTimeStatus});

		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(this);

	}	
	@Override
	public void onResume() {
			
		Log.d("DateTimeSetting", "=========Activity:onResume");
		
		
    	current_date_year = calendar.get(Calendar.YEAR);
    	current_date_month = calendar.get(Calendar.MONTH);
    	current_date_day = calendar.get(Calendar.DAY_OF_MONTH);

    	current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
    	current_time_minute = calendar.get(Calendar.MINUTE);  
		
		current_timezone = getTimeZoneText();
				
		Log.d("DateTimeSetting", current_timezone);
		map_TimeZoneSet.put("DateTimeStatus",current_timezone);
		
		UpdateTimeDispaly(is24HourFormat);
	
		listItemAdapter.notifyDataSetChanged();
		
        Message msg = new Message();
        msg.what = 4;
        mHandler.sendMessage(msg);
		super.onResume();
	}
    @Override
    protected void onDestroy() {

        Log.d("DateTimeSetting", "=============================Activity:onDestroy");
        
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        Log.d("DateTimeSetting", "=============================Activity:onPause");

        super.onPause();
    }
    
	public void startIntentWithTranlete(Activity act,Intent intent){
		ActivityAnimationTool.startActivity(act, intent);
	}
	public void startActivityForResult(Activity currActivity, Intent intent,int result){
		ActivityAnimationTool.startActivityForResult(currActivity, intent,result);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		switch (index) {		
			case 0:	
				Intent intent = new Intent(DateTimeSetting.this, TimeZoneAlterDialogActivity.class);
				startActivityForResult(DateTimeSetting.this,intent,0);
				//((Activity)DateTimeSetting.this).startActivityForResult(intent, 0);				
				break;
			case 1:
				
				if(is24HourFormat){					
					is24HourFormat = false;				
					UpdateTimeDispaly(false);		
				}else{
					UpdateTimeDispaly(true);
					is24HourFormat = true;		
				}
				listItemAdapter.notifyDataSetChanged();
				break;
            case 2:
                if( (++date_format_click_time) > 2){
                    date_format_click_time = 0;
                }
                switch(date_format_click_time){
                    case 0:
                        date_format = "yyyy-MM-dd";
                        break;
                    case 1:
                        date_format = "dd-MM-yyyy";
                        break;
                    case 2:
                        date_format = "MM-dd-yyyy";
                        break;
                    default:
                        break;
                }
                setDateFormat(date_format);
                map_DateFormat.put("DateTimeStatus",date_format_sring[date_format_click_time]);
                UpdateTimeDispaly(is24HourFormat);
                listItemAdapter.notifyDataSetChanged();

                break;
			case 3:
						
				View view1 = list.getChildAt(4);
				View view2 = list.getChildAt(5);
				//TextView text1 = (TextView)findViewById(R.id.datetime_list).get;
					
				if(isAutoDateTime)
				{
					isAutoDateTime = false;
					map_DateTimeAuto.put("DateTimeStatus",getString(R.string.not_auto_time));
	
					//view1.setVisibility(View.VISIBLE);
					//view2.setVisibility(View.VISIBLE);

                    Message msg = new Message();
                    msg.what = 5;
                    mHandler.sendMessage(msg);
					
					Log.d("smj","====================="+Integer.toString(current_date_year)+"-"
							+Integer.toString((current_date_month+1))+"-"
							+Integer.toString(current_date_day)+Integer.toString(current_time_hour)+Integer.toString(current_time_minute));

				}else{														
					isAutoDateTime = true;
					map_DateTimeAuto.put("DateTimeStatus",getString(R.string.auto_time));
	
					//view1.setVisibility(View.INVISIBLE);
					//view2.setVisibility(View.INVISIBLE);
					
					if(!GetTimeFromNetTryTimes(5)){
		    	    	current_date_year = calendar.get(Calendar.YEAR);
		            	current_date_month = calendar.get(Calendar.MONTH);
		            	current_date_day = calendar.get(Calendar.DAY_OF_MONTH);
		            	current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
		            	current_time_minute = calendar.get(Calendar.MINUTE); 
					}	   			
					UpdateTimeDispaly(is24HourFormat);

					Log.d("smj","====================="+Integer.toString(current_date_year)+"-"
							+Integer.toString((current_date_month+1))+"-"
							+Integer.toString(current_date_day)+Integer.toString(current_time_hour)+Integer.toString(current_time_minute));
				}
				listItemAdapter.notifyDataSetChanged();
                Settings.Global.putInt(DateTimeSetting.this.getContentResolver(),
                        Settings.Global.AUTO_TIME, isAutoDateTime ? 1 : 0);
	
				break;
			case 4:
				if(!isAutoDateTime){
					final Calendar cal = Calendar.getInstance();
					DatePickerDialog datePicker = new DatePickerDialog(
							DateTimeSetting.this,
							mDateSetListener,
							cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH));	
					datePicker.show();
					datePicker.updateDate(
		                    cal.get(Calendar.YEAR),
		                    cal.get(Calendar.MONTH),
		                    cal.get(Calendar.DAY_OF_MONTH));
				}
	
				break;
			case 5:
				if(!isAutoDateTime){
					final Calendar cal1 = Calendar.getInstance();
					TimePickerDialog timePicker = new TimePickerDialog(
														DateTimeSetting.this,
														mTimeSetListener,
														cal1.get(Calendar.HOUR_OF_DAY),
														cal1.get(Calendar.MINUTE),
														DateFormat.is24HourFormat(DateTimeSetting.this));
		        	timePicker.setTitle(DateTimeSetting.this.getResources().getString(R.string.date_time_changeTime_text));
		        	timePicker.show();
		        	timePicker.updateTime(
		                    		cal1.get(Calendar.HOUR_OF_DAY),
		                    		cal1.get(Calendar.MINUTE));			
				}                  
				break;

		    default:
                break;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode,Intent data) 
	{
		UpdateTimeDispaly(is24HourFormat);		
		listItemAdapter.notifyDataSetChanged();
	}
	
	private CharSequence getPeroidText(){
		
		Settings.System.putString(DateTimeSetting.this.getContentResolver(),
				Settings.System.TIME_12_24,"24");
		Calendar cal= Calendar.getInstance();
		String am_pm = cal.get(Calendar.HOUR_OF_DAY) >= 12 ? "PM" : "AM";

		Settings.System.putString(DateTimeSetting.this.getContentResolver(),
				Settings.System.TIME_12_24,is24HourFormat ? "24" : "12");
		return am_pm;
	};
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {    
	    
	       public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {   
	    	   
	  		 final Calendar cal = Calendar.getInstance();
		     cal.set(Calendar.YEAR, year);
		     cal.set(Calendar.MONTH, monthOfYear);
		     cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        
	         AlarmManager am = (AlarmManager) DateTimeSetting.this.getSystemService(Context.ALARM_SERVICE);
		     am.setTime(cal.getTimeInMillis());

	   	     current_date_year = cal.get(Calendar.YEAR);
	   		 current_date_month = cal.get(Calendar.MONTH);
	   		 current_date_day = cal.get(Calendar.DAY_OF_MONTH);

             return_time[0] = current_date_year;
             return_time[1] = current_date_month;
             return_time[2] = current_date_day;
	   		    
	   		 map_DateSet.put("DateTimeStatus",Integer.toString(current_date_year)+"-"
						+Integer.toString((current_date_month+1))+"-"
						+Integer.toString(current_date_day));
	   		    
	   		 listItemAdapter.notifyDataSetChanged();
	         calendar.set(current_date_year, current_date_month, current_date_day);

	       }    
	 };   
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {  	          
	        @Override  
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
	        	
	        	final Calendar cal = Calendar.getInstance();
	        	
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);
				
				AlarmManager am = (AlarmManager) DateTimeSetting.this.getSystemService(Context.ALARM_SERVICE);
				am.setTime(cal.getTimeInMillis());

	    		current_time_hour = cal.get(Calendar.HOUR_OF_DAY);
	    		current_time_minute = cal.get(Calendar.MINUTE);

                return_time[3] = current_time_hour;
                return_time[4] = current_time_minute;
	    		    		
	    		UpdateTimeDispaly(is24HourFormat);
	    		listItemAdapter.notifyDataSetChanged();
	    		
	            calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, current_time_hour, current_time_minute, 0);

	        }  
	};  
	
	private String getTimeZoneText() 
	{
		TimeZone tz = Calendar.getInstance().getTimeZone();
		boolean daylight = tz.inDaylightTime(new Date());
		StringBuilder sb = new StringBuilder();

		sb.append(formatOffset(tz.getRawOffset() +(daylight ? tz.getDSTSavings() : 0))).
		append(", ").append(tz.getDisplayName(daylight, TimeZone.LONG));
		return sb.toString();        
	}

	private char[] formatOffset(int off) 
	{
		off = off / 1000 / 60;

		char[] buf = new char[9];
		buf[0] = 'G';
		buf[1] = 'M';
		buf[2] = 'T';

		if (off < 0) {
		    buf[3] = '-';
		    off = -off;
		} else {
		    buf[3] = '+';
		}
		int hours = off / 60; 
		int minutes = off % 60;

		buf[4] = (char) ('0' + hours / 10);
		buf[5] = (char) ('0' + hours % 10);

		buf[6] = ':';

		buf[7] = (char) ('0' + minutes / 10);
		buf[8] = (char) ('0' + minutes % 10);

		return buf;
	}
  
    @Override    
    protected void onPrepareDialog(int id, Dialog dialog) {    
       switch (id) {    
           case DATE_DIALOG_ID:
               ((DatePickerDialog) dialog).updateDate(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
               calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
               break;
           case TIME_DIALOG_ID:
               ((TimePickerDialog) dialog).updateTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE);
               calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, 0);
               break;
       }  
    }

    private String getDateFormat()
    {
        return Settings.System.getString(DateTimeSetting.this.getContentResolver(),Settings.System.DATE_FORMAT);
    }

    private void setDateFormat(String format)
    {
        if (format.length() == 0)
        {
            format = null;
        }

        Settings.System.putString(DateTimeSetting.this.getContentResolver(), Settings.System.DATE_FORMAT, format);
    }

    private void UpdateTimeDispaly(boolean is24Hour){
		Log.i("SETTINGS2", "updatetimedisplay....");
        switch(date_format_click_time){
            case 0:
                map_DateSet.put("DateTimeStatus",Integer.toString(current_date_year)+"-"
                        +Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_day));
                break;
            case 1:
                map_DateSet.put("DateTimeStatus",Integer.toString(current_date_day)+"-"
                        +Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_year));
                break;
            case 2:
                map_DateSet.put("DateTimeStatus",Integer.toString((current_date_month+1))+"-"
                        +Integer.toString(current_date_day)+"-"
                        +Integer.toString(current_date_year));
                break;
            default:
                break;
        }
		if(is24Hour){	
			
			set24Hour(true);
			
			map_24HourFormat.put("DateTimeStatus",getString(R.string.hour24));
	    	
	    	if(current_time_minute < 10){
				map_TimeSet.put("DateTimeStatus",Integer.toString(current_time_hour)+":"
									+"0"+Integer.toString(current_time_minute));
	    		
	    	}else{
				map_TimeSet.put("DateTimeStatus",Integer.toString(current_time_hour)
									+":"+Integer.toString(current_time_minute));
	    	}
			
		}else{
			
			set24Hour(false);
			
			map_24HourFormat.put("DateTimeStatus",getString(R.string.hour12));
	        if(current_time_minute < 10)
	        {
				map_TimeSet.put("DateTimeStatus",getPeroidText()+"  "        
						+Integer.toString(current_time_hour > 12 ? current_time_hour-12 : current_time_hour)+":"
						+"0"+Integer.toString(current_time_minute));
	        }else{
				map_TimeSet.put("DateTimeStatus",getPeroidText()+"  "
						+Integer.toString(current_time_hour > 12 ? current_time_hour-12 : current_time_hour)+":"
						+Integer.toString(current_time_minute));		        	
	        }	    		
		}
    }
	
	private void set24Hour(boolean is24Hour) {
        Settings.System.putString(getContentResolver(),
                Settings.System.TIME_12_24,
                is24Hour? "24" : "12");
    }
    
    private boolean getAutoState() 
	{
        try{
			if(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.JELLY_BEAN)
            	return Settings.Global.getInt(DateTimeSetting.this.getContentResolver(),Settings.Global.AUTO_TIME) > 0; 
			else
				return Settings.System.getInt(DateTimeSetting.this.getContentResolver(),Settings.System.AUTO_TIME) > 0;
        }catch (SettingNotFoundException snfe){

            return true;
        }
    }    
    
    protected boolean TryGetTimeFromNET()
    {
    		try{
	    	    URL url=new URL("http://www.baidu.com");
	    	    URLConnection uc=url.openConnection();
	    	    uc.connect(); 
	    	    
	    	    long ld=uc.getDate(); 
	    	    Date date=new Date(ld);    
	    	        	    
	    	    current_date_year = date.getYear();
	    	    current_date_month = date.getMonth();
	    	    current_date_day = date.getDay();
	    	    current_time_hour = date.getHours();
	    	    current_time_minute = date.getMinutes();

		  		calendar.set(current_date_year, 
		  					current_date_month, 
		  					current_date_day, 
		  					current_time_hour, 
		  					current_time_minute);
					
				AlarmManager am = (AlarmManager) DateTimeSetting.this.getSystemService(Context.ALARM_SERVICE);
				am.setTime(calendar.getTimeInMillis());
				return true;
	        	
		    }catch(Exception e){
		  		return false;    	
		    }  
    	}
    private boolean GetTimeFromNetTryTimes(int trytimes)
    {
    	for(int i = 0; i< trytimes; i++){
    		if(TryGetTimeFromNET()){
    			return true;
    		}   		
    	}
    	return false;	   	
    }
    
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
            
        }
    }
    class UpdateThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000*60);
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
            
        }
    }
    
    class BindListThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
            
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 1:

                final Calendar cal = Calendar.getInstance();

                switch(date_format_click_time)
                {
                    case 0:
                        if(is24HourFormat){
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTime);
                            tvTime.setText(sysTimeStr);
                        }else{
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd hh:mm:ss aaa", sysTime);
                            tvTime.setText(sysTimeStr);
                        }
                        break;
                    case 1:
                        if(is24HourFormat){
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("dd-MM-yyyy HH:mm:ss", sysTime);
                            tvTime.setText(sysTimeStr);
                        }else{
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("dd-MM-yyyy hh:mm:ss aaa", sysTime);
                            tvTime.setText(sysTimeStr);
                        }
                        break;
                    case 2:
                        if(is24HourFormat){
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("MM-dd-yyyy HH:mm:ss", sysTime);
                            tvTime.setText(sysTimeStr);
                        }else{
                            sysTime = java.lang.System.currentTimeMillis();
                            CharSequence sysTimeStr = DateFormat.format("MM-dd-yyyy hh:mm:ss aaa", sysTime);
                            tvTime.setText(sysTimeStr);
                        }
                        break;
                    default:
                        break;
                }
                current_date_year = cal.get(Calendar.YEAR);
                current_date_month = cal.get(Calendar.MONTH);
                current_date_day = cal.get(Calendar.DAY_OF_MONTH);
                current_time_hour = cal.get(Calendar.HOUR_OF_DAY);
                current_time_minute = cal.get(Calendar.MINUTE);

                //UpdateTimeDispaly(is24HourFormat);
                listItemAdapter.notifyDataSetChanged();

                break;
            case 2:
            	current_date_year = calendar.get(Calendar.YEAR);
                current_date_month = calendar.get(Calendar.MONTH);
                current_date_day = calendar.get(Calendar.DAY_OF_MONTH);
                current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
                current_time_minute = calendar.get(Calendar.MINUTE);
            	UpdateTimeDispaly(is24HourFormat);
	    		listItemAdapter.notifyDataSetChanged();
            	break;
            case 3:
            	if(isListNotReady)
            	{
            		isListNotReady = false;
	    			View view1 = list.getChildAt(4);
                    View view2 = list.getChildAt(5);
	        		//UpdateTimeDispaly(is24HourFormat);       		
	        		listItemAdapter.notifyDataSetChanged();
	    			if(isAutoDateTime){
	    				//view1.setVisibility(view1.INVISIBLE);
	    				//view2.setVisibility(view1.INVISIBLE);
	    			}
                    switch(date_format_click_time)
                    {
                        case 0:
                            if(is24HourFormat){
                                sysTime = java.lang.System.currentTimeMillis();
                                                CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTime);
                                tvTime.setText(sysTimeStr);
                            }else{
                                sysTime = java.lang.System.currentTimeMillis();
                                                CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd hh:mm:ss aaa", sysTime);
                                tvTime.setText(sysTimeStr);
                            }
                            break;
                        case 1:
                            if(is24HourFormat){
                                sysTime = java.lang.System.currentTimeMillis();
                                CharSequence sysTimeStr = DateFormat.format("dd-MM-yyyy HH:mm:ss", sysTime);
                                tvTime.setText(sysTimeStr);
                            }else{
                                sysTime = java.lang.System.currentTimeMillis();
                                CharSequence sysTimeStr = DateFormat.format("dd-MM-yyyy hh:mm:ss aaa", sysTime);
                                tvTime.setText(sysTimeStr);
                            }
                            break;
                        case 2:
                            if(is24HourFormat){
                                sysTime = java.lang.System.currentTimeMillis();
                                CharSequence sysTimeStr = DateFormat.format("MM-dd-yyyy HH:mm:ss", sysTime);
                                tvTime.setText(sysTimeStr);
                            }else{
                                sysTime = java.lang.System.currentTimeMillis();
                                CharSequence sysTimeStr = DateFormat.format("MM-dd-yyyy hh:mm:ss aaa", sysTime);
                                tvTime.setText(sysTimeStr);
                            }
                            break;
                        default:
                            break;
                    }
            	}
            	break;
            case 4:
            	if(isAutoDateTime){       	
                	if(!GetTimeFromNetTryTimes(TryTimes)){     		
            	    	current_date_year = calendar.get(Calendar.YEAR);
                    	current_date_month = calendar.get(Calendar.MONTH);
                    	current_date_day = calendar.get(Calendar.DAY_OF_MONTH);
                    	current_time_hour = calendar.get(Calendar.HOUR_OF_DAY);
                    	current_time_minute = calendar.get(Calendar.MINUTE); 
                	}
                }
        		UpdateTimeDispaly(is24HourFormat);       		
        		listItemAdapter.notifyDataSetChanged();
        		break;
            case 5:
                current_date_year = return_time[0];
                current_date_month = return_time[1];
                current_date_day = return_time[2];
                current_time_hour = return_time[3];
                current_time_minute = return_time[4];
                UpdateTimeDispaly(is24HourFormat);
                listItemAdapter.notifyDataSetChanged();
                calendar.set(current_date_year,
                        current_date_month,
                        current_date_day,
                        current_time_hour,
                        current_time_minute);

                AlarmManager am = (AlarmManager) DateTimeSetting.this.getSystemService(Context.ALARM_SERVICE);
                am.setTime(calendar.getTimeInMillis());


            default :
            	break;
            }
        }
    };
    	
}
