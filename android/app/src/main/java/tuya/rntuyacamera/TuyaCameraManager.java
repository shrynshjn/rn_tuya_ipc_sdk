package tuya.rntuyacamera;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.tuya.smart.camera.middleware.widget.TuyaCameraView;

public class TuyaCameraManager extends SimpleViewManager<TuyaCameraPlayerView> {
    public static final String REACT_CLASS = "TuyaCameraPlayer";
    public  static final String PROP_DEVICE_ID = "deviceId";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    };

    @NonNull
    @Override
    protected TuyaCameraPlayerView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        return new TuyaCameraPlayerView(themedReactContext);
    }

    @ReactProp(name = PROP_DEVICE_ID)
    public void setPropDeviceId(TuyaCameraPlayerView view, String deviceId) {
        if (!view.deviceId.equals(deviceId)) {
            view.deviceId = deviceId;
            view.prepare();
            view.invalidate();
        }
    }
}
