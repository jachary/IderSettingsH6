package com.rk_itvui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rk_itvui.settings.network.WifiApFragment;
import com.rk_itvui.settings.network.WifiFragment;
import com.rk_itvui.settings.network.WireNetworkFragment;
import com.rk_itvui.settings.network.wifi.Wifi_setting;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.zxy.idersettings.R;

import java.util.ArrayList;
import java.util.HashMap;
import com.rk_itvui.utils.LogUtil;

import static com.rk_itvui.settings.LogUtils.LOGD;

/**
 * Created by guoliang.wgl on 18/3/18.
 */
public class BlueToothItem extends FullScreenActivity implements AdapterView.OnItemClickListener {
    private String SUBTAG = "BlueToothItem" ;

    ListView list;
    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> map_WiredItem = new HashMap<String, Object>();

    HashMap<String, Object> map_Dialing = new HashMap<String, Object>();
    WireNetworkFragment wireNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_settings);
        addListView();
        initFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.networkInfo, wireNetworkFragment).commit();
        //registerReciver();
        ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);

    }

    public void addListView() {
        String wireNetwork=this.getResources().getString(R.string .bluetooth);
        String wireNetworkInfo=this.getResources().getString(R.string.disconncect);

        map_WiredItem.put("networkSettingIcon", R.drawable.network_icon_eth);
        map_WiredItem.put("networkSettingItem", wireNetwork);
        map_WiredItem.put("networkSettingStatus", wireNetworkInfo);






        listItem.add(map_WiredItem);

        list = (ListView) findViewById(R.id.networkListView);

        listItemAdapter = new SimpleAdapter(this,
                listItem,
                R.layout.network_item,

                new String[] { "networkSettingIcon","networkSettingItem", "networkSettingStatus" },
                new int[] { R.id.networkSettingIcon,R.id.networkSettingItem, R.id.networkSettingStatus });

        list.setAdapter(listItemAdapter);
        list.setOnItemClickListener(this);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (0 == arg2) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.networkInfo, wireNetworkFragment).commit();
                    //fragment.getHandlerMethod(mHandler);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void startIntentWithTranlete(Activity act, Intent intent){
        ActivityAnimationTool.startActivity(act, intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){

            case 0:
                LOGD(SUBTAG,"onItemClick : index is :" + i);
                Intent intent_wifi = new Intent(this, BlueToothSettings.class);
                startIntentWithTranlete(BlueToothItem.this,intent_wifi);
                break;

            default:
                break;


        }



    }

    public void initFragment(){
        wireNetworkFragment = new WireNetworkFragment(wireNetworkHandler,this);

        getFragmentManager().beginTransaction().replace(R.id.networkInfo, wireNetworkFragment).commit();
    }

    private Handler wireNetworkHandler=new Handler() {

        public void handleMessage(Message msg) {
            String netStateInfo;

            switch(msg.what){
                case 0:
                    netStateInfo=getResources().getString(R.string.wiredNetworkUnconnected);
                    map_WiredItem.put("networkSettingStatus", netStateInfo);
                    break;
                case 1:
                    netStateInfo=getResources().getString(R.string.wiredNetworkConnected);
                    map_WiredItem.put("networkSettingStatus", netStateInfo);
                    break;
            }
            listItemAdapter.notifyDataSetChanged();
        }

    };
}
