//
//  LightRPCConfig.m
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import "LightRPCConfig.h"

@implementation LightRPCConfig
@synthesize address;
@synthesize securityEncryption;
@synthesize securityEncryptionType;
@synthesize securityEncryptionPassphrase;

-(LightRPCConfig*)initWithAddress:(NSString *)pAddress {
    self = [self init];
    if(self) {
        self.address = [pAddress retain];
        securityEncryption = NO;
        securityEncryptionType = [[NSString alloc] init];
        securityEncryptionPassphrase = [[NSString alloc] init];
    }
    return self;
}

-(void) addAESSecurityEncryptionWithPassphrase:(NSString *)pPassphrase {
    if([pPassphrase length] == 16) {
        securityEncryptionType = @"AES256";
        securityEncryptionPassphrase = [pPassphrase retain];
        securityEncryption = YES;
    } else {
        NSException* exception = [[NSException alloc] initWithName:@"InvalidPassphrase" reason:@"The given passphrase are bad for AES encryption. The length of the passphrase must be 16 character." userInfo:nil];
        @throw exception;
    }
}

-(void) add3DESSecurityEncryptionWithPassphrase:(NSString *)pPassphrase {
    if([pPassphrase length] == 24) {
        securityEncryptionType = @"3DES";
        securityEncryptionPassphrase = [pPassphrase retain];
        securityEncryption = YES;
    } else {
        NSException* exception = [[NSException alloc] initWithName:@"InvalidPassphrase" reason:@"The given passphrase are bad for 3DES encryption. The length of the passphrase must be 24 character." userInfo:nil];
        @throw exception;
    }
}

-(void) removeSecurityEncryption {
    securityEncryption = NO;
    securityEncryptionPassphrase = nil;
    securityEncryptionType = nil;
}
@end
