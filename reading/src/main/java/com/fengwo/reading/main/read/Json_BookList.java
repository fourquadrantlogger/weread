package com.fengwo.reading.main.read;

import com.fengwo.reading.bean.BaseJson;

import java.util.List;

public class Json_BookList extends BaseJson {

    public List<Bean_BookList> book;
    public String is_unlock;//往期书单特权 :1解锁 0未解锁
}
