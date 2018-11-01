#import "NativeContactPlugin.h"
#import <native_contact/native_contact-Swift.h>

@implementation NativeContactPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    [SwiftNativeContactPlugin registerWithRegistrar:registrar];
}
@end
