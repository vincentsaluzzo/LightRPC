//
//  LightRPCClientForiOSAppDelegate.h
//  LightRPCClientForiOS
//
//  Created by Vincent Saluzzo on 13/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <UIKit/UIKit.h>

@class LightRPCClientForiOSViewController;

@interface LightRPCClientForiOSAppDelegate : NSObject <UIApplicationDelegate> {

}

@property (nonatomic, retain) IBOutlet UIWindow *window;

@property (nonatomic, retain) IBOutlet LightRPCClientForiOSViewController *viewController;

@end
