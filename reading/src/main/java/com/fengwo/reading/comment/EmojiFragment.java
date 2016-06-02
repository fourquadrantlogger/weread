package com.fengwo.reading.comment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.EmojiUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EmojiFragment extends Fragment {

    private ViewPager viewPager;
    private Button button;
    private EditText editText;

    private List<String> resList;

    public EmojiFragment() {
    }

    public static EmojiFragment fragment = new EmojiFragment();

    public static EmojiFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.fragment_emoji, container, false);
        View view = inflater.inflate(R.layout.fragment_readbooklist, container, false);

        findViewById(view);

        // 表情list
        resList = getExpressionRes(141);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        View gv3 = getGridChildView(3);
        View gv4 = getGridChildView(4);
        View gv5 = getGridChildView(5);
        View gv6 = getGridChildView(6);
        View gv7 = getGridChildView(7);
        views.add(gv1);
        views.add(gv2);
        views.add(gv3);
        views.add(gv4);
        views.add(gv5);
        views.add(gv6);
        views.add(gv7);
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
        editText.requestFocus();

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                System.out.println("==========" + editText.getText()
                        .toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    /**
     * 获取表情的gridview的子view
     */
    private View getGridChildView(int i) {
        View view = View.inflate(getActivity(), R.layout.expression_gridview,
                null);
        GridView gv = (GridView) view.findViewById(R.id.gridView1);
        List<String> list = new ArrayList<String>();
        if (i == 7) {
            list.addAll(resList.subList(20 * 7, resList.size()));
        } else {
            list.addAll(resList.subList((i - 1) * 20, i * 20));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(
                getActivity(), 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        Class<?> clz = Class
                                .forName("com.fengwo.reading.emoticon.EmojiUtils");
                        Field field = clz.getField(filename);
                        editText.append(EmojiUtils.getSmiledText(getActivity(),
                                (String) field.get(null)));
                    } else {// 删除文字或者表情
                        if (!TextUtils.isEmpty(editText.getText().toString())) {
                            int selectionStart = editText.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = editText.getText().toString();
                                String tempStr = body.substring(0,
                                        selectionStart);
                                if (tempStr.length() >= 5 && tempStr.endsWith("]") && tempStr.substring(0, tempStr.length() - 4).endsWith("[")) {
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i,
                                                selectionStart);
                                        if (EmojiUtils.containsKey(cs.toString())) {
                                            editText.getEditableText().delete(i,
                                                    selectionStart);
                                        } else {
                                            editText.getEditableText().delete(
                                                    selectionStart - 1,
                                                    selectionStart);
                                        }
                                    } else {
                                        editText.getEditableText().delete(
                                                selectionStart - 1, selectionStart);
                                    }
                                } else {
                                    editText.getEditableText().delete(
                                            selectionStart - 1, selectionStart);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }

    private List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    private void findViewById(View view) {
//        editText = (EditText) view.findViewById(R.id.editText1);
//        viewPager = (ViewPager) view.findViewById(R.id.vp_emoji_expression);
    }

}
