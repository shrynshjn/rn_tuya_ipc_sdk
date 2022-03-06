package tuya.utils;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.tuya.smart.android.camera.sdk.TuyaIPCSdk;
import com.tuya.smart.android.camera.sdk.api.ITuyaIPCDoorBellManager;
import com.tuya.smart.android.camera.sdk.bean.TYDoorBellCallModel;
import com.tuya.smart.android.camera.sdk.callback.TuyaSmartDoorBellObserver;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.sdk.bean.DeviceBean;

public final class CameraDoorbellManager {
    private static final String TAG = "CameraDoorbellManager";
    public static final String EXTRA_AC_DOORBELL = "ac_doorbell";

    ITuyaIPCDoorBellManager doorBellInstance = TuyaIPCSdk.getDoorbell().getIPCDoorBellManagerInstance();

    private CameraDoorbellManager() {
    }

    public static CameraDoorbellManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final CameraDoorbellManager INSTANCE = new CameraDoorbellManager();
    }

    public void init(Application application) {
        if (doorBellInstance != null) {
            doorBellInstance.addObserver(new TuyaSmartDoorBellObserver() {
                @Override
                public void doorBellCallDidReceivedFromDevice(TYDoorBellCallModel callModel, DeviceBean deviceBean) {
                    L.d(TAG, "Receiving a doorbell call");
                    if (null == callModel) {
                        return;
                    }
                    String type = callModel.getType();
                    String messageId = callModel.getMessageId();
                    if (EXTRA_AC_DOORBELL.equals(type)) {
                        Log.d("AlisteTuya", "door bell shit");
                    }
                }
            });
        }
    }

    public void deInit() {
        if (doorBellInstance != null) {
            doorBellInstance.removeAllObservers();
        }
    }
}
