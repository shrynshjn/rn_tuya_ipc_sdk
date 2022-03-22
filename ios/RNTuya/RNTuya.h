//
//  RNTuya.h
//  AlisteTuya
//
//  Created by Shreyansh Jain on 21/03/22.
//

#ifndef RNTuya_h
#define RNTuya_h

#import "React/RCTBridgeModule.h"
@interface RNTuya : NSObject <RCTBridgeModule, TuyaSmartActivatorDelegate>
@property(nonatomic, strong) TuyaSmartHomeManager *homeManager;
@end
#endif /* RNTuya_h */
