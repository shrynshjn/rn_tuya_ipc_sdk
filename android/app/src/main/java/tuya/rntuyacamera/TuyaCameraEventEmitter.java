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
    public static final String EVENT_PLAYING = "onPlayingChanged";
    public static final String EVENT_SPEAKING = "onSpeakingChanged";
    public static final String EVENT_LISTENING = "onListeningChanged";
    public static final String EVENT_STATUS = "onStatusChanged";
    public static final String EVENT_SAVE_SNAP_COMPLETE = "onSaveSnapComplete";
    static final String[] Events = {
            EVENT_PLAYING,
            EVENT_SPEAKING,
            EVENT_LISTENING,
            EVENT_STATUS,
            EVENT_CAMERA_EVENT,
            EVENT_SAVE_SNAP_COMPLETE,
    };

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            EVENT_PLAYING,
            EVENT_SPEAKING,
            EVENT_LISTENING,
            EVENT_STATUS,
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
