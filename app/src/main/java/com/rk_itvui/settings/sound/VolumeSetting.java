package com.rk_itvui.settings.sound;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.zxy.idersettings.R;


public class VolumeSetting extends Activity {

    /** Called when the activity is first created. */
    private MediaPlayer mediaPlayer01;
    public AudioManager audiomanage;
    private TextView mVolume ;  //鏄剧ず褰撳墠闊抽噺
    public SeekBar soundBar;
    private int maxVolume, currentVolume;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_settings);
        mediaPlayer01 = new MediaPlayer();

        //imageButton_white1=(ImageButton)findViewById(R.id.volume_test);
        final SeekBar soundBar=(SeekBar)findViewById(R.id.volume_seekbar);  //闊抽噺璁剧疆
        mVolume = (TextView)findViewById(R.id.volume_test);
        audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //鑾峰彇绯荤粺鏈�澶ч煶閲�
        soundBar.setMax(maxVolume);   //鎷栧姩鏉℃渶楂樺�间笌绯荤粺鏈�澶у０鍖归厤
        currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //鑾峰彇褰撳墠鍊�
        soundBar.setProgress(currentVolume);
        mVolume.setText(currentVolume*100/maxVolume + " %");

        soundBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() //璋冮煶鐩戝惉鍣�
        {
            public void onProgressChanged(SeekBar arg0,int progress,boolean fromUser)
            {
                audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //鑾峰彇褰撳墠鍊�
                soundBar.setProgress(currentVolume);
                mVolume.setText(currentVolume*100/maxVolume + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });
    }
}
