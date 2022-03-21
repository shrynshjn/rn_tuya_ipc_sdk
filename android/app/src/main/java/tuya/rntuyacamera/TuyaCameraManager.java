package tuya.rntuyacamera;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.tuya.smart.camera.middleware.widget.TuyaCameraView;

import java.util.Map;

public class TuyaCameraManager extends SimpleViewManager<TuyaCameraPlayerView> {
    public static final String REACT_CLASS = "TuyaCameraPlayer";
    public  static final String PROP_DEVICE_ID = "deviceId";
    public  static final String PROP_SPEAK = "speak";
    public  static final String PROP_LISTEN = "listen";
    public  static final String PROP_INITED = "initialized";
    public  static final String PROP_PLAY = "play";
    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    };

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (String event : TuyaCameraEventEmitter.Events) {
            builder.put(event, MapBuilder.of("registrationName", event));
        }
        return builder.build();
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

    @ReactProp(name = PROP_SPEAK)
    public void setPropSpeak(TuyaCameraPlayerView view, Boolean speak) {
        if (view.speaking != speak) {
            view.handleSpeakChanged(speak);
        }
    }

    @ReactProp(name = PROP_LISTEN)
    public void setPropListen(TuyaCameraPlayerView view, Boolean listen) {
        if (view.listening != listen) {
            view.handleListenChanged(listen);
        }
    }

    @ReactProp(name = PROP_INITED)
    public void setPropInited(TuyaCameraPlayerView view, Boolean init) {
        if (view.inited != init) {
            view.inited = init;
            view.prepare();
            view.invalidate();
        }
    }

    @ReactProp(name = PROP_PLAY)
    public void setPropPlay(TuyaCameraPlayerView view, Boolean play) {
        if (view.playing != play) {
            view.handlePlayingChanged(play);
        }
    }

    @Override
    public void receiveCommand(@NonNull TuyaCameraPlayerView root, String commandId, @Nullable ReadableArray args) {
        Log.d("AlisteTuya", "[TuyaCameraManager][receiveCommand] " + root.getId() + "," + commandId);
        int command = Integer.parseInt(commandId);
        switch (command) {
            case 0:
                root.saveSnap();
                break;
            default:
                break;
        }
    }
}
