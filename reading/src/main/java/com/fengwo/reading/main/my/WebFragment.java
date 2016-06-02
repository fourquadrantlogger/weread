package com.fengwo.reading.main.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.SplashActivity;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.MLog;
import com.umeng.analytics.MobclickAgent;

/**
 * @author lxq - 使用帮助
 */
public class WebFragment extends Fragment implements OnClickListener {

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private ProgressBar progressBar;
    private WebView webView;

    private View saveView = null;
    public boolean needSaveView = false;

    public String url = "";

    public int source = 0;
    // 来源 1:Activity 2:Fragment

    private WebFragment() {
    }

    private static WebFragment fragment = new WebFragment();

    public static WebFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_web, container, false);

        findViewById(view);
        onClickListener();

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_title_mid.setText(title);
                tv_title_mid.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        url += "?user_id=" + GlobalParams.uid;
        webView.loadUrl(url);
        MLog.v("web", url);

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_web);
        webView = (WebView) view.findViewById(R.id.wv_web);
    }

    private void onClickListener() {
        iv_title_left.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("WebFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WebFragment");
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_return:
                switch (source) {
                    case 1:
                        fragment.getActivity().finish();
                        fragment.getActivity().overridePendingTransition(
                                R.anim.in_from_left, R.anim.out_to_right);
                        break;
                    case 2:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                }
                break;

            default:
                break;
        }
    }

}