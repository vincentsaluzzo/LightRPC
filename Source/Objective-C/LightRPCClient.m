//
//  LightRPCClient.m
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import "LightRPCClient.h"


@implementation LightRPCClient
@synthesize lastRequest;
@synthesize lastResponse;
@synthesize configuration;

-(LightRPCClient*) initWithConfiguration:(LightRPCConfig *)pConfig {
    self = [self init];
    if(self) {
        configuration = [pConfig retain];
    }
    return self;
}

-(LightRPCResponse*) executeRequest:(LightRPCRequest *)pRequest {
    NSString* req = [self buildRequest:pRequest];
    
    NSURL *url = [NSURL URLWithString:@"http://localhost:8080"];
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:url];
    [request appendPostData:[req dataUsingEncoding:NSUTF8StringEncoding]];
    [request setRequestMethod:@"POST"];
    [request startSynchronous];
    NSError *error = [request error];
    if (!error) {
        NSLog(@"Reponse du serveur: %@",[request responseString]);
        NSString* response = [self buildResponse:[request responseString]];
        //NSLog(@"Reponse réel (decodé): %@", response);
        LightRPCResponse* resp = [[LightRPCResponse alloc] initWithXML:response];
        return resp;
    } else {
        return nil;
    }
}

-(NSString*) buildRequest:(LightRPCRequest*) pRequest {
    NSString* xml = [[NSString alloc] init];
    xml = [xml stringByAppendingString:@"<lightrpc>"];
    xml = [xml stringByAppendingString:@"<header>"];
    if(configuration.securityEncryption) {
        xml = [xml stringByAppendingString:@"<security-encryption>true</security-encryption>"];
    }
    xml = [xml stringByAppendingString:@"</header>"];
    xml = [xml stringByAppendingString:@"<content>"];
    if(configuration.securityEncryption) {
        if([configuration.securityEncryptionType isEqualToString:@"3DES"]) {
            NSString* xmlToEncrypt = [[pRequest transformToXML] retain];
            NSString* xmlEncrypted = [xmlToEncrypt TripleDESEncryptWithKeyAndReturnHexString:self.configuration.securityEncryptionPassphrase];
            //NSLog(@"Xml Encrypted (HexReprensation): %@",xmlEncrypted);
            xml = [xml stringByAppendingString:xmlEncrypted ];
            [xmlToEncrypt release];
        } else if([configuration.securityEncryptionType isEqualToString:@"AES256"]) {
            NSString* xmlToEncrypt = [[pRequest transformToXML] retain];
            NSString* xmlEncrypted = [xmlToEncrypt AES256EncryptWithKeyAndReturnHexString:self.configuration.securityEncryptionPassphrase];
            //NSLog(@"Xml Encrypted (HexReprensation): %@",xmlEncrypted);
            xml = [xml stringByAppendingString:xmlEncrypted ];
            [xmlToEncrypt release];
        } else {
            return nil;
        }
    } else {
        xml = [xml stringByAppendingString:[pRequest transformToXML]];
    }
    xml = [xml stringByAppendingString:@"</content>"];
    xml = [xml stringByAppendingString:@"</lightrpc>"];
    return xml;
}

-(NSString*) buildResponse:(NSString*) pResponse {
    TBXML* tbxml = [[TBXML tbxmlWithXMLString:pResponse] retain];
    TBXMLElement* racine = [tbxml rootXMLElement];
    TBXMLElement* content = [TBXML childElementNamed:@"content" parentElement:racine];
    if(configuration.securityEncryption) {
        if([configuration.securityEncryptionType isEqualToString:@"3DES"]) {
            NSString* contentEncrypted = [[NSString alloc] initWithUTF8String:content->text];
            return [contentEncrypted TripleDESDecryptWithKeyAndHexString:configuration.securityEncryptionPassphrase];
        } else if([configuration.securityEncryptionType isEqualToString:@"AES256"]) {
            NSString* contentEncrypted = [[NSString alloc] initWithUTF8String:content->text];
            return [contentEncrypted AES256DecryptWithKeyAndHexString:configuration.securityEncryptionPassphrase];
        }
    } else {
        TBXMLElement* response = [TBXML childElementNamed:@"response" parentElement:content];
        TBXMLElement* param = response->firstChild;
        NSString* xml = [[NSString alloc] initWithString:@"<response>"];
        while(param) {
            xml = [xml stringByAppendingFormat:[self parseXMLForParameter:param]];
            param = param->nextSibling;
        }
    
        xml = [xml stringByAppendingFormat:@"</response>"];
        [tbxml release];
        return xml;
    }
}

-(NSString*) parseXMLForParameter:(TBXMLElement*)pE {
    NSString* name = [[NSString alloc] initWithUTF8String:pE->name];
    if([name isEqualToString:@"string"]) {
        NSString* parameters = [[NSString alloc] initWithString:@"<string>"];
        parameters = [parameters stringByAppendingFormat:[[NSString alloc] initWithUTF8String:pE->text]];
        parameters = [parameters stringByAppendingFormat:@"</string>"];
        [name release];
        return parameters;
    } else if([name isEqualToString:@"array"]) {
        NSString* parameters = [[NSString alloc] initWithString:@"<array>"];
        TBXMLElement* param = pE->firstChild;
        while(param) {
            parameters = [parameters stringByAppendingFormat:[self parseXMLForParameter:param]];
            param = param->nextSibling;
        }
        parameters = [parameters stringByAppendingFormat:@"</array>"];
        [name release];
        return parameters;
    } else if([name isEqualToString:@"method"]) {
        return [[NSString alloc] initWithFormat:@"<method>%s</method>", pE->text];
        [name release];
        return nil;
    } else if([name isEqualToString:@"type"]) {
        return [[NSString alloc] initWithFormat:@"<type>%s</type>", pE->text];
        [name release];
        return nil;
    } else  if([name isEqualToString:@"parameter"]) {
        NSString* parameters = [[NSString alloc] initWithString:@"<parameter>"];
        TBXMLElement* param = pE->firstChild;
        while(param) {
            parameters = [parameters stringByAppendingFormat:[self parseXMLForParameter:param]];
            param = param->nextSibling;
        }
        parameters = [parameters stringByAppendingFormat:@"</parameter>"];
        [name release];
        return parameters;
    } else {
        
    }
}
@end
