package com.tianshaokai.baidudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private RadioGroup radioGroup;
    private RadioButton button1, button2;

    private CheckBox checkbox1, checkbox2;

    private Button location, indoorMap, tip;

    private LocationClient mLocationClient;

    private MyOrientationListener myOrientationListener;

    //    GCJ02（国测局坐标）
    private String CoorType_GCJ02 = "GCJ02";
    //    BD09（百度墨卡托坐标）
    private String CoorType_BD09 = "BD09";
    //    BD09ll（百度经纬度坐标)
    private String CoorType_BD09ll = "BD09ll";

    private FloorView floorView;

    private BitmapDescriptor bitmapLocationIcon;

    private float mCurrentX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.baiduMapView);
        mBaiduMap = mMapView.getMap();

        radioGroup = findViewById(R.id.radioGroup);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        tip = findViewById(R.id.tip);

        floorView = findViewById(R.id.floorView);

        checkbox1 = findViewById(R.id.checkbox1);
        checkbox2 = findViewById(R.id.checkbox2);

        checkbox1.setOnCheckedChangeListener(onCheckedChangeListener);
        checkbox2.setOnCheckedChangeListener(onCheckedChangeListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == button1.getId()) {
                    //普通地图 ,mBaiduMap是地图控制器对象
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                } else if (checkedId == button2.getId()) {
                    //卫星地图
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        location = findViewById(R.id.location);
        location.setOnClickListener(onClickListener);

        initIndoorMap();

        initLocation();


    }

    /**
     * 初始化室内地图
     */
    private void initIndoorMap() {
        mBaiduMap.setIndoorEnable(true);//打开室内图，默认为关闭状态
        floorView.setCallBack(new FloorView.CallBack() {
            @Override
            public void onClick(String strFloor, String floorID) {
                switchIndoorFloor(strFloor, floorID);
            }
        });
        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean in, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (in) {
                    // 进入室内图
                    // 通过获取回调参数 mapBaseIndoorMapInfo 便可获取室内图信
                    //息，包含楼层信息，室内ID等
                    Log.d(TAG, "进入室内地图");
                    tip.setVisibility(View.VISIBLE);
                    floorView.setVisibility(View.VISIBLE);
                    floorView.showFloor(mapBaseIndoorMapInfo);
                } else {
                    // 移除室内图
                    Log.d(TAG, "移除了室内地图");
                    tip.setVisibility(View.GONE);
                    floorView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 切换室内楼层
     * @param currentFloor 楼层
     * @param id           室内id
     */
    private void switchIndoorFloor(String currentFloor, String id) {
        MapBaseIndoorMapInfo.SwitchFloorError switchFloorError = mBaiduMap.switchBaseIndoorMapFloor(currentFloor, id);
        switch (switchFloorError) {
            case SWITCH_OK:          //切换成功
                Log.d(TAG, "进入室内地图 SWITCH_OK");
                break;
            case FLOOR_INFO_ERROR:   //切换楼层, 室内ID信息错误
                Log.e(TAG, "进入室内地图 FLOOR_INFO_ERROR");
                break;
            case FLOOR_OVERLFLOW:    //楼层溢出 即当前室内图不存在该楼层
                Log.e(TAG, "进入室内地图 FLOOR_OVERLFLOW");
                break;
            case FOCUSED_ID_ERROR:   //切换楼层室内ID与当前聚焦室内ID不匹配
                Log.e(TAG, "进入室内地图 FOCUSED_ID_ERROR");
                break;
            case SWITCH_ERROR:       //切换楼层失败
                Log.e(TAG, "进入室内地图 SWITCH_ERROR");
                break;
        }
    }

    /**
     * 初始化定位功能
     */
    private void initLocation() {
        //初始化定位图标
        bitmapLocationIcon = BitmapDescriptorFactory.fromResource(R.mipmap.bus_bsdl_location_icon);
        startLocation();
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.location:

                    break;

                case R.id.indoorMap:

                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == checkbox1.getId()) {
                if (isChecked) {
                    //开启交通图
                    mBaiduMap.setTrafficEnabled(true);
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                }
            } else if (buttonView.getId() == checkbox2.getId()) {
                if (isChecked) {
                    //开启热力图
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                } else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                }
            }
        }
    };

    private void startLocation() {
        ////定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mLocationClient = new LocationClient(getApplicationContext());

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption locationOption = new LocationClientOption();

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        locationOption.setOpenGps(true);                // 打开gps
        locationOption.setCoorType(CoorType_BD09ll);    // 设置坐标类型

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        locationOption.setScanSpan(1000);

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);



        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        //设置locationClientOption
        mLocationClient.setLocOption(locationOption);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();

        //初始化罗盘定位
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
        //开始定位
        myOrientationListener.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        //定位请求回调接口
        private boolean isFirstIn = true;

        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }

            String coorType = location.getCoorType();

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())  //获取定位精度，默认值为0.0f
                    .direction(mCurrentX) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude()) //获取纬度信息
                    .longitude(location.getLongitude())//获取经度信息
                    .build();
            //设置图标在地图上的位置
            mBaiduMap.setMyLocationData(locData);


            MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
            MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(mCurrentMode, true, bitmapLocationIcon);
            mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                isFirstIn = false;
                // 开始移动百度地图的定位地点到中心位置
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 17f);
                mBaiduMap.animateMapStatus(u);
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位的允许
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
            //开启方向传感器
            myOrientationListener.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
    }
}
