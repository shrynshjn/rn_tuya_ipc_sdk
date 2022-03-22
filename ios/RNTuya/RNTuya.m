//
//  RNTuya.m
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#import <Foundation/Foundation.h>
#import <React/RCTLog.h>
#import "RNTuya.h"
#import "TuyaAppKey.h"
@implementation RNTuya
@synthesize homeManager = _homeManager;

RCT_EXPORT_MODULE(RNTuya)

RCT_EXPORT_METHOD(initializeTuya: (RCTPromiseResolveBlock)resolve
                  rejector: (RCTPromiseRejectBlock)reject) {
  RCTLogInfo(@"AlisteTuya: initializeTuyaCalled");
  @try {
    [[TuyaSmartSDK sharedInstance] startWithAppKey:APP_KEY secretKey:APP_SECRET_KEY];
    [[TuyaSmartSDK sharedInstance] setDebugMode:YES];
    resolve(@{
      @"success": @(true),
      @"message": @"Initialization was smooth"
    });
    if (!_homeManager) {
      _homeManager = [[TuyaSmartHomeManager alloc] init];
    }
  } @catch (NSException *exception) {
    resolve(@{
      @"success": @(false),
      @"message": @"Initialization failed"
    });
  } @finally {
    RCTLogInfo(@"AlisteTuya: initialization complete");
  }
}

RCT_EXPORT_METHOD(loginTuyaUser: (NSString *)countryCode email: (NSString *)email pasword: (NSString *)password resolve: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject) {
  [[TuyaSmartUser sharedInstance] loginByEmail:countryCode
                                      email: email
                                      password: password
                                      success:^{
    RCTLogInfo(@"AlisteTuya: user logged in successfully");
    resolve(@{
      @"success": @(true),
      @"username":  [[TuyaSmartUser sharedInstance] userName],
      @"email":  [[TuyaSmartUser sharedInstance] email],
      @"user":  [[TuyaSmartUser sharedInstance] uid],
      @"message": @"User logged in successfully"
    });
  } failure:^(NSError *error) {
    resolve(@{
      @"success":  @(false),
      @"message": @"User login failed"
    });
  }];
}

RCT_EXPORT_METHOD(getTuyaVerificationCode) {}

RCT_EXPORT_METHOD(registerTuyaUser){}

RCT_EXPORT_METHOD(createTuyaHome){}

RCT_EXPORT_METHOD(getTuyaHomesList: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject) {
  RCTLogInfo(@"[AlisteTuya] tryna get the tuya home list");
  if (!_homeManager) {
    resolve(@{
      @"success": @(false),
      @"message": @"Home manager was not initialized or some bullshit",
    });
  } else {
    [_homeManager getHomeListWithSuccess:^(NSArray<TuyaSmartHomeModel *> *homes) {
      RCTLogInfo(@"[AlisteTuya] homeManager Get Home List success");
      NSMutableArray *homeList = [[NSMutableArray alloc] init];
      for (TuyaSmartHomeModel *home in homes) {
        [homeList addObject:@{
          @"name": [home name],
          @"homeId": @([home homeId])
        }];
      };
      resolve(@{
        @"success": @(true),
        @"message": @"Got homes",
        @"homes": homeList,
      });
    } failure:^(NSError *error) {
      RCTLogInfo(@"[AlisteTuya] homeManager Get Home List failed");
      resolve(@{
        @"success": @(false),
        @"message": @"Could not gett homes",
        @"error": [error localizedDescription]
      });
    }];
  }
}

RCT_EXPORT_METHOD(getTuyaWifiQRToken:
                  (NSString * )homeId
                  resolve: (RCTPromiseResolveBlock)resolve
                  reject: (RCTPromiseRejectBlock)reject) {
  [[TuyaSmartActivator sharedInstance] getTokenWithHomeId:[homeId longLongValue] success:^(NSString *result) {
    RCTLogInfo(@"[AlisteTuya] get Wifi token %@ success", result);
    resolve(@{
      @"success": @(true),
      @"token": result,
    });
  } failure:^(NSError *error) {
    resolve(@{
      @"success": @(false),
      @"message": [error localizedDescription],
    });
  }];
}

RCT_EXPORT_METHOD(getTuyaWifiQRUrl:
                  (NSString *)ssid
                  password: (NSString *)password
                  token: (NSString *)token
                  resolve: (RCTPromiseResolveBlock)resolve
                  reject: (RCTPromiseRejectBlock)reject) {
  NSDictionary *dictionary = @{
  @"s": ssid,
  @"p": password,
  @"t": token
  };
  NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:nil];
  NSString *wifiURL = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
  resolve(@{
    @"success": @(true),
    @"qrUrl": wifiURL,
  });

}

RCT_EXPORT_METHOD(startTuyaAPDevicePairing:
                  (NSString *)ssid
                  password: (NSString *)password
                  token: (NSString *)token
                  resolve: (RCTPromiseResolveBlock)resolve
                  reject: (RCTPromiseRejectBlock)reject) {
  @try {
    [TuyaSmartActivator sharedInstance].delegate = self;
    [[TuyaSmartActivator sharedInstance] startConfigWiFi:TYActivatorModeAP ssid:ssid password:password token:token timeout:120];
    resolve(@{
      @"success": @(true),
      @"message": @"Pairing has started",
    });
  } @catch (NSException *exception) {
    resolve(@{
      @"success": @(false),
      @"token": [exception description],
    });
  } @finally {
    RCTLogInfo(@"[AlisteTuya] startAPDevicePairing is called");
  }
}

RCT_EXPORT_METHOD(stopTuyaAPDevicePairing:
                  (RCTPromiseResolveBlock)resolve
                  reject: (RCTPromiseRejectBlock)reject) {
  @try {
    [TuyaSmartActivator sharedInstance].delegate = NULL;
    [[TuyaSmartActivator sharedInstance] stopConfigWiFi];
    resolve(@{
      @"success": @(true),
      @"message": @"Pairing has stopped",
    });
  } @catch (NSException *exception) {
    resolve(@{
      @"success": @(false),
      @"token": [exception description],
    });
  } @finally {
    RCTLogInfo(@"[AlisteTuya] stopAPDevicePairing is called");
  }
}

RCT_EXPORT_METHOD(getTuyaDevicesList:
                  (NSString *)homeId
                  resolve: (RCTPromiseResolveBlock)resolve
                  reject: (RCTPromiseRejectBlock)reject) {
  @try {
    RCTLogInfo(@"[AlisteTuya] trying to get devices list");
    TuyaSmartHome *home = [TuyaSmartHome homeWithHomeId:[homeId longLongValue]];
    [home getHomeDetailWithSuccess:^(TuyaSmartHomeModel *homeModel) {
      NSMutableArray *devices = [[NSMutableArray alloc] init];
      for(TuyaSmartDeviceModel *device in [home deviceList]) {
        [devices addObject:@{
          @"deviceId": [device devId],
          @"uuid": [device uuid],
          @"ability": @([device ability]),
          @"name": [device name],
        }];
      }
      resolve(@{
        @"success": @(true),
        @"devices": devices,
      });
    } failure:^(NSError *error) {
      resolve(@{
        @"success": @(false),
        @"message": [error localizedDescription],
      });
    }];
  } @catch (NSException *exception) {
    resolve(@{
      @"success": @(false),
      @"message": [exception description],
    });
  } @finally {
    RCTLogInfo(@"[AlisteTuya] get device list execution complete");
  }
}

- (void)activator:(TuyaSmartActivator *)activator didReceiveDevice:(TuyaSmartDeviceModel *)deviceModel error:(NSError *)error {
    if (deviceModel && error == nil) {
        NSString *name = deviceModel.name?deviceModel.name:NSLocalizedString(@"Unknown Name", @"Unknown name device.");
      RCTLogInfo(@"[AlisteTuya] %@ device is paired", name);
    }
    
    if (error) {
      RCTLogInfo(@"[AlisteTuya] %@ device is pairing failed", [error localizedDescription]);
    }
}
@end

