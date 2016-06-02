package com.fengwo.reading.main.my.myfav;

import com.fengwo.reading.main.my.achieve.Xunzhang;

import java.util.List;

/**
 * Created by timeloveboy on 16/5/24.
 */
public class ListXunzhang {
    public static Xunzhang findbyId(List<Xunzhang> list,String id){
        for(int i=0;i<list.size();i++){
            if(list.get(i).id.equals(id)){
                return list.get(i);
            }
        }
        return null;
    }

    public static List<Xunzhang> removebyId(List<Xunzhang> list,String id){
        List<Xunzhang> result=list;
        for(int i=0;i<list.size();i++){
            if(list.get(i).id.equals(id)){
                result.remove(i);
            }
        }
        return result;
    }
}
