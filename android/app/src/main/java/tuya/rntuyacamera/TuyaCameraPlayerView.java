package tuya.rntuyacamera;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.tuya.smart.android.camera.sdk.TuyaIPCSdk;
import com.tuya.smart.android.camera.sdk.api.ICameraConfigInfo;
import com.tuya.smart.android.camera.sdk.api.ITuyaIPCCore;
import com.tuya.smart.camera.camerasdk.typlayer.callback.AbsP2pCameraListener;
import com.tuya.smart.camera.camerasdk.typlayer.callback.OperationDelegateCallBack;
import com.tuya.smart.camera.ipccamerasdk.p2p.ICameraP2P;
import com.tuya.smart.camera.middleware.p2p.ICameraConfig;
import com.tuya.smart.camera.middleware.p2p.ITuyaSmartCameraP2P;
import com.tuya.smart.camera.middleware.p2p.TuyaSmartCameraP2P;
import com.tuya.smart.camera.middleware.widget.AbsVideoViewCallback;
import com.tuya.smart.camera.middleware.widget.TuyaCameraView;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TuyaCameraPlayerView extends TuyaCameraView {
    ThemedReactContext context;
    private int viewId;
    private TuyaCameraEventEmitter eventEmitter;
    private ITuyaIPCCore cameraInstance;
    private ITuyaSmartCameraP2P cameraP2P;
    public  String deviceId = "";
    public Boolean speaking = false;
    public Boolean listening = false;
    public Boolean inited = false;
    public Boolean playing = false;
    private String snapPath = "";
    public static final Integer METHOD_SAVE_SNAP = 0;
    public TuyaCameraPlayerView(ThemedReactContext themedReactContext) {
        super(themedReactContext);
        this.context = themedReactContext;
        this.eventEmitter = new TuyaCameraEventEmitter(themedReactContext);
        cameraInstance = TuyaIPCSdk.getCameraInstance();
    }

    public void prepare() {
        if (deviceId.length() > 0 && inited == true) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare][deviceId] " + deviceId);
            cameraP2P = cameraInstance.createCameraP2P(deviceId);
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare][" + deviceId  + "] " + cameraInstance.getP2PType(deviceId) + "," + cameraInstance.isIPCDevice(deviceId));
            this.setViewCallback(new AbsVideoViewCallback() {
                @Override
                public void onCreated(Object o) {
                    super.onCreated(o);
                    if (cameraP2P != null) {
                        Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare] binding view using generateCameraView");
                        cameraP2P.generateCameraView(o);
                    }
                }
            });
            this.createVideoView(deviceId);
            if (cameraP2P != null) {
                Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare] register a p2p listener");
                cameraP2P.registerP2PCameraListener(new AbsP2pCameraListener() {
                    @Override
                    public void onSessionStatusChanged(Object camera, int sessionId, int sessionStatus) {
                        super.onSessionStatusChanged(camera, sessionId, sessionStatus);
                        Log.d("AlisteTuya", "[TuyaCameraPlayerView][p2plistener][statusChanged]" + sessionId + "," + sessionStatus + "," + camera.toString());
                    }
                });
                cameraP2P.connect(deviceId, connectCallBack);
            }
        } else {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare][deviceId] is not there " + deviceId);
        }
    }

    public void setId(int id) {
        super.setId(id);
        viewId = id;
        eventEmitter.setViewId(id);
    }

    public void handlePlayingChanged(Boolean value) {
        if (playing == value) {
            return;
        }
        if (cameraP2P == null) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][handleSpeakChanged]  cameraP2P is null");
            return;
        }
        if (value == true) {
            cameraP2P.startPreview(startPreviewCallback);
        } else {
            if (speaking) {
                handleSpeakChanged(false);
            }
            cameraP2P.stopPreview(stopPreviewCallback);
        }
    }

    public void handleSpeakChanged(Boolean value) {
        if (speaking == value) {
            return;
        }
        if (cameraP2P == null) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][handleSpeakChanged]  cameraP2P is null");
            return;
        }
        if (value == true) {
            cameraP2P.startAudioTalk(startSpeakCallback);
        } else  {
            cameraP2P.stopAudioTalk(stopSpeakCallback);
        }
    }

    public void handleListenChanged(Boolean value) {
        if (listening == value) {
            return;
        }
        if (cameraP2P == null) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][handleListenChanged]  cameraP2P is null");
            return;
        }
        if (value == true) {
            cameraP2P.setMute(ICameraP2P.UNMUTE, unmuteCallback);
        } else  {
            cameraP2P.setMute(ICameraP2P.MUTE, muteCallback);
        }
    }

    public void  saveSnap() {
        if (cameraP2P == null) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][saveSnap] cameraP2P is null");
            return;
        }
        String imgDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Aliste/snapshots";
        File file = new File(imgDir);
        if (!file.exists()) {
            if(!file.mkdir()) {
                // failed creating directory
                // return error
                return;
            }
        }
        DateTimeFormatter dateTimeFormatter = null;
        String fileName;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy-HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            fileName = dateTimeFormatter.format(now) + "-" + deviceId;
        } else {
            fileName = deviceId + Math.random();
        }
        snapPath = imgDir + "/" +  fileName;
        Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][saveSnap] saving snap to " + snapPath);
        cameraP2P.snapshot(imgDir, fileName, context, saveSnapCallback);
    }

    private OperationDelegateCallBack saveSnapCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            // send event to compoenent that speaking has stopeed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][saveSnapCallback][success]" + sessionId + ","  + requestId + "," + data);
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", true);
            map.putString("message", "Snap saved");
            map.putString("location", snapPath);
            eventEmitter.sendEvent(TuyaCameraEventEmitter.EVENT_SAVE_SNAP_COMPLETE, map);
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            // send event to component that speaking stop has failed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][saveSnapCallback][false]" + sessionId + ","  + requestId + "," + errCode);
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", false);
            map.putString("message", "Could not save snap");
            eventEmitter.sendEvent(TuyaCameraEventEmitter.EVENT_SAVE_SNAP_COMPLETE, map);
        }
    };
    private OperationDelegateCallBack stopSpeakCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            // send event to compoenent that speaking has stopeed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][stopSpeakCallback][success]" + sessionId + ","  + requestId + "," + data);
            speaking = false;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            // send event to component that speaking stop has failed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][stopSpeakCallback][false]" + sessionId + ","  + requestId + "," + errCode);
            speaking = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack startSpeakCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            // send event to compoenent that speaking has started
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][startSpeakCallback][success]" + sessionId + ","  + requestId + "," + data);
            speaking = true;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            // send event to component that speaking start has failed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][startSpeakCallback][false]" + sessionId + ","  + requestId + "," + errCode);
            speaking = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack unmuteCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            // send event to compoenent that speaking has stopeed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][unmuteCallback][success]" + sessionId + ","  + requestId + "," + data);
            listening = true;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            // send event to component that speaking stop has failed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][unmuteCallback][false]" + sessionId + ","  + requestId + "," + errCode);
            listening = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack muteCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            // send event to compoenent that speaking has started
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][muteCallback][success]" + sessionId + ","  + requestId + "," + data);
            listening = false;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            // send event to component that speaking start has failed
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][" + deviceId + "][muteCallback][false]" + sessionId + ","  + requestId + "," + errCode);
            listening = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack startPreviewCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][startPreviewCallback][success]" + sessionId + "," + requestId + "," + data);
            playing = true;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][startPreviewCallback][failed]" + sessionId + "," + requestId + "," + errCode);
            playing = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack stopPreviewCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][stopPreviewCallback][success]" + sessionId + "," + requestId + "," + data);
            playing = false;
            emitCurrentStatus();
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][stopPreviewCallback][failed]" + sessionId + "," + requestId + "," + errCode);
            playing = false;
            emitCurrentStatus();
        }
    };
    private OperationDelegateCallBack connectCallBack = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][connectCallBack][success]" + sessionId + "," + requestId + "," + data);
            cameraP2P.startPreview(startPreviewCallback);
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][connectCallBack][failed]" + sessionId + "," + requestId + "," + errCode);
        }
    };
    private void emitCurrentStatus () {
        WritableMap map = Arguments.createMap();
        map.putBoolean("playing", playing);
        map.putBoolean("listening", listening);
        map.putBoolean("speaking", speaking);
        eventEmitter.sendEvent(TuyaCameraEventEmitter.EVENT_STATUS, map);
    }
}
