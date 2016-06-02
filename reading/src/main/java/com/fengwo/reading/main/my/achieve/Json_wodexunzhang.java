package com.fengwo.reading.main.my.achieve;

import android.os.Environment;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.myinterface.GlobalParams;

import java.util.List;


/**
 * Created by timeloveboy on 16/4/29.
 */
public class Json_wodexunzhang extends BaseJson {
    public static String xunzhangFolder() {
        return Environment.getExternalStorageDirectory().toString() + GlobalParams.FolderPath + "勋章/";
    }

    public List<Xunzhang> data;
    public String banben;

}
