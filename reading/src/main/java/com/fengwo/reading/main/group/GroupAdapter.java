package com.fengwo.reading.main.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.main.discover.ChoicenessBooksDetailsFragment;
import com.fengwo.reading.main.discover.DiscoverFragment;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.main.my.SuggestFragment;
import com.fengwo.reading.main.my.myfav.Fragment_MyFav;
import com.fengwo.reading.qq.ImageBrowserActivity;
import com.fengwo.reading.utils.DateUtils;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.ListUtils;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VipImageUtil;

import java.util.List;

public class GroupAdapter extends BaseAdapter {

    //不同的布局
    public static final int ALL = 0;        //随笔
    public static final int PINGFEN = 1;    //评分
    private LayoutInflater mInflater;

    private Fragment fromFragment;
    private List<GroupBean> list;
    //来源 1:有书圈 2:他人主页 3:我的收藏 4:我的随笔
    //5:搜索-精选 6:搜索-所有 7:发现-书评 8: 9:发现-精选模块
    private int source = 0;

    public GroupAdapter(Fragment fromFragment, List<GroupBean> list, int source) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;
        this.source = source;

        mInflater = (LayoutInflater) fromFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        ViewHolder holder = null;
        PingFenHolder PFholder = null;

        if (convertView == null) {
            switch (source) {
                case 1:
                    if (type == PINGFEN) {
                        PFholder = new PingFenHolder();
                        convertView = mInflater.inflate(R.layout.item_group_pingfen, null);
                        PFholder.rl_group_item_rl = (RelativeLayout) convertView
                                .findViewById(R.id.rl_group_item_rl);
                        PFholder.tv_group_item_title = (TextView) convertView
                                .findViewById(R.id.tv_group_item_title);
                        PFholder.tv_group_item_like = (TextView) convertView
                                .findViewById(R.id.tv_group_item_like);
                        PFholder.tv_group_item_dislike = (TextView) convertView
                                .findViewById(R.id.tv_group_item_dislike);
                        PFholder.tv_group_item_go = (TextView) convertView
                                .findViewById(R.id.tv_group_item_go);
                        PFholder.tv_group_item_fankui = (TextView) convertView
                                .findViewById(R.id.tv_group_item_fankui);
                        convertView.setTag(PFholder);
                        break;
                    }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    holder = new ViewHolder();
                    convertView = mInflater.from(fromFragment.getActivity())
                            .inflate(R.layout.item_group, parent, false);
                    holder.view = (View) convertView
                            .findViewById(R.id.view);
                    holder.rl_group_other = (RelativeLayout) convertView
                            .findViewById(R.id.rl_group_other);
                    holder.rl_group_pinglun = (RelativeLayout) convertView
                            .findViewById(R.id.rl_group_pinglun);
                    holder.rl_group_fenxiang = (RelativeLayout) convertView
                            .findViewById(R.id.rl_group_fenxiang);
                    holder.rl_group_dianzan = (RelativeLayout) convertView
                            .findViewById(R.id.rl_group_dianzan);
                    holder.relativeLayout = (RelativeLayout) convertView
                            .findViewById(R.id.relativeLayout1);
                    holder.ll_group_details = (LinearLayout) convertView
                            .findViewById(R.id.ll_group_details);
                    holder.iv_group_avatar = (ImageView) convertView
                            .findViewById(R.id.iv_group_avatar);
                    holder.iv_group_sex = (ImageView) convertView
                            .findViewById(R.id.iv_group_sex);
                    holder.iv_group_down = (ImageView) convertView
                            .findViewById(R.id.iv_group_down);
                    holder.gv_group_gridview = (GridView) convertView
                            .findViewById(R.id.gv_group_gridview);
                    holder.tv_group_bookname = (TextView) convertView.findViewById(R.id.tv_group_bookname);
                    holder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
                    holder.tv_group_time = (TextView) convertView
                            .findViewById(R.id.tv_group_time);
                    holder.tv_group_title = (TextView) convertView
                            .findViewById(R.id.tv_group_title);
                    holder.tv_group_content = (TextView) convertView
                            .findViewById(R.id.tv_group_content);
                    holder.tv_group_pinglun = (TextView) convertView
                            .findViewById(R.id.tv_group_pinglun);
                    holder.tv_group_zannum = (TextView) convertView
                            .findViewById(R.id.tv_group_zannum);
                    holder.tv_group_pub = (TextView) convertView
                            .findViewById(R.id.tv_group_pub);
                    convertView.setTag(holder);
                    break;

                default:
                    break;
            }
        } else {
            switch (source) {
                case 1:
                    if (type == PINGFEN) {
                        PFholder = (PingFenHolder) convertView.getTag();
                        break;
                    }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    holder = (ViewHolder) convertView.getTag();
                    break;

                default:
                    break;
            }
        }

        switch (source) {
            case 1:
                if (type == PINGFEN) {
                    final PingFenHolder finalPFholder = PFholder;
                    PFholder.tv_group_item_like.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalPFholder.tv_group_item_title.setText("亲,给个好评呗!");
                            finalPFholder.tv_group_item_like.setVisibility(View.GONE);
                            finalPFholder.tv_group_item_dislike.setVisibility(View.GONE);
                            finalPFholder.tv_group_item_go.setVisibility(View.VISIBLE);
                            finalPFholder.tv_group_item_fankui.setVisibility(View.GONE);
                            GroupFragment.getInstance().refresh2(true);
                        }
                    });
                    PFholder.tv_group_item_dislike.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalPFholder.tv_group_item_title.setText("来吐槽一下呗!");
                            finalPFholder.tv_group_item_like.setVisibility(View.GONE);
                            finalPFholder.tv_group_item_dislike.setVisibility(View.GONE);
                            finalPFholder.tv_group_item_go.setVisibility(View.GONE);
                            finalPFholder.tv_group_item_fankui.setVisibility(View.VISIBLE);
                            GroupFragment.getInstance().refresh2(true);
                        }
                    });
                    PFholder.tv_group_item_go.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalPFholder.rl_group_item_rl.setVisibility(View.GONE);
                            GroupFragment.getInstance().refresh2(false);
                            GroupFragment.getInstance().GO();
                        }
                    });
                    PFholder.tv_group_item_fankui.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(fromFragment.getActivity(), NextActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("fragmentname", SuggestFragment.class.getSimpleName());
                            intent.putExtras(bundle);
                            fromFragment.getActivity().startActivity(intent);
                            fromFragment.getActivity().overridePendingTransition(R.anim.in_from_right,
                                    R.anim.out_to_left);
                            SuggestFragment.getInstance().needSaveView = false;
                            finalPFholder.rl_group_item_rl.setVisibility(View.GONE);
                            GroupFragment.getInstance().refresh2(false);
                        }
                    });
                    break;
                }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                switch (source) {
                    case 1:
                        if (position == 0) {
                            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.view.getLayoutParams(); // 取控件mGrid当前的布局参数
                            linearParams.height = 22;// 象素
                            holder.view.setLayoutParams(linearParams); // 布局参数
                        } else {
                            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.view.getLayoutParams(); // 取控件mGrid当前的布局参数
                            linearParams.height = 1;
                            holder.view.setLayoutParams(linearParams);
                        }
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        break;

                    default:
                        break;
                }

                // 头像
                if (list.get(position).user_data != null
                        && list.get(position).user_data.avatar != null) {
                    DisplayImageUtils.displayImage(list.get(position).user_data.avatar, holder.iv_group_avatar, 100, R.drawable.avatar);
                    holder.iv_group_sex.setVisibility(View.VISIBLE);
                    // 等级
                    if (!TextUtils.isEmpty(list.get(position).user_data.level)) {
                        int i = Integer.valueOf(list.get(position).user_data.level).intValue();
                        VipImageUtil.getVipGrade(fromFragment.getActivity(), holder.iv_group_sex, i, 1);
                    } else {
                        holder.iv_group_sex.setVisibility(View.GONE);
                    }
                    // 姓名
                    holder.tv_group_name.setText(list.get(position).user_data.name);
//                    holder.tv_group_name.setCompoundDrawables(null, null, list.get(position).user_data.badge_Drawable(0.12f, 0.12f), null);

                } else {
                    holder.iv_group_avatar.setImageResource(R.drawable.avatar);
                }

                // 发布时间
                holder.tv_group_time
                        .setText(DateUtils.getTime((list.get(position).create_time)));
                //书名
                if (!TextUtils.isEmpty(list.get(position).book_title)) {
                    holder.tv_group_bookname.setVisibility(View.VISIBLE);
                    holder.tv_group_bookname.setText("《 " + list.get(position).book_title + " 》");
                } else {
                    holder.tv_group_bookname.setVisibility(View.GONE);
                }
                // 标题
                if (!TextUtils.isEmpty(list.get(position).title)) {
                    holder.tv_group_title.setVisibility(View.VISIBLE);
                    holder.tv_group_title.setText(list.get(position).title);
                } else {
                    holder.tv_group_title.setVisibility(View.GONE);
                }
                // 内容 #话题#
                if (list.get(position).content != null) {
                    ListUtils.getNewTextView(fromFragment.getActivity(),
                            list.get(position).content, holder.tv_group_content);
                }

                // 附带图片
                if (list.get(position).img_str != null && list.get(position).img_str.length != 0) {
                    holder.gv_group_gridview.setVisibility(View.VISIBLE);
                    DisplayMetrics metric = new DisplayMetrics();
                    fromFragment.getActivity().getWindowManager().getDefaultDisplay()
                            .getMetrics(metric);
                    int width = (metric.widthPixels / 3);
                    GridViewAdapter gridViewAdapter = new GridViewAdapter(
                            fromFragment.getActivity(), list.get(position).img_str,
                            width);
                    holder.gv_group_gridview.setAdapter(gridViewAdapter);
                    holder.gv_group_gridview.setFocusable(false);
                } else {
                    holder.gv_group_gridview.setVisibility(View.GONE);
                }

                // 评论数
                if (!list.get(position).comment_count.equals("0")) {
                    holder.tv_group_pinglun.setText(list.get(position).comment_count);
                } else {
                    holder.tv_group_pinglun.setText("");
                }
                // 点赞数
                if (!list.get(position).digg_count.equals("0")) {
                    holder.tv_group_zannum.setText(list.get(position).digg_count);
                } else {
                    holder.tv_group_zannum.setText("");
                }

                if (source == 4) {
                    if (!TextUtils.isEmpty(list.get(position).is_pub)
                            && "1".equals(list.get(position).is_pub)) {
                        holder.tv_group_pub.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_group_pub.setVisibility(View.GONE);
                    }
                } else {
                    holder.tv_group_pub.setVisibility(View.GONE);
                }

                // 跳转他人主页的点击
                holder.rl_group_other.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                GroupFragment.getInstance().goOther(position);
                                break;
                            case 2:

                                break;
                            case 3:
                                Fragment_MyFav.getInstance().goOther(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().goOther(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().goOther(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().goOther(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().goOther(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().goOther(position);
                                break;

                            default:
                                break;
                        }
                    }
                });

                //浏览图片
                holder.gv_group_gridview
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int num, long id) {
                                switch (source) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                        // 采用 多图浏览Activity 模式
                                        ImageBrowserActivity.position = num;
                                        ImageBrowserActivity.mList = java.util.Arrays.asList(list.get(position).img_str_orignsize());
                                        fromFragment.getActivity().startActivity(new Intent(fromFragment.getActivity(), ImageBrowserActivity.class));
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });

                // 是否已收藏
                if ("1".equals(list.get(position).is_fav)) {
                    holder.iv_group_down.setImageResource(R.drawable.group_shoucang_red);
                } else {
                    holder.iv_group_down.setImageResource(R.drawable.group_shoucang_gray);
                }
                // 爱心(收藏)的点击
                holder.iv_group_down.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                GroupFragment.getInstance().duihaoShow(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().duihaoShow(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().duihaoShow(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().duihaoShow(position);
                                break;

                            default:
                                break;
                        }
                    }
                });

                // 分享的点击
                holder.rl_group_fenxiang.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                GroupFragment.getInstance().fenxiang(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().fenxiang(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().fenxiang(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().fenxiang(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().fenxiang(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().fenxiang(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().fenxiang(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().fenxiang(position);
                                break;
                            default:
                                break;
                        }
                    }
                });

                // 跳转讨论详情的点击
                holder.ll_group_details.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                UMengUtils.onCountListener(fromFragment.getActivity(), "GD_03_07");
                                GroupFragment.getInstance().goDetails(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().goDetails(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().goDetails(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().goDetails(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().goDetails(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().goDetails(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().goDetails(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().goDetails(position);
                                break;

                            default:
                                break;
                        }
                    }
                });
                // 标题和内容的点击
                holder.tv_group_title.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                UMengUtils.onCountListener(fromFragment.getActivity(), "GD_03_07");
                                GroupFragment.getInstance().goDetails(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().goDetails(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().goDetails(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().goDetails(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().goDetails(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().goDetails(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().goDetails(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().goDetails(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                holder.tv_group_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                UMengUtils.onCountListener(fromFragment.getActivity(), "GD_03_07");
                                GroupFragment.getInstance().goDetails(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().goDetails(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().goDetails(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().goDetails(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().goDetails(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().goDetails(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().goDetails(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().goDetails(position);
                                break;
                            default:
                                break;
                        }
                    }
                });

                // 评论的点击
                holder.rl_group_pinglun.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转 发表留言
                        switch (source) {
                            case 1:
                                UMengUtils.onCountListener(fromFragment.getActivity(), "GD_03_05");
                                GroupFragment.getInstance().goDetails(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().goDetails(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().goDetails(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().goDetails(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().goDetails(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().goDetails(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().goDetails(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().goDetails(position);
                                break;
                            default:
                                break;
                        }
                    }
                });

                // 是否点赞过
                if ("1".equals(list.get(position).is_digg)) {
                    Drawable drawable = fromFragment.getActivity().getResources().getDrawable(
                            R.drawable.comment_zan_hou);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    holder.tv_group_zannum
                            .setCompoundDrawables(drawable, null, null, null);
                    holder.tv_group_zannum.setTextColor(fromFragment.getResources()
                            .getColor(R.color.zan_text_color));
                } else {
                    Drawable drawable = fromFragment.getActivity().getResources().getDrawable(
                            R.drawable.comment_zan);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    holder.tv_group_zannum
                            .setCompoundDrawables(drawable, null, null, null);
                    holder.tv_group_zannum.setTextColor(fromFragment.getResources()
                            .getColor(R.color.text_98));
                }
                // 点赞的点击
                holder.rl_group_dianzan.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (source) {
                            case 1:
                                GroupFragment.getInstance().dianzan(position);
                                break;
                            case 2:
                                OtherUserFragment.getInstance().dianzan(position);
                                break;
                            case 3:
                                Fragment_MyFav.getInstance().dianzan(position);
                                break;
                            case 4:
                                Fragment_Suibi.getInstance().dianzan(position);
                                break;
                            case 5:
                                SearchFragment.getInstance().dianzan(position, 5);
                                break;
                            case 6:
                                SearchFragment.getInstance().dianzan(position, 6);
                                break;
                            case 7:
                                DiscoverFragment.getInstance().dianzan(position);
                                break;
                            case 8:

                                break;
                            case 9:
                                ChoicenessBooksDetailsFragment.getInstance().dianzan(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                // 记录位置
                holder.tv_group_zannum.setTag("dianzan_tv" + position);
                holder.tv_group_pinglun.setTag("pinglun" + position);
                break;
        }
        return convertView;
    }

    /**
     * 根据数据源的position返回需要显示的的layout的type
     * <p/>
     * type的值必须从0开始
     */
    @Override
    public int getItemViewType(int position) {
        GroupBean bean = list.get(position);
        int type = bean.getType();
        return type;
    }

    /**
     * 返回所有的layout的数量
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    //随笔
    private static class ViewHolder {
        private RelativeLayout rl_group_other, rl_group_dianzan,
                rl_group_pinglun, rl_group_fenxiang, relativeLayout;
        private LinearLayout ll_group_details;
        private GridView gv_group_gridview;
        private ImageView iv_group_avatar, iv_group_sex, iv_group_down;
        private TextView tv_group_name, tv_group_time, tv_group_title,
                tv_group_content, tv_group_pinglun, tv_group_zannum, tv_group_bookname;
        // TODO
        private TextView tv_group_pub;
        private View view;
    }

    //评分
    private static class PingFenHolder {
        private RelativeLayout rl_group_item_rl;
        private TextView tv_group_item_title, tv_group_item_like, tv_group_item_dislike, tv_group_item_go, tv_group_item_fankui;
    }


}
