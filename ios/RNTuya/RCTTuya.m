//
//  RCTTuya.m
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#import <Foundation/Foundation.h>
#import <React/RCTLog.h>
#import "RCTTuya.h"
@implementation RNTuya

RCT_EXPORT_MODULE(RNTuya)

RCT_EXPORT_METHOD(initializeTuya: (RCTPromiseResolveBlock)resolve
                  rejector: (RCTPromiseRejectBlock)reject) {
  RCTLogInfo(@"AlisteTuya: initializeTuya");
  resolve(@{
    @"success": false,
  })
}

@end
