package tuya.rntuyacamera;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.tuya.smart.android.camera.sdk.TuyaIPCSdk;
import com.tuya.smart.android.camera.sdk.api.ICameraConfigInfo;
import com.tuya.smart.android.camera.sdk.api.ITuyaIPCCore;
import com.tuya.smart.camera.camerasdk.typlayer.callback.AbsP2pCameraListener;
import com.tuya.smart.camera.camerasdk.typlayer.callback.OperationDelegateCallBack;
import com.tuya.smart.camera.middleware.p2p.ICameraConfig;
import com.tuya.smart.camera.middleware.p2p.ITuyaSmartCameraP2P;
import com.tuya.smart.camera.middleware.p2p.TuyaSmartCameraP2P;
import com.tuya.smart.camera.middleware.widget.AbsVideoViewCallback;
import com.tuya.smart.camera.middleware.widget.TuyaCameraView;

public class TuyaCameraPlayerView extends TuyaCameraView {
    ThemedReactContext context;
    private int viewId;
    private TuyaCameraEventEmitter eventEmitter;
    private TuyaCameraView tuyaCameraView;
    private ITuyaIPCCore cameraInstance;
    private ITuyaSmartCameraP2P cameraP2P;
    public  String deviceId = "";
    public TuyaCameraPlayerView(ThemedReactContext themedReactContext) {
        super(themedReactContext);
        this.context = themedReactContext;
        this.eventEmitter = new TuyaCameraEventEmitter(themedReactContext);
        cameraInstance = TuyaIPCSdk.getCameraInstance();
    }

    public void prepare() {
        if (deviceId.length() > 0) {
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
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][prepare][deviceId] is not there" + deviceId);
        }
    }

    private OperationDelegateCallBack startPreviewCallback = new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][startPreviewCallback][success]" + sessionId + "," + requestId + "," + data);
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
            Log.d("AlisteTuya", "[TuyaCameraPlayerView][startPreviewCallback][failed]" + sessionId + "," + requestId + "," + errCode);
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
    public void setId(int id) {
        super.setId(id);
        viewId = id;
        eventEmitter.setViewId(id);
    }

}
