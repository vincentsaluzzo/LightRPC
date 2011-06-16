//
//  NSString+AESCrypt.m
//
//  Created by Michael Sedlaczek, Gone Coding on 2011-02-22
//  Modified by Vincent Saluzzo

#import "NSString+CommonCrypto.h"

unsigned char strToChar (char a, char b)
{
    char encoder[3] = {'\0','\0','\0'};
    encoder[0] = a;
    encoder[1] = b;
    return (char) strtol(encoder,NULL,16);
}

@implementation NSString (CommonCrypto)

- (NSString *)TripleDESEncryptWithKey:(NSString *)key
{
   NSData *plainData = [self dataUsingEncoding:NSUTF8StringEncoding];
   NSData *encryptedData = [plainData TripleDESEncryptWithKey:key];
    //NSLog([encryptedData stringWithHexBytes]);
   NSString *encryptedString = [encryptedData base64Encoding];
   
   return encryptedString;
}

- (NSString *)TripleDESDecryptWithKey:(NSString *)key
{
   NSData *encryptedData = [NSData dataWithBase64EncodedString:self];
   NSData *plainData = [encryptedData TripleDESDecryptWithKey:key];
   
   NSString *plainString = [[NSString alloc] initWithData:plainData encoding:NSUTF8StringEncoding];
   
   return [plainString autorelease];
}

- (NSString *)TripleDESEncryptWithKeyAndReturnHexString:(NSString *)key 
{
    NSData *plainData = [self dataUsingEncoding:NSUTF8StringEncoding];
    NSData *encryptedData = [plainData TripleDESEncryptWithKey:key];
    //NSLog([encryptedData stringWithHexBytes]);
    return [encryptedData stringWithHexBytes];
}

- (NSString *)TripleDESDecryptWithKeyAndHexString:(NSString *)key {
    NSData *encryptedData = [self decodeFromHexidecimal];
    NSData *plainData = [encryptedData TripleDESDecryptWithKey:key];
    
    NSString *plainString = [[NSString alloc] initWithData:plainData encoding:NSUTF8StringEncoding];
    
    return [plainString autorelease];
}

- (NSString *)AES256EncryptWithKey:(NSString *)key
{
    NSData *plainData = [self dataUsingEncoding:NSUTF8StringEncoding];
    NSData *encryptedData = [plainData AES256EncryptWithKey:key];
    //NSLog([encryptedData stringWithHexBytes]);
    NSString *encryptedString = [encryptedData base64Encoding];
    
    return encryptedString;
}

- (NSString *)AES256DecryptWithKey:(NSString *)key
{
    NSData *encryptedData = [NSData dataWithBase64EncodedString:self];
    NSData *plainData = [encryptedData AES256DecryptWithKey:key];
    
    NSString *plainString = [[NSString alloc] initWithData:plainData encoding:NSUTF8StringEncoding];
    
    return [plainString autorelease];
}

- (NSString *)AES256EncryptWithKeyAndReturnHexString:(NSString *)key 
{
    NSData *plainData = [self dataUsingEncoding:NSUTF8StringEncoding];
    NSData *encryptedData = [plainData AES256EncryptWithKey:key];
    //NSLog([encryptedData stringWithHexBytes]);
    return [encryptedData stringWithHexBytes];
}

- (NSString *)AES256DecryptWithKeyAndHexString:(NSString *)key {
    NSData *encryptedData = [self decodeFromHexidecimal];
    NSData *plainData = [encryptedData AES256DecryptWithKey:key];
    
    NSString *plainString = [[NSString alloc] initWithData:plainData encoding:NSUTF8StringEncoding];
    
    return [plainString autorelease];
}

- (NSData *) decodeFromHexidecimal;
{
    const char * bytes = [self cStringUsingEncoding: NSUTF8StringEncoding];
    NSUInteger length = strlen(bytes);
    unsigned char * r = (unsigned char *) malloc(length / 2 + 1);
    unsigned char * index = r;
    
    while ((*bytes) && (*(bytes +1))) {
        *index = strToChar(*bytes, *(bytes +1));
        index++;
        bytes+=2;
    }
    *index = '\0';
    
    NSData * result = [NSData dataWithBytes: r length: length / 2];
    free(r);
    
    return result;
}

@end
