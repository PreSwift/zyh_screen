#import "ZyhScreenPlugin.h"
#if __has_include(<zyh_screen/zyh_screen-Swift.h>)
#import <zyh_screen/zyh_screen-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "zyh_screen-Swift.h"
#endif

@implementation ZyhScreenPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftZyhScreenPlugin registerWithRegistrar:registrar];
}
@end
