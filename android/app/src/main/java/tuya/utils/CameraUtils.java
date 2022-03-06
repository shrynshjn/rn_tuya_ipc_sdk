package tuya.utils;

import android.app.Application;

public final  class CameraUtils {
    private CameraUtils() {}

    public static void init(Application application) {
        FrescoManager.initFresco(application);
        CameraDoorbellManager.getInstance().init(application);
    }
}
