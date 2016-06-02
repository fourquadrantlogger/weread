package com.fengwo.reading.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 截取字符,设置textView变色、点击(TextViewUtils)
 *
 * @author Luo Sheng
 * @date 2016-3-2
 */
public class ListUtils {

    /**
     * 截取字符,设置textView特定内容变色与可点击
     *
     * @param context  上下文
     * @param str      全部内容
     * @param textView 需要改变的textView
     */
    public static void getNewTextView(Context context, String str,
                                      TextView textView) {
        textView.setText("");
        boolean isOne = false;
        List<String> list = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(str, "#", true);
        while (token.hasMoreTokens()) {
            String str1 = token.nextToken();
            if ("#".equals(str1)) {
                try {
                    isOne = true;
                    String str2 = token.nextToken();
                    if ("#".equals(str2)) {
                        str1 += str2;
                    } else {
                        str1 += str2;
                        String str3 = token.nextToken();
                        str1 += str3;
                    }
                } catch (Exception e) {
                    MLog.v("ListUtils", "" + e);
                }
            }
            list.add(str1);
        }

        if (isOne) {
            for (int i = 0; i < list.size(); i++) {
                if ("#".equals(list.get(i).subSequence(0, 1))
                        && "#".equals(list.get(i).subSequence(
                        list.get(i).length() - 1, list.get(i).length()))) {

                    SpannableString spanttt = new SpannableString(list.get(i));
                    ClickableSpan clickttt = new TextViewUtils(context, list.get(i));
                    spanttt.setSpan(clickttt, 0, list.get(i).length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    textView.append(spanttt);
                } else {
                    textView.append(list.get(i));
                }
            }
            //垂直滑动,可点击 (需修改)
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            //文字没有 # 号
            textView.setText(str);
//            textView.setMovementMethod(null);
        }
        list.clear();
    }

    /**
     * 计算ListView的Item高度的方法
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}
