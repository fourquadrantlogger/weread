package com.fengwo.reading.main.read;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.my.UserReadBean;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.FileUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


import java.io.File;
import java.util.List;

/**
 * Created by timeloveboy on 16/5/19.
 */
public class Adapter_BookList extends BaseAdapter {

    private Fragment fromFragment;
    private List<UserReadBean> list;
    private boolean local=false;
    private List<Json_BookInfoWithPacks> list_2;
    public Adapter_BookList(Fragment fromFragment, List<UserReadBean> list) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;
        local=false;
    }
    public Adapter_BookList(Fragment fromFragment, List<Json_BookInfoWithPacks> list,boolean nouse) {
        super();
        this.fromFragment = fromFragment;
        this.list_2=list;
        local=true;
    }
    @Override
    public int getCount() {
        if(local){
            return list_2 != null ? list_2.size() : 0;
        }else {
            return list != null ? list.size() : 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity())
                    .inflate(R.layout.item_partbooks, parent, false);

            holder.rl_item_partbooks = (RelativeLayout) convertView.findViewById(R.id.rl_item_booklist);
            holder.iv_item_booklist_bookcover = (ImageView) convertView.findViewById(R.id.iv_item_booklist_bookcover);
            holder.tv_item_booklist_booktitle = (TextView) convertView.findViewById(R.id.tv_item_booklist_booktitle);
            holder.tv_item_booklist_qi = (TextView) convertView.findViewById(R.id.tv_item_booklist_qi);
            holder.tv_item_booklist_author = (TextView) convertView.findViewById(R.id.tv_item_booklist_author);
            holder.tv_item_booklist_wereadtime = (TextView) convertView.findViewById(R.id.tv_item_booklist_wereadtime);

            holder.iv_item_booklist_deletelocal=(ImageView)convertView.findViewById(R.id.iv_item_booklist_deletelocal);
            holder.tv_item_booklist_localsize=(TextView)convertView.findViewById(R.id.tv_item_partbooks_localsize);
            holder.tv_item_booklist_alreadycount=(TextView)convertView.findViewById(R.id.tv_item_booklist_alreadycount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       if(local){
           holder.iv_item_booklist_deletelocal.setVisibility(View.VISIBLE);
           holder.tv_item_booklist_localsize.setVisibility(View.VISIBLE);
           holder.tv_item_booklist_alreadycount.setVisibility(View.VISIBLE);


           holder.tv_item_booklist_localsize.setText(list_2.get(position).localsize());
           if(list_2.get(position).data_allhavemedia().size()!=list_2.get(position).mediaalreadycount()) {
               holder.tv_item_booklist_alreadycount.setText("已下载 " + list_2.get(position).mediaalreadycount() + "篇");
               holder.iv_item_booklist_deletelocal.setImageResource(R.drawable.offline_download);
           }else {
               holder.iv_item_booklist_deletelocal.setImageResource(R.drawable.offline_delete);
               holder.tv_item_booklist_alreadycount.setText("已下载完成:" + list_2.get(position).mediaalreadycount() + "篇");
           }
           holder.tv_item_booklist_wereadtime.setVisibility(View.GONE);
           holder.tv_item_booklist_booktitle.setText(list_2.get(position).book_data.book_title);
           holder.tv_item_booklist_qi.setVisibility(View.GONE);
           holder.tv_item_booklist_author.setText(list_2.get(position).book_data.author + "\t著");

           if (!list_2.get(position).book_data.book_cover_exist()) {
               DisplayImageUtils.displayImage(list_2.get(position).book_data.book_cover,holder.iv_item_booklist_bookcover, 0, R.drawable.zanwufengmian);
               HttpUtils http = new HttpUtils();
               http.download(list_2.get(position).book_data.book_cover,list_2.get(position).book_data.book_cover_local(), new RequestCallBack<File>() {
                   @Override
                   public void onSuccess(ResponseInfo<File> responseInfo) {
                       MLog.v("reading", "book cover:" +list_2.get(position).book_data.book_cover_local());
                   }

                   @Override
                   public void onFailure(HttpException e, String s) {

                   }
               });
           }
           else
               holder.iv_item_booklist_bookcover.setImageBitmap(ImageUtils.getLoacalBitmap(list_2.get(position).book_data.book_cover_local()));

           holder.iv_item_booklist_deletelocal.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (list_2 == null) return;

                   Json_BookInfoWithPacks bookinfo = list_2.get(position);
                   //删除该书的
                   //音频文件夹

                   FileUtil.delFolder(bookinfo.bookfolder());
                   //相关 领读包
                   NOsqlUtil.del_BookInfoWithPacks_local(bookinfo.data.get(0).pb_id);
                   list_2.remove(position);
                   CustomToast.showToast(fromFragment.getActivity(), "删除成功");
                   notifyDataSetChanged();
               }
           });
       }else {
           holder.iv_item_booklist_deletelocal.setVisibility(View.GONE);
           holder.tv_item_booklist_localsize.setVisibility(View.GONE);
           holder.tv_item_booklist_alreadycount.setVisibility(View.GONE);

           holder.tv_item_booklist_wereadtime.setText("共读时间" + list.get(position).wereadtime_data_to_date());
           holder.tv_item_booklist_booktitle.setText(list.get(position).book_title);
           holder.tv_item_booklist_qi.setText(list.get(position).qi);
           holder.tv_item_booklist_author.setText(list.get(position).author + "\t著");
           DisplayImageUtils.displayImage(list.get(position).book_cover, holder.iv_item_booklist_bookcover, 0, R.color.bg);
           new HttpUtils().download(list.get(position).book_cover, list.get(position).book_cover_local(), new RequestCallBack<File>() {
               @Override
               public void onSuccess(ResponseInfo<File> responseInfo) {

               }

               @Override
               public void onFailure(HttpException e, String s) {

               }
           });
       }

        holder.rl_item_partbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMengUtils.onCountListener(fromFragment.getActivity(), "GD_02_04_03");
                FragmentTransaction transaction = fromFragment.getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_BookInfoWithPacks.getInstance());
                transaction.commit();
                if(local) {
                    Fragment_BookInfoWithPacks.getInstance().pb_id = list_2.get(position).book_data.id;
                }else {
                    Fragment_BookInfoWithPacks.getInstance().pb_id = list.get(position).id;
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_item_booklist_booktitle, tv_item_booklist_qi, tv_item_booklist_author,
                tv_item_booklist_wereadtime,tv_item_booklist_localsize,tv_item_booklist_alreadycount;
        private ImageView iv_item_booklist_bookcover,iv_item_booklist_deletelocal;
        private RelativeLayout rl_item_partbooks;
    }
}