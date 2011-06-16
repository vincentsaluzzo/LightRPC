//
//  LightRPCClientAppDelegate.h
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 07/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface LightRPCClientAppDelegate : NSObject <NSApplicationDelegate> {
@private
    NSWindow *window;
}

@property (assign) IBOutlet NSWindow *window;

@end
