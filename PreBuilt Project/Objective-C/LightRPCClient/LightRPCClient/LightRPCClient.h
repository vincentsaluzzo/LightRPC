//
//  LightRPCClient.h
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LightRPCConfig.h"
#import "LightRPCRequest.h"
#import "LightRPCResponse.h"
#import "ASIHttpRequest/ASIHTTPRequest.h"
#import "NSString+CommonCrypto.h"
@interface LightRPCClient : NSObject {
    LightRPCConfig* configuration;
    LightRPCRequest* lastRequest;
    LightRPCResponse* lastResponse;
}

@property(readonly) LightRPCConfig* configuration;
@property(readonly) LightRPCRequest* lastRequest;
@property(readonly) LightRPCResponse* lastResponse;


-(LightRPCClient*) initWithConfiguration:(LightRPCConfig*)pConfig;
-(LightRPCResponse*) executeRequest:(LightRPCRequest*)pRequest;
-(NSString*) parseXMLForParameter:(TBXMLElement*)pE;
-(NSString*) buildRequest:(LightRPCRequest*) pRequest;
-(NSString*) buildResponse:(NSString*) pResponse;
@end
