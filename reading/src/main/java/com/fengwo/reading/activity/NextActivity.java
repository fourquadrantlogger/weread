package com.fengwo.reading.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.fengwo.reading.R;
import com.fengwo.reading.main.comment.CommentEditFragment;
import com.fengwo.reading.main.discover.ACEFragment;
import com.fengwo.reading.main.discover.ChoicenessBooksDetailsFragment;
import com.fengwo.reading.main.discover.ChoicenessBooksFragment;
import com.fengwo.reading.main.discover.hottopics.HotFragment;
import com.fengwo.reading.main.group.BooksFragment;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.group.SearchFragment;
import com.fengwo.reading.main.group.qun.AllQunWeekRankFragment;
import com.fengwo.reading.main.group.qun.QunDetailFragment;
import com.fengwo.reading.main.group.qun.groupMemRankFragment;
import com.fengwo.reading.main.my.Fragment_My;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.main.my.MyInfoFragment;
import com.fengwo.reading.main.my.MyNotifyFragment;
import com.fengwo.reading.main.my.ProgressFragment;
import com.fengwo.reading.main.my.RemindFragment;
import com.fengwo.reading.main.my.SettingFragment;
import com.fengwo.reading.main.my.SuggestFragment;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.main.my.achieve.Fragment_Youshubang;
import com.fengwo.reading.main.my.achieve.MyAchieveFragment;
import com.fengwo.reading.main.my.myfav.Fragment_MyFav;
import com.fengwo.reading.main.read.Fragment_BookList;
import com.fengwo.reading.main.read.Fragment_Bookpack;
import com.fengwo.reading.main.read.Fragment_Local;
import com.fengwo.reading.player.Fragment_MediaPlayer;
import com.fengwo.reading.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

public class NextActivity extends BaseActivity {

    public NextActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ActivityUtil.nextActivity = this;
        Bundle bundle = this.getIntent().getExtras();
        String fragmentname = "";
        if (bundle != null) {
            fragmentname = bundle.getString("fragmentname", "");
        }

        if (!"".equals(fragmentname)) {
            replaceFragment(fragmentname);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment_My.getInstance().is_notify = false;
    }

    private void replaceFragment(String fragmentname) {
        if (fragmentname.equals(MyInfoFragment.class.getSimpleName())) {
            replaceFragment(MyInfoFragment.getInstance());
        } else if (fragmentname.equals(SearchFragment.class.getSimpleName())) {
            replaceFragment(SearchFragment.getInstance());
        } else if (fragmentname.equals(OtherUserFragment.class.getSimpleName())) {
            replaceFragment(OtherUserFragment.getInstance());
            // 他人主页
        } else if (fragmentname.equals(GroupDetailsFragment.class.getSimpleName())) {
            replaceFragment(GroupDetailsFragment.getInstance());
            // 随笔详情
        } else if (fragmentname.equals(MyNotifyFragment.class.getSimpleName())) {
            replaceFragment(MyNotifyFragment.getInstance());
            // 讨论留言 - 回复
        } else if (fragmentname.equals(CommentEditFragment.class.getSimpleName())) {
            replaceFragment(CommentEditFragment.getInstance());
            // 拆书包详情
        } else if (fragmentname.equals(Fragment_Bookpack.class.getSimpleName())) {
            replaceFragment(Fragment_Bookpack.getInstance());
            //  我的阅历
        } else if (fragmentname.equals(ProgressFragment.class.getSimpleName())) {
            replaceFragment(ProgressFragment.getInstance());
            // 使用帮助,活动 等H5页面
        } else if (fragmentname.equals(WebFragment.class.getSimpleName())) {
            replaceFragment(WebFragment.getInstance());
            // 意见反馈
        } else if (fragmentname.equals(SuggestFragment.class.getSimpleName())) {
            replaceFragment(SuggestFragment.getInstance());
            // 账户设置
        } else if (fragmentname.equals(SettingFragment.class.getSimpleName())) {
            replaceFragment(SettingFragment.getInstance());
            // 我的随笔
        } else if (fragmentname.equals(Fragment_Suibi.class.getSimpleName())) {
            replaceFragment(Fragment_Suibi.getInstance());
            // 早晚读设置
        } else if (fragmentname.equals(RemindFragment.class.getSimpleName())) {
            replaceFragment(RemindFragment.getInstance());
            // 我的成就
        } else if (fragmentname.equals(MyAchieveFragment.class.getSimpleName())) {
            replaceFragment(MyAchieveFragment.getInstance());
            // 有书圈 - 书籍筛选
        } else if (fragmentname.equals(BooksFragment.class.getSimpleName())) {
            replaceFragment(BooksFragment.getInstance());
            // 音乐
        } else if (fragmentname.equals(Fragment_MediaPlayer.class.getSimpleName())) {
            replaceFragment(Fragment_MediaPlayer.getInstance());
            // 热门话题
        } else if (fragmentname.equals(HotFragment.class.getSimpleName())) {
            replaceFragment(HotFragment.getInstance());
            //群详情
        } else if (fragmentname.equals(QunDetailFragment.class.getSimpleName())) {
            replaceFragment(QunDetailFragment.getInstance());
            //群详情
        } else if (fragmentname.equals(groupMemRankFragment.class.getSimpleName())) {
            replaceFragment(groupMemRankFragment.getInstance());
            //达人
        } else if (fragmentname.equals(ACEFragment.class.getSimpleName())) {
            replaceFragment(ACEFragment.getInstance());
            //精选随笔(书籍)
        } else if (fragmentname.equals(ChoicenessBooksFragment.class.getSimpleName())) {
            replaceFragment(ChoicenessBooksFragment.getInstance());
            // 更多精选随笔
        } else if (fragmentname.equals(ChoicenessBooksDetailsFragment.class.getSimpleName())) {
            replaceFragment(ChoicenessBooksDetailsFragment.getInstance());
            // 往期共读
        } else if (fragmentname.equals(Fragment_BookList.class.getSimpleName())) {
            replaceFragment(Fragment_BookList.getInstance());
            //群详情
        } else if (fragmentname.equals(AllQunWeekRankFragment.class.getSimpleName())) {
            replaceFragment(AllQunWeekRankFragment.getInstance());
            //有书榜
        } else if (fragmentname.equals(Fragment_Youshubang.class.getSimpleName())) {
            replaceFragment(Fragment_Youshubang.getInstance());
            // 我的收藏
        } else if (fragmentname.equals(Fragment_MyFav.class.getSimpleName())) {
            replaceFragment(Fragment_MyFav.getInstance());
        }
        //我的离线
        else if (fragmentname.equals(Fragment_Local.class.getSimpleName())) {
            replaceFragment(Fragment_Local.getInstance());
        }
        //WithYouShuAgreement
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.replace(R.id.ll_activity_next, fragment, fragment.getClass().getName());
        }
        fragmentTransaction.commit();
    }

    // 整个平台的Controller,负责管理整个SDK的配置、操作等处理
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    // 如果有使用任一平台的SSO授权, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // 根据requestCode获取对应的SsoHandler
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                resultCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, intent);
        }
    }


    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}