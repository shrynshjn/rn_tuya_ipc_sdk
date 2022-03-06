package tuya.rntuyacamera;

import android.view.View;

import androidx.annotation.StringDef;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TuyaCameraEventEmitter {
    private final RCTEventEmitter eventEmitter;
    private int viewId = View.NO_ID;

    TuyaCameraEventEmitter(ReactContext reactContext) {
        this.eventEmitter = reactContext.getJSModule(RCTEventEmitter.class);
    }

    public static final String EVENT_CAMERA_EVENT = "onCameraEvent";
    static final String[] Events = {

            EVENT_CAMERA_EVENT,
    };

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            EVENT_CAMERA_EVENT,
    })

    @interface TuyaCameraEvents {};

    void setViewId(int viewId) {
        this.viewId = viewId;
    }

    void sendEvent(String event, WritableMap map) {
        eventEmitter.receiveEvent(viewId, event, map);
    }
}
