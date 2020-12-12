package com.tianshaokai.study.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.common.entity.AppPackageInfo;
import com.tianshaokai.study.R;
import com.tianshaokai.study.interfaces.OnItemClickListener;

import java.text.MessageFormat;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    List<AppPackageInfo> packageInfoList;
    PackageManager packageManager;


    public AppListAdapter(List<AppPackageInfo> packageInfoList, PackageManager packageManager) {
        this.packageInfoList = packageInfoList;
        this.packageManager = packageManager;
    }

    private OnItemClickListener onItemClickListener;//声明一下这个接口

    //提供setter方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app_list, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据RecyclerView获得当前View的位置
                int position = viewHolder.getAdapterPosition();
                //程序执行到此，会去执行具体实现的onItemClick()方法
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemClick(v, position, myFunctionList.get(position));
//                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        AppPackageInfo packageInfo = packageInfoList.get(position);
        viewHolder.onBindViewData(packageInfo);

    }

    @Override
    public int getItemCount() {
        return packageInfoList == null ? 0 : packageInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppVersion;
        TextView tvAppSize;

        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppVersion = itemView.findViewById(R.id.tvAppVersion);
            tvAppSize = itemView.findViewById(R.id.tvAppSize);

            context = itemView.getContext();
        }

        public void onBindViewData(AppPackageInfo appPackageInfo) {


            PackageInfo packageInfo = appPackageInfo.getPackageInfo();


            ivAppIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(packageManager));
            tvAppName.setText(packageInfo.applicationInfo.loadLabel(packageManager));
            tvAppVersion.setText(MessageFormat.format("版本：{0}", packageInfo.versionName));
            tvAppSize.setText(MessageFormat.format("大小：{0}", Formatter.formatShortFileSize(context, appPackageInfo.getAppSize())));
        }
    }
}
