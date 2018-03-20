package com.rk_itvui.settings.bluetooth;

/**
 * Created by guoliang.wgl on 18/3/19.
 */
public class LocalBluetoothDevice {
    private String name ;
    private String address;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }



    public LocalBluetoothDevice(String name, String address){
        this.name =  name;
        this.address =  address;
    }


}
