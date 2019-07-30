package com.gsafety.infoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gsafety.infoapplication.utils.LocationUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long MIN_TIME = 2 * 1000;
    private static final long MIN_DISTANCE = 1;

    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvResult = findViewById(R.id.tv_result);

        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION,
                        Permission.READ_SMS, Permission.READ_PHONE_NUMBERS)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        ToastUtils.showShort("权限申请失败，导致某些功能无法使用");
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        setClickListener(R.id.btn_access_type);
                        setClickListener(R.id.btn_imei);
                        setClickListener(R.id.btn_mac);
                        setClickListener(R.id.btn_client_ip);
                        setClickListener(R.id.btn_cdma);
                        setClickListener(R.id.btn_imsi);
                        setClickListener(R.id.btn_gps);
                        setClickListener(R.id.btn_network);
                        setClickListener(R.id.btn_tel);
                        setClickListener(R.id.btn_bts);
                        setClickListener(R.id.btn_nearbts);
                        setClickListener(R.id.btn_mmac);
                        setClickListener(R.id.btn_macs);
                    }
                })
                .start();
    }


    private void setClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        String text = ((Button) view).getText() + " : ";
        switch (view.getId()) {
            case R.id.btn_access_type:
                text += getAccessType();
                mTvResult.setText(text);
                break;
            case R.id.btn_imei:
                text += PhoneUtils.getIMEI();
                mTvResult.setText(text);
                break;
            case R.id.btn_mac:
                text += DeviceUtils.getMacAddress();
                mTvResult.setText(text);
                break;
            case R.id.btn_client_ip:
                text += NetworkUtils.getIPAddress(true);
                mTvResult.setText(text);
                break;
            case R.id.btn_cdma:
                text += isCDMA();
                mTvResult.setText(text);
                break;
            case R.id.btn_imsi:
                text += PhoneUtils.getIMSI();
                mTvResult.setText(text);
                break;
            case R.id.btn_gps:
                startGPSLocation();
                break;
            case R.id.btn_network:
                text += getNetwork();
                mTvResult.setText(text);
                break;
            case R.id.btn_tel:
                text += getTel();
                mTvResult.setText(text);
                break;
            case R.id.btn_bts:
                text += getBts();
                mTvResult.setText(text);
                break;
            case R.id.btn_nearbts:
                text += getNearBts();
                mTvResult.setText(text);
                break;
            case R.id.btn_mmac:
                text += getMMAC();
                mTvResult.setText(text);
                break;
            case R.id.btn_macs:
                text += getMacs();
                mTvResult.setText(text);
                break;
            default:
                break;
        }
    }

    /**
     * 移动端接入网络方式 可选值：
     * 移动接入网络：0
     * wifi接入网络：1
     * 仅gps坐标转换：2
     *
     * @return
     */
    private int getAccessType() {
        int accessType = -1;
        /**
         *   NETWORK_ETHERNET,
         *   NETWORK_WIFI,
         *   NETWORK_4G,
         *   NETWORK_3G,
         *   NETWORK_2G,
         *   NETWORK_UNKNOWN
         */
        NetworkUtils.NetworkType type = NetworkUtils.getNetworkType();
        switch (type) {
            case NETWORK_2G:
            case NETWORK_3G:
            case NETWORK_4G:
                accessType = 0;
                break;
            case NETWORK_WIFI:
                accessType = 1;
                break;
            default:
                break;
        }
        return accessType;
    }

    /**
     * 是否为cdma。非cdma：0; cdma：1
     *
     * @return
     */
    private int isCDMA() {
        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            return 1;
        }
        return 0;
    }

    /**
     * 开启手机GPS定位
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private void startGPSLocation() {
        if (LocationUtils.isGpsEnabled()) {
            LocationUtils.register(MIN_TIME, MIN_DISTANCE, new LocationUtils.OnLocationChangeListener() {
                @Override
                public void getLastKnownLocation(Location location) {
                    setLocationMsg(location);
                }

                @Override
                public void onLocationChanged(Location location) {
                    setLocationMsg(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            });
        } else {
            LocationUtils.openGpsSettings();
        }
    }

    /**
     * 设置手机GPS数据 取值规则： 经度|纬度|半径
     *
     * @param location
     */
    private void setLocationMsg(Location location) {
        if (location != null) {
            String msg = String.valueOf(location.getLongitude()) + '|' + String.valueOf(location.getLatitude()) + '|' + String.valueOf(location.getAccuracy());
            mTvResult.setText("gps：" + msg);
        }
    }

    /**
     * 获取无线网络类型
     *
     * @return
     */
    private String getNetwork() {
        String netWork = "";
        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            int type = tm.getNetworkType();
            switch (type) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    netWork = "GPRS";
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    netWork = "EDGE";
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    netWork = "UMTS";
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    netWork = "CDMA";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    netWork = "EVDO_0";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    netWork = "EVDO_A";
                    break;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    netWork = "1xRTT";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    netWork = "HSDPA";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    netWork = "HSUPA";
                    break;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    netWork = "IDEN";
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    netWork = "EVDO_B";
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    netWork = "LTE";
                    break;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    netWork = "EHRPD";
                    break;
                case TelephonyManager.NETWORK_TYPE_GSM:
                    netWork = "GSM";
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    netWork = "HSPAP";
                    break;
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    netWork = "TD_SCDMA";
                    break;
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    netWork = "IWLAN";
                    break;
                default:
                    break;
            }
        }
        return netWork;
    }

    /**
     * 获取手机号
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private String getTel() {
        //手机号码、不保证能取到、要看sim卡里有没有此信息
        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    /**
     * 获取基站信息
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private String getBts() {
//        StringBuilder sb = new StringBuilder();
//        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
//        if (tm != null) {
//            CellLocation cel = tm.getCellLocation();
//            if (cel != null) {
//                if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//                    CdmaCellLocation location = (CdmaCellLocation) cel;
//                    //sid,nid,bid,lon,lat,signal
//                    sb.append(location.getSystemId()).append(',')
//                            .append(location.getNetworkId()).append(',')
//                            .append(location.getBaseStationId()).append(',')
//                            .append(location.getBaseStationLongitude()).append(',')
//                            .append(location.getBaseStationLatitude()).append(',')
//                            //此signal不知具体指什么
//                            .append("signal");
//                } else {
//                    //mcc, mnc,lac,cellid,signal
//                    GsmCellLocation location = (GsmCellLocation) cel;
//                    //通过operator获取 MCC 和MNC
//                    String operator = tm.getNetworkOperator();
//                    int mcc = Integer.parseInt(operator.substring(0, 3));
//                    int mnc = Integer.parseInt(operator.substring(3));
//                    sb.append(mcc).append(',').append(mnc).append(',')
//                            .append(location.getLac()).append(',')
//                            .append(location.getCid()).append(',')
//                            //此signal不知具体指什么
//                            .append("signal");
//                }
//            }
//        }
//        return sb.toString();

        StringBuilder sb = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            List<CellInfo> cellInfos = tm.getAllCellInfo();
            for (CellInfo info : cellInfos) {
                if (info.isRegistered()) {
                    generateBtsMsg(info, sb);
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取周边基站信息
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private String getNearBts() {
        StringBuilder sb = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            List<CellInfo> cellInfos = tm.getAllCellInfo();
            for (CellInfo info : cellInfos) {
                generateBtsMsg(info, sb);
                sb.append("|");
            }
        }
        String result = sb.toString();
        if (!StringUtils.isTrimEmpty(result)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 生成基站信息
     *
     * @param info
     * @param sb
     * @return
     */
    private void generateBtsMsg(CellInfo info, StringBuilder sb) {
        if (info instanceof CellInfoCdma) {
            CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
            CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
            sb.append(cellIdentityCdma.getSystemId()).append(',')
                    .append(cellIdentityCdma.getNetworkId()).append(',')
                    .append(cellIdentityCdma.getBasestationId()).append(',')
                    .append(cellIdentityCdma.getLongitude()).append(',')
                    .append(cellIdentityCdma.getLatitude()).append(',')
                    .append(cellSignalStrengthCdma.getDbm());
        } else if (info instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
            sb.append(cellIdentityGsm.getMcc()).append(',')
                    .append(cellIdentityGsm.getMnc()).append(',')
                    .append(cellIdentityGsm.getLac()).append(',')
                    .append(cellIdentityGsm.getCid()).append(',')
                    .append(cellSignalStrengthGsm.getDbm());
        } else if (info instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) info;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
            sb.append(cellIdentityLte.getMcc()).append(',')
                    .append(cellIdentityLte.getMnc()).append(',')
                    .append(cellIdentityLte.getTac()).append(',')
                    .append(cellIdentityLte.getCi()).append(',')
                    .append(cellSignalStrengthLte.getDbm());
        } else if (info instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) info;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
            sb.append(cellIdentityWcdma.getMcc()).append(',')
                    .append(cellIdentityWcdma.getMnc()).append(',')
                    .append(cellIdentityWcdma.getLac()).append(',')
                    .append(cellIdentityWcdma.getCid()).append(',')
                    .append(cellSignalStrengthWcdma.getDbm());
        }
    }

    /**
     * 获取已连热点mac信息
     *
     * @return
     */
    private String getMMAC() {
        String result = "";
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                //获取被连接网络的mac地址 
                String mac = wifiInfo.getBSSID();
                if (!StringUtils.isTrimEmpty(mac)) {
                    //信号强度0到-100的区间值
                    int single = wifiInfo.getRssi();
                    //获取被连接网络的名称，不存在则"<unknown ssid>"
                    String ssid = wifiInfo.getSSID();
                    if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    result = mac + ',' + single + ',' + ssid;
                }
            }
        }
        return result;
    }

    /**
     * 获取WI-FI列表中mac信息
     *
     * @return
     */
    private String getMacs() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        StringBuilder sb = new StringBuilder();
        if (wifiManager != null) {
          /*  if (!wifiManager.isWifiEnabled()) {
                //打开wifi
                wifiManager.setWifiEnabled(true);
            }
            //开始扫描wifi热点
            wifiManager.startScan();*/
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult scanResult : scanResults) {
                sb.append(scanResult.BSSID).append(',').append(scanResult.level).append(',').append(scanResult.SSID).append('|');
            }
        }
        String result = sb.toString();
        if (!StringUtils.isTrimEmpty(result)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 注销手机GPS定位
         */
        LocationUtils.unregister();
    }
}
