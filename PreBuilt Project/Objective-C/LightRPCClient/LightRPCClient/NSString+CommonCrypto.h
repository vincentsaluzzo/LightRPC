//
//  NSString+AESCrypt.h
//
//  Created by Michael Sedlaczek, Gone Coding on 2011-02-22
//  Modified by Vincent Saluzzo

#import <Foundation/Foundation.h>
#import "NSData+CommonCrypto.h"

@interface NSString (CommonCrypto)

- (NSString *)TripleDESEncryptWithKey:(NSString *)key;
- (NSString *)TripleDESDecryptWithKey:(NSString *)key;

- (NSString *)TripleDESEncryptWithKeyAndReturnHexString:(NSString *)key;
- (NSString *)TripleDESDecryptWithKeyAndHexString:(NSString *)key;


- (NSString *)AES256EncryptWithKey:(NSString *)key;
- (NSString *)AES256DecryptWithKey:(NSString *)key;

- (NSString *)AES256EncryptWithKeyAndReturnHexString:(NSString *)key;
- (NSString *)AES256DecryptWithKeyAndHexString:(NSString *)key;


- (NSData *) decodeFromHexidecimal;
@end
