//
//  LightRPCConfig.h
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <Foundation/Foundation.h>

#define SECURITY_ENCRYPTION_TYPE_DESede DESede
#define SECURITY_ENCRYPTION_TYPE_AES256 AES256

//faire en sorte que la clé de AES256 soit égale à 16 caractere
//et faire en sorte que la clé de 3DES soit égale à 24 caractere

@interface LightRPCConfig : NSObject {
    NSString* address;
    BOOL securityEncryption;
    NSString* securityEncryptionType;
    NSString* securityEncryptionPassphrase;
}

@property NSString* address;
@property(readonly) BOOL securityEncryption;
@property(readonly)  NSString* securityEncryptionType;
@property(readonly)  NSString* securityEncryptionPassphrase;

-(LightRPCConfig*) initWithAddress:(NSString*)pAddress;
-(void) addAESSecurityEncryptionWithPassphrase:(NSString*)pPassphrase;
-(void) add3DESSecurityEncryptionWithPassphrase:(NSString*)pPassphrase;
-(void) removeSecurityEncryption;
@end
