//
//  RNTuyaCameraPlayerView.h
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#ifndef RNTuyaCameraPlayerView_h
#define RNTuyaCameraPlayerView_h
#define kTuyaSmartIPCConfigAPI @"tuya.m.ipc.config.get"
#define kTuyaSmartIPCConfigAPIVersion @"2.0"

@interface RNTuyaCameraPlayer : UIView <TuyaSmartCameraDelegate>
@property(nonatomic, assign) BOOL  *speaking;
@property(nonatomic, assign) BOOL *listening;
@property(nonatomic, assign) BOOL  *playing;
@property(nonatomic, assign) NSString *deviceId;
@property(nonatomic, assign) BOOL *initialized;
@property NSObject <TuyaSmartCameraType> *camera;
@property(nonatomic, assign) BOOL  *connected;
@property(nonatomic, assign) BOOL *play;
@property(nonatomic, assign) BOOL *speak;
@property(nonatomic, assign) BOOL *listen;

@end
#endif /* RNTuyaCameraPlayerView_h */
