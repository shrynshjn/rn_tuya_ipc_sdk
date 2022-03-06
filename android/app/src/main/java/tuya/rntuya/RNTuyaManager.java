package tuya.rntuya;

import android.util.Log;

import com.aliste.tuva.test.MainApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.tuya.smart.android.camera.sdk.TuyaIPCSdk;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.TuyaCameraActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaCameraDevActivator;
import com.tuya.smart.sdk.api.ITuyaSmartCameraActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import tuya.utils.CameraUtils;

public class RNTuyaManager extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactApplicationContext;

    RNTuyaManager(ReactApplicationContext context) {
        super(context);
        this.reactApplicationContext = context;
    }

    public  String getName() {
        return "RNTuya";
    }

    @ReactMethod
    public void initializeTuya(Promise promise) {
        Log.d("AlisteTuya", "initializeTuyaCalled");
        try {
            TuyaHomeSdk.init(MainApplication.getInstance());
            CameraUtils.init(MainApplication.getInstance());
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", true);
            map.putString("message", "Initialization was smooth");
            promise.resolve(map);
        } catch (Exception e) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", e.getMessage());
            promise.resolve(map);
        }
    }

    @ReactMethod
    public void loginTuyaUser(String countryCode, String email, String password, Promise promise) {
        Log.d("AlisteTuya", "[loginTuyaUser]");
        ILoginCallback callback = new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("AlisteTuya", "[loginTuyaUser][success]" + user.toString());
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", true);
                map.putString("user", user.toString());
                map.putString("email", user.getEmail());
                map.putString("username", user.getUsername());
                promise.resolve(map);
            }

            @Override
            public void onError(String code, String error) {
                Log.d("AlisteTuya", "[loginTuyaUser][error]" + code + " : " + error);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", false);
                map.putString("message", error);
                promise.resolve(map);
            }
        };
        TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, email, password, callback);
    }

    @ReactMethod
    public  void getTuyaVerificationCode(String countryCode, String email, Promise promise) {
        Log.d("AliseTuya", "[getTuyaVerificationCode]");
        IResultCallback callback = new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("AlisteTuya", "[getTuyaVerificationCode][error]" + code + " : " + error);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", false);
                map.putString("message", error);
                promise.resolve(map);
            }

            @Override
            public void onSuccess() {
                Log.d("AlisteTuya", "[getTuyaVerificationCode][success]");
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", true);
                map.putString("message", "Verification Code is sent to email..");
                promise.resolve(map);
            }
        };
        TuyaHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "IN", countryCode, 1, callback);
    }
    @ReactMethod
    public void registerTuyaUser(String countryCode, String email, String password, String code, Promise promise) {
        Log.d("AlisteTuya", "[registerTuyaUser]params: " + countryCode + "," + email + "," + password + "," + code + ",");
        try {
            IRegisterCallback callback = new IRegisterCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.d("AlisteTuya", "[registerTuyaUser][success]" + user.toString());
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    map.putString("user", user.toString());
                    map.putString("email", user.getEmail());
                    map.putString("username", user.getUsername());
                    promise.resolve(map);
                }

                @Override
                public void onError(String code, String error) {
                    Log.d("AlisteTuya", "[registerTuyaUser][error]" + code + " : " + error);
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", false);
                    map.putString("message", error);
                    promise.resolve(map);
                }
            };

            TuyaHomeSdk.getUserInstance().registerAccountWithEmail(countryCode, email, password, code, callback);
        } catch (Exception e) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", e.getMessage());
            promise.resolve(map);
        }
    }

    @ReactMethod
    public void createTuyaHome(String name, Promise promise) {
        ITuyaHomeResultCallback callback = new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                Log.d("AlisteTuya", "[createTuyaHome][success]" + bean.getName() + "," + bean.getHomeId());
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", true);
                map.putString("homeId", Long.toString(bean.getHomeId()));
                map.putString("name", bean.getName());
                map.putString("status", Integer.toString(bean.getHomeStatus()));
                promise.resolve(map);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d("AlisteTuya", "[createTuyaHome][error]" + errorCode + " : " + errorMsg);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", false);
                map.putString("message", errorMsg);
                promise.resolve(map);
            }
        };
        List<String> rooms = new ArrayList<>();
        rooms.add("cameras");
        TuyaHomeSdk.getHomeManagerInstance().createHome(name, 0, 0, "", rooms, callback);
    }

    @ReactMethod
    public void getTuyaHomesList(Promise promise) {
        try {
            ITuyaGetHomeListCallback callback = new ITuyaGetHomeListCallback() {
                @Override
                public void onSuccess(List<HomeBean> homeBeans) {
                    Log.d("AlisteTuya", "[getTuyaHomesList][success]" + homeBeans.toString());
                    WritableMap map = Arguments.createMap();
                    WritableArray homes = Arguments.createArray();
                    for (HomeBean homeBean : homeBeans) {
                        WritableMap home = Arguments.createMap();
                        home.putString("name", homeBean.getName());
                        home.putString("homeId", Long.toString(homeBean.getHomeId()));
                        homes.pushMap(home);
                    }
                    map.putArray("homes", homes);
                    map.putBoolean("success", true);
                    promise.resolve(map);
                }

                @Override
                public void onError(String errorCode, String error) {
                    Log.d("AlisteTuya", "[getTuyaHomesList][error]" + errorCode + " : " + error);
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", false);
                    map.putString("message", error);
                    promise.resolve(map);
                }
            };
            TuyaHomeSdk.getHomeManagerInstance().queryHomeList(callback);
        } catch (Exception e) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", e.getMessage());
            promise.resolve(map);
        }
    }

    @ReactMethod
    public void getTuyaWifiQRToken(String homeId, Promise promise) {
        try {
            ITuyaActivatorGetToken callback = new ITuyaActivatorGetToken() {
                @Override
                public void onSuccess(String token) {
                    Log.d("AlisteTuya", "[getTuyaWifiQRToken][success]" + token);
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    map.putString("token", token);
                    promise.resolve(map);
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    Log.d("AlisteTuya", "[getTuyaWifiQRToken][error]" + errorCode + " : " + errorMsg);
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", false);
                    map.putString("message", errorMsg);
                    promise.resolve(map);
                }
            };
          TuyaHomeSdk.getActivatorInstance().getActivatorToken(Long.valueOf(homeId), callback);
        } catch (Exception e) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", e.getMessage());
            promise.resolve(map);
        }
    }

    @ReactMethod
    public void getTuyaWifiQRUrl(String ssid, String password, String token, Promise promise) {
        Log.d("AlisteTuya", "[getTuyaWifiQRUrl]params: " + ssid + "," + password + "," + token + ",");
        ITuyaSmartCameraActivatorListener listener = new ITuyaSmartCameraActivatorListener() {
            @Override
            public void onQRCodeSuccess(String qrcodeUrl) {
                Log.d("AlisteTuya", "[getTuyaWifiQRUrl][success]" + qrcodeUrl);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", true);
                map.putString("qrUrl", qrcodeUrl);
                promise.resolve(map);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d("AlisteTuya", "[getTuyaWifiQRUrl][error]" + errorCode + " : " + errorMsg);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", false);
                map.putString("message", errorMsg);
                promise.resolve(map);
            }

            @Override
            public void onActiveSuccess(DeviceBean devResp) {
                Log.d("AlisteTuya", "[getTuyaWifiQRToken][onActiveSuccess]" + devResp.devId);
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", false);
                map.putString("message", "Device Paired Successfully");
                map.putString("deviceId", devResp.devId);
                map.putString("uuid", devResp.uuid);
                //promise.resolve(map);
            }
        };
        TuyaCameraActivatorBuilder builder = new TuyaCameraActivatorBuilder()
                .setContext(reactApplicationContext)
                .setSsid(ssid)
                .setPassword(password)
                .setToken(token)
                .setTimeOut(100000)
                .setListener(listener);
        ITuyaCameraDevActivator mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newCameraDevActivator(builder);
        mTuyaActivator.createQRCode();
        mTuyaActivator.start();
    }

    @ReactMethod
    public void getTuyaDevicesList(String homeId, Promise promise) {
        try {
            ITuyaHomeResultCallback callback = new ITuyaHomeResultCallback() {
                @Override
                public void onSuccess(HomeBean bean) {
                    Log.d("AlisteTuya", "[getTuyaDevicesList][success]" + bean.getName());
                    List<DeviceBean> deviceBeans = bean.getDeviceList();
                    WritableArray devices = Arguments.createArray();
                    for (DeviceBean deviceBean: deviceBeans) {
                        WritableMap device = Arguments.createMap();
                        device.putString("deviceId", deviceBean.devId);
                        device.putString("uuid", deviceBean.uuid);
                        device.putInt("ability", deviceBean.ability);
                        device.putString("name", deviceBean.name);
                        device.putString("getDevId", deviceBean.getDevId());
                        devices.pushMap(device);
                    }
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    map.putArray("devices", devices);
                    promise.resolve(map);
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    Log.d("AlisteTuya", "[getTuyaDevicesList][error]" + errorCode + " : " + errorMsg);
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", false);
                    map.putString("message", errorMsg);
                    promise.resolve(map);
                }
            };
            TuyaHomeSdk.newHomeInstance(Long.valueOf(homeId)).getHomeDetail(callback);
        } catch (Exception e) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", e.getMessage());
            promise.resolve(map);
        }
    }
}
