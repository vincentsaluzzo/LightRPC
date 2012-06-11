//
//  LightRPCResponse.h
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TBXML.h"

@interface LightRPCResponse : NSObject {
    NSString* methodName;
    NSString* typeOfResponse;
    NSMutableArray* parameterList;
}

@property(readonly) NSString* methodName;
@property(readonly) NSString* typeOfResponse;
@property NSMutableArray* parameterList;

-(LightRPCResponse*) initWithMethodName:(NSString*)PMethodName andType:(NSString*)type andParameters:(id)param,...;
-(LightRPCResponse*) initWithXML:(NSString*)pXml;
-(id) parseXMLForParameter:(TBXMLElement*)pE;
-(NSString*) parseParameterForXML:(id)pE;
-(NSString*) transformToXML;



@end
