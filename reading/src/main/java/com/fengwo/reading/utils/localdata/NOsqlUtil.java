package com.fengwo.reading.utils.localdata;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.main.comment.BpInfoBean;
import com.fengwo.reading.main.my.achieve.Json_wodexunzhang;
import com.fengwo.reading.main.read.Json_BookInfoWithPacks;
import com.fengwo.reading.main.read.Json_BookList;
import com.fengwo.reading.main.read.Json_Index;
import com.fengwo.reading.task.config.Bean_ad;
import com.fengwo.reading.task.config.Bean_nao_ling;
import com.fengwo.reading.task.config.Bean_shudan;
import com.fengwo.reading.task.config.Bean_word_limit;
import com.fengwo.reading.utils.MLog;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timeloveboy on 16/5/4.
 */
public class NOsqlUtil {

    //region我的勋章
    public static Json_wodexunzhang get_json_wodexunzhang() {
        DB snappydb;
        Json_wodexunzhang myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("wodexunzhang", Json_wodexunzhang.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "wodexunzhang");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static boolean set_json_wodexunzhang(Json_wodexunzhang myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            snappydb.put("wodexunzhang", myObject);
            MLog.v("reading", "put:" + "http://localhost/snappydb/" + "wodexunzhang");
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }
    //endregion

    //region用户信息
    public static UserInfoBean get_userInfoBean() {
        DB snappydb;
        UserInfoBean myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("userInfoBean", UserInfoBean.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "userInfoBean");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static boolean set_userInfoBean(UserInfoBean myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject == null) {
                snappydb.del("userInfoBean");
            } else {
                snappydb.put("userInfoBean", myObject);
                MLog.v("reading", "put:" + "http://localhost/snappydb/" + "userInfoBean");
            }
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

    //region拆书包离线下载
    public static BpInfoBean get_BpInfoBean(String pack_info_id) {
        DB snappydb;
        BpInfoBean myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("pack_info?id=" + pack_info_id, BpInfoBean.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "pack_info?id=" + pack_info_id);
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static boolean set_BpInfoBean(BpInfoBean myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            snappydb.put("pack_info?id=" + myObject.id, myObject);
            MLog.v("reading", "put:" + "http://localhost/snappydb/" + "pack_info?id=" + myObject.id);
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

    //region 书信息及音频列表 离线下载
    public static Json_BookInfoWithPacks get_BookInfoWithPacks(String pb_id) {
        DB snappydb;
        Json_BookInfoWithPacks myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("book?pb_id=" + pb_id, Json_BookInfoWithPacks.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "book?pb_id=" + pb_id);
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static List<Json_BookInfoWithPacks> get_BookInfoWithPacks_local() {
        DB snappydb;
        List<Json_BookInfoWithPacks> list = new ArrayList<>();


        try {
            snappydb = DBFactory.open(MyApplication.getContext());

            String[] keys = snappydb.findKeys("book?pb_id=");
            MLog.v("reading", "findkeys:" + "http://localhost/snappydb/" + "book?pb_id=");
            for (int i = 0; i < keys.length; i++) {
                Json_BookInfoWithPacks myObject;
                String pb_id = keys[i].substring(keys[i].indexOf("=") + 1);

                try {
                    myObject = snappydb.getObject("book?pb_id=" + pb_id, Json_BookInfoWithPacks.class);
                    MLog.v("reading", "get:" + "http://localhost/snappydb/" + "book?pb_id=" + pb_id);
                    list.add(myObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    new Throwable("snappydb失败").printStackTrace();
                }

            }
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
            return list;
        }
        return list;
    }

    public static void del_BookInfoWithPacks_local(String pb_id) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());

            Json_BookInfoWithPacks bookinfo = snappydb.getObject("book?pb_id=" + pb_id, Json_BookInfoWithPacks.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "book?pb_id=" + pb_id);
            for (int i = 0; i < bookinfo.data.size(); i++) {

                try {
                    snappydb.del("pack_info?id=" + bookinfo.data.get(i).id);
                    MLog.v("reading", "del:" + "http://localhost/snappydb/" + "pack_info?id=" + bookinfo.data.get(i).id);
                } catch (Exception e) {
                    e.printStackTrace();
                    new Throwable("snappydb失败").printStackTrace();
                }

            }
            snappydb.del("book?pb_id=" + pb_id);
            MLog.v("reading", "del:" + "http://localhost/snappydb/" + "book?pb_id=" + pb_id);
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
    }

    public static boolean set_BookInfoWithPacks(Json_BookInfoWithPacks myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject.data.size() > 0)
                snappydb.put("book?pb_id=" + myObject.data.get(0).pb_id, myObject);
            MLog.v("reading", "put:" + "http://localhost/snappydb/" + "book?pb_id=" + myObject.data.get(0).pb_id);
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

    //region首页缓存
    public static Json_Index get_Json_Index() {
        DB snappydb;
        Json_Index myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("index", Json_Index.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "index");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static void set_Json_Index(Json_Index myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            snappydb.put("index", myObject);
            MLog.v("reading", "put:" + "http://localhost/snappydb/" + "index");
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
    }

    //endregionfind/bookList
    //region往期共读
    public static Json_BookList get_Json_BookList() {
        DB snappydb;
        Json_BookList myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("find/bookList", Json_BookList.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "find/bookList");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return null;
    }

    public static void set_Json_BookList(Json_BookList myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            snappydb.put("find/bookList", myObject);
            MLog.v("reading", "put:" + "http://localhost/snappydb/" + "find/bookList");
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
    }

    //endregionfind/bookList
    //region 闹铃信息
    public static Bean_nao_ling get_naoling() {
        DB snappydb;
        Bean_nao_ling myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("naoling", Bean_nao_ling.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "naoling");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
            myObject = new Bean_nao_ling() {
            };
            return myObject;
        }
    }

    public static boolean set_naoling(Bean_nao_ling myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject == null) {
                snappydb.del("naoling");
            } else {
                snappydb.put("naoling", myObject);
                MLog.v("reading", "put:" + "http://localhost/snappydb/" + "naoling");
            }
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

    //region 文字限制
    public static Bean_word_limit get_wordlimit() {
        DB snappydb;
        Bean_word_limit myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("wordlimit", Bean_word_limit.class);
            MLog.v("reading", "get:" + "http://localhost/snappydb/" + "wordlimit");
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
            myObject = new Bean_word_limit() {
            };
            return myObject;
        }
    }

    public static boolean set_wordlimit(Bean_word_limit myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject == null) {
                snappydb.del("wordlimit");
            } else {
                snappydb.put("wordlimit", myObject);
                MLog.v("reading", "put:" + "http://localhost/snappydb/" + "wordlimit");
            }
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

    //开屏广告
    public static Bean_ad getConfig_ad() {
        DB snappydb;
        Bean_ad myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("Bean_ad", Bean_ad.class);
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
            myObject = new Bean_ad() {
            };
            return myObject;
        }
    }

    public static boolean setConfig_ad(Bean_ad myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject == null) {
                snappydb.del("Bean_ad");
            } else {
                snappydb.put("Bean_ad", myObject);
            }
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }
    //几月书单
    public static Bean_shudan getConfig_shudan() {
        DB snappydb;
        Bean_shudan myObject;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            myObject = snappydb.getObject("Bean_shudan", Bean_shudan.class);
            return myObject;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
            myObject = new Bean_shudan() {
            };
            return myObject;
        }
    }

    public static boolean setConfig_shudan(Bean_shudan myObject) {
        DB snappydb;
        try {
            snappydb = DBFactory.open(MyApplication.getContext());
            if (myObject == null) {
                snappydb.del("Bean_shudan");
            } else {
                snappydb.put("Bean_shudan", myObject);
            }
            return true;
        } catch (SnappydbException e) {
            e.printStackTrace();
            new Throwable("snappydb失败").printStackTrace();
        }
        return false;
    }

}
