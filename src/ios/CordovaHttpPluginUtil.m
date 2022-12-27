//
//  CordovaHttpPluginUtil.m
//  AgentSAM UAT
//
//  Created by Xena Cheng （成啸） on 2022/12/27.
//

#import <Foundation/Foundation.h>
#import "CordovaHttpPluginUtil.h"

@implementation CordovaHttpPluginUtil

static NSArray *noCheckDomains;

+(BOOL) isNoCheckURL:(NSString*) url{


    if(noCheckDomains == NULL || noCheckDomains.count == 0){
        CordovaHttpPluginUtil *cordovaHttpPluginUtil = [[CordovaHttpPluginUtil alloc] init];
        noCheckDomains = [cordovaHttpPluginUtil getNoCheckDomainsFromFile];
    }

    for (NSString *noCheckDomain in noCheckDomains) {
        if( [url containsString:noCheckDomain]){
            return YES;
        }
    }

    return NO;
}

-(NSArray*) getNoCheckDomainsFromFile{

    NSBundle *mainBundle = [NSBundle mainBundle];
    NSString *filePath = [mainBundle pathForResource:@"www/AdancedHTTPDomainConfig" ofType:@"json"];
    NSData *data = [[NSData alloc] initWithContentsOfFile:filePath];
    NSDictionary *nsDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    NSArray *noCheckDomains = [nsDictionary objectForKey:@"advancedDomain"];
    return noCheckDomains;

}

@end
