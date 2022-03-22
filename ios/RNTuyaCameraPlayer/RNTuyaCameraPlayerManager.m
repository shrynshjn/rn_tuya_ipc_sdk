//
//  RNTuyaCameraPlayerManager.m
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#import <Foundation/Foundation.h>
#import "RNTuyaCameraPlayerView.h"
#import "RNTuyaCameraPlayerManager.h"
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>

@implementation RNTuyaCameraPlayerManager

RCT_EXPORT_MODULE(TuyaCameraPlayer);

- (UIView *) view {
  return [[RNTuyaCameraPlayer alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(speak, BOOL)
RCT_EXPORT_VIEW_PROPERTY(listen, BOOL)
RCT_EXPORT_VIEW_PROPERTY(deviceId, NSString)
RCT_EXPORT_VIEW_PROPERTY(initialized, BOOL)
RCT_EXPORT_VIEW_PROPERTY(play, BOOL)
RCT_EXPORT_VIEW_PROPERTY(onStatusChanged, RCTBubblingEventBlock)

@end
