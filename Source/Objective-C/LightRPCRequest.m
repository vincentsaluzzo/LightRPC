//
//  LightRPCRequest.m
//  LightRPCClient
//
//  Created by Vincent Saluzzo on 10/06/11.
//  Copyright 2011 Vincent Saluzzo. All rights reserved.
//

#import "LightRPCRequest.h"


@implementation LightRPCRequest
@synthesize methodName;
@synthesize parameterList;

-(LightRPCRequest*) initWithMethodName:(NSString*)pMethodName andParameters:(id)param,... {
    self = [self init];
	if (self != nil) {
		id currentObject;
        va_list argList;
        parameterList = [[NSMutableArray alloc] init];
        if (param)
        {
            [parameterList addObject:param];
            va_start(argList, param);
            while ((currentObject = va_arg(argList, id)) != nil) {
                [parameterList addObject:currentObject];
            }
            va_end(argList);
        }
        
        methodName = [pMethodName retain];
	}
	return self;
}

-(LightRPCRequest*) initWithXML:(NSString*)pXml {
    self = [self init];
	if (self != nil) {
        TBXML* tbxml = [[TBXML tbxmlWithXMLString:pXml] retain];
    
        TBXMLElement* racine = [tbxml rootXMLElement];
    
        TBXMLElement* method = [TBXML childElementNamed:@"method" parentElement:racine];
    
        methodName = [TBXML textForElement:method];
    
        TBXMLElement* parameter = [TBXML childElementNamed:@"parameter" parentElement:racine];
    
        parameterList = [[NSMutableArray alloc] init];
        TBXMLElement* param = parameter->firstChild;
        while(param) {
            [parameterList addObject:[self parseXMLForParameter:param]];
            param = param->nextSibling;
        }
    
        [tbxml release];
    }
	return self;
}

-(id) parseXMLForParameter:(TBXMLElement*)pE {
    NSString* name = [[NSString alloc] initWithUTF8String:pE->name];
    if([name isEqualToString:@"string"]) {
        NSString* value = [[NSString alloc] initWithUTF8String:pE->text];
        [name release];
        return value;
    } else if([name isEqualToString:@"array"]) {
        NSMutableArray* array = [[NSMutableArray alloc] init];
        TBXMLElement* param = pE->firstChild;
        while(param) {
            [array addObject:[self parseXMLForParameter:param]];
            param = param->nextSibling;
        }
        [name release];
        return array;
    } else {
        [name release];
        return nil;
    }
}

-(NSString*) parseParameterForXML:(id)pE {
    NSString* xml = [[NSString alloc] init];
    if([pE isKindOfClass:[NSString class]]) {
        //Cas de string
        xml = [xml stringByAppendingString:@"<string>"];
        xml = [xml stringByAppendingString:(NSString*)pE];
        xml = [xml stringByAppendingString:@"</string>"];
    } else if([pE isKindOfClass:[NSMutableArray class]]) {
        //Cas de array
        xml = [xml stringByAppendingString:@"<array>"];
        
        NSMutableArray* array = (NSMutableArray*)pE;
        for(int i = 0; i < [array count]; i++) {
            xml = [xml stringByAppendingString:[self parseParameterForXML:[array objectAtIndex:i]]];
        }
        xml = [xml stringByAppendingString:@"</array>"];
    } else if([pE isKindOfClass:[NSArray class]]) {
        //Cas de array
        xml = [xml stringByAppendingString:@"<array>"];
        
        NSArray* array = (NSArray*)pE;
        for(int i = 0; i < [array count]; i++) {
            xml = [xml stringByAppendingString:[self parseParameterForXML:[array objectAtIndex:i]]];
        }
        xml = [xml stringByAppendingString:@"</array>"];
    }
    return xml;
}

-(NSString*) transformToXML {
    NSString* xml = [[NSString alloc] init];
    xml = [xml stringByAppendingString:@"<request>"];
    xml = [xml stringByAppendingString:@"<method>"];
    xml = [xml stringByAppendingString:methodName];
    xml = [xml stringByAppendingString:@"</method>"];
    xml = [xml stringByAppendingString:@"<parameter>"];
    for(int i = 0; i < [parameterList count]; i++) {
        xml = [xml stringByAppendingString:[self parseParameterForXML:[parameterList objectAtIndex:i]]];
    }
    xml = [xml stringByAppendingString:@"</parameter>"];
    xml = [xml stringByAppendingString:@"</request>"];
    return xml;
}

@end
