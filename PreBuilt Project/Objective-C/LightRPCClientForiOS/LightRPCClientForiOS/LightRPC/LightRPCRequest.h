//
//  LightRPCRequest.h
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TBXML.h"

@interface LightRPCRequest : NSObject {
    NSString* methodName;
    NSMutableArray* parameterList;
}

@property(readonly) NSString* methodName;
@property(readonly) NSMutableArray* parameterList;

-(LightRPCRequest*) initWithMethodName:(NSString*)PMethodName andParameters:(id)param,...;
-(LightRPCRequest*) initWithXML:(NSString*)pXml;
-(id) parseXMLForParameter:(TBXMLElement*)pE;
-(NSString*) parseParameterForXML:(id)pE;
-(NSString*) transformToXML;



@end
