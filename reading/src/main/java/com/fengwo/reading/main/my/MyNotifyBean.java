package com.fengwo.reading.main.my;

public class MyNotifyBean {

    public String _id;              //

    public String id;               //
    public String name;
    public String avatar;
    public String sex;              //0未知，1男2女
    public String notify_user_id;   //
    public String source;           // comment评论、digg点赞、system系统
    public String right;            //右侧显示内容
    public String content;          //评论内容
    public String create_time;
    public String type;             //note随笔、bp拆书包评论详情页

    public MyNotifyBean() {
        super();
    }

    public MyNotifyBean(String _id, String id, String name, String avatar,
                        String sex, String notify_user_id, String source, String right,
                        String content, String create_time, String type) {
        super();
        this._id = _id;
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.sex = sex;
        this.notify_user_id = notify_user_id;
        this.source = source;
        this.right = right;
        this.content = content;
        this.create_time = create_time;
        this.type = type;
    }

}
