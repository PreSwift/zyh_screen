import Flutter
import UIKit

public class SwiftZyhScreenPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "github.com/PreSwift/zyh_screen", binaryMessenger: registrar.messenger())
    let instance = SwiftZyhScreenPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if ([@"brightness" isEqualToString:call.method]) {
        result([NSNumber numberWithFloat:[UIScreen mainScreen].brightness]);
      }
      else if ([@"setBrightness" isEqualToString:call.method]) {
        NSNumber *brightness = call.arguments[@"brightness"];
        [[UIScreen mainScreen] setBrightness:brightness.floatValue];
        result(nil);
      }
      else if ([@"isKeptOn" isEqualToString:call.method]) {
        bool isIdleTimerDisabled =  [[UIApplication sharedApplication] isIdleTimerDisabled];
        result([NSNumber numberWithBool:isIdleTimerDisabled]);
      }
      else if ([@"keepOn" isEqualToString:call.method]) {
        NSNumber *b = call.arguments[@"on"];
        [[UIApplication sharedApplication] setIdleTimerDisabled:b.boolValue];
      }
      else {
        result(FlutterMethodNotImplemented);
      }
  }
}
