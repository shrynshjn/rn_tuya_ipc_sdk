//
//  RNTuyaCameraPlayerView.m
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#import <Foundation/Foundation.h>
#import "RNTuyaCameraPlayerView.h"

@implementation RNTuyaCameraPlayer
@synthesize camera = _camera;
@synthesize playing = _playing;
@synthesize connected = _connected;
@synthesize speaking = _speaking;
@synthesize listening = _listening;
@synthesize deviceId = _deviceId;

-(instancetype) init {
  self = [super init];
  if (self) {
    [self setUp];
  }
  return self;
}

-(void) setUp {
 
}

- (void) layoutSubviews {
  [super layoutSubviews];
  NSLog(@"[AlisteTuya] camera player layout subviews");
  for (UIView* view in self.subviews) {
    [view setFrame:self.bounds];
    NSLog(@"[AlisteTuya] camera player layout subviews");
  }
}

-(void) setPlay:(BOOL *)play {
  if (self.connected) {
    if (play) {
      NSLog(@"[AlisteTuya] Camera is connected, start playing");
      [self.camera startPreview];
    } else {
      NSLog(@"[AlisteTuya] Camera is connected, stop playing");
      [self.camera stopPreview];
    }
  } else {
    NSLog(@"[AlisteTuya] Camera is not connected to start playing");
  }
}

-(void) setListen:(BOOL *)listen {
  if (self.connected) {
    if (listen) {
      NSLog(@"[AlisteTuya] Camera is  connected, start listeninng");
      [self.camera enableMute:NO forPlayMode:TuyaSmartCameraPlayModePreview];
    } else {
      NSLog(@"[AlisteTuya] Camera is connected, stop listeninng");
      [self.camera enableMute:YES forPlayMode:TuyaSmartCameraPlayModePreview];
    }
  } else {
    NSLog(@"[AlisteTuya] Camera is not connected to start listening");
  }
}

-(void) setSpeak:(BOOL *)speak {
  if (self.connected) {
    if (speak) {
      NSLog(@"[AlisteTuya] Camera is  connected, start speaking");
      [self.camera startTalk];
    } else {
      NSLog(@"[AlisteTuya] Camera is connected, stop speaking");
      [self.camera stopTalk];
    }
  } else {
    NSLog(@"[AlisteTuya] Camera is not connected to start speaking");
  }
}

-(void) setDeviceId:(NSString *)deviceId {
  NSLog(@"[AlisteTuya] setting device id to: %@", deviceId);
  _deviceId = deviceId;
  if (!_connected) {
    id p2pType = @(4);
    NSString *userId = [TuyaSmartUser sharedInstance].uid;
    NSLog(@"[AlisteTuya] uid: %@", userId);
    [[TuyaSmartRequest new] requestWithApiName:kTuyaSmartIPCConfigAPI postData:@{@"devId": deviceId} version:kTuyaSmartIPCConfigAPIVersion success:^(id result) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
          TuyaSmartCameraConfig *config = [TuyaSmartCameraFactory ipcConfigWithUid:[TuyaSmartUser sharedInstance].uid localKey:@"40972e2f64495a3e" configData:result];
          self.camera = [TuyaSmartCameraFactory cameraWithP2PType:p2pType config:config delegate:self];
          [self.camera connect];
        });
    } failure:^(NSError *error) {
            // Failed to get the configurations.
      NSLog(@"[AlisteTuya] failed to get configuration");
      }];
  } else {
    NSLog(@"[AlisteTuya] camera already connencted: %@", deviceId);
  }
  
}

-(void) setInited:(BOOL *)inited {
  NSLog(@"[AlisteTuya] setting initied id to: %@", inited ? @"Yes": @"No");
}


-(void) cameraDidConnected:(id<TuyaSmartCameraType>)camera {
  NSLog(@"[AlisteTuya] %@ camera connected, %@", [self.camera devId], camera.devId);
  _connected = YES;
  NSLog(@"[AlisteTuya] %@ trying to start camera preview", [self.camera devId]);
  [self.camera startPreview];
  NSLog(@"[AlisteTuya] %@ hast started camera preview", [self.camera devId]);
}

-(void) cameraDisconnected:(id<TuyaSmartCameraType>)camera specificErrorCode:(NSInteger)errorCode {
  NSLog(@"[AlisteTuya] %@ camera disconnected", [self.camera devId]);
  _connected = NO;
  _playing = NO;
  _speaking = NO;
  _listening = NO;
}

-(void)camera:(id<TuyaSmartCameraType>)camera didOccurredErrorAtStep:(TYCameraErrorCode)errStepCode specificErrorCode:(NSInteger)errorCode {
  NSLog(@"[AlisteTuya] camera error occured~!!!!,%@ %u, %lii", camera.devId, errStepCode,(long) (long)errorCode);
}

-(void) cameraDidBeginPreview:(id<TuyaSmartCameraType>)camera {
  NSLog(@"[AlisteTuya] start preview");
  _playing = YES;
  NSLog(@"[AlisteTuya] current view width: %f, current view height: %f", camera.getCurViewWidth, camera.getCurViewHeight);
  [self addSubview:camera.videoView];
  NSLog(@"[AlisteTuya] current view width: %f, current view height: %f", self.camera.getCurViewWidth, self.camera.getCurViewHeight);
}

-(void) cameraDidStopPreview:(id<TuyaSmartCameraType>)camera {
  NSLog(@"[AlisteTuya] %@ camera pauysed", _deviceId);
  _playing = NO;
}

-(void) cameraDidBeginTalk:(id<TuyaSmartCameraType>)camera {
  NSLog(@"[AlisteTuya] %@ camera speakinng", _deviceId);
  _speaking = YES;
}

-(void) cameraDidStopTalk:(id<TuyaSmartCameraType>)camera {
  NSLog(@"[AlisteTuya] %@ camera stopped speakinng", _deviceId);
  _speaking = NO;
}


-(void) reportCurrentStatus {
  NSLog(@"[AlisteTuya] sould report status on tghhe event");
}
@end
