package com.tianshaokai.camera;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.common.entity.AppPackageInfo;
import com.tianshaokai.common.utils.AppUtil;
import com.tianshaokai.camera.adapter.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;
    private ArrayList<String> appList = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        recyclerView.setOnItemClickListener((parent, view, position, id) -> {
//            if (fileArray == null) return;
//
//            File file = fileArray[position];
//
//            String content = FileUtil.getFileContent(file.getAbsolutePath());
//            Log.d("File", "文件内容: " + content);
//
//            Intent intent = new Intent(FileListActivity.this, FileDetailActivity.class);
//            intent.putExtra("file", content);
//            startActivity(intent);
//        });

        List<PackageInfo> packageInfoList = com.tianshaokai.framework.util.AppUtils.getInstalledPackages(this);

        if(packageInfoList == null || packageInfoList.isEmpty()) return;


        if (!hasUsageStatsPermission(this))
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 1);
        else {
            final Context context = this;
            AsyncTask.execute(new Runnable() {
                @TargetApi(Build.VERSION_CODES.O)
                @Override
                public void run() {

                    List<AppPackageInfo> appPackageInfoList = AppUtil.getInstance().getInstallPackageSizeList(AppListActivity.this, packageInfoList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appListAdapter = new AppListAdapter(appPackageInfoList, getPackageManager());
                            recyclerView.setAdapter(appListAdapter);
                        }
                    });
                }
            });

        }



    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        //http://stackoverflow.com/a/42390614/878126
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        boolean granted = false;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }
}
