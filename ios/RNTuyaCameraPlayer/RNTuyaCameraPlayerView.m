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
//  [self addSubview:[[self camera] videoView]];
}

- (void) layoutSubviews {
  [super layoutSubviews];
  for (UIView* view in self.subviews) {
    [view setFrame:self.bounds];
  }
}

-(void) setDeviceId:(NSString *)deviceId {
  NSLog(@"[AlisteTuya] setting device id to: %@", deviceId);
  if (!_connected) {
    TuyaSmartDeviceModel *device = [[TuyaSmartDeviceModel alloc] init];
    [device setDevId:deviceId];
    NSLog(@"[AlisteTuya] devicemodel device name, %@", [device name]);
    self.camera = [TuyaSmartCameraFactory cameraWithP2PType:@(4) deviceId:deviceId delegate:self];
    [self.camera connect];
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
  [self addSubview:[self.camera videoView]];
  NSLog(@"[AlisteTuya] %@ trying to start camera preview", [self.camera devId]);
  [self.camera startPreview];
}

-(void) cameraDisconnected:(id<TuyaSmartCameraType>)camera specificErrorCode:(NSInteger)errorCode {
  NSLog(@"[AlisteTuya] %@ camera disconnected", [self.camera devId]);
  _connected = NO;
  _playing = NO;
  _speaking = NO;
  _listening = NO;
}

-(void)camera:(id<TuyaSmartCameraType>)camera didOccurredErrorAtStep:(TYCameraErrorCode)errStepCode specificErrorCode:(NSInteger)errorCode {
  NSLog(@"[AlisteTuya] camera error occured~!!!!,%@ %@, %i", camera.devId, errStepCode, errorCode);
}

-(void) cameraDidBeginPreview:(id<TuyaSmartCameraType>)camera {

  NSLog(@"[AlisteTuya] %@ camera playing", _deviceId);
  _playing = YES;
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
