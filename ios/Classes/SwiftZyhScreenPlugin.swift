import Flutter
import UIKit

public class SwiftZyhScreenPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "github.com/PreSwift/zyh_screen", binaryMessenger: registrar.messenger())
    let instance = SwiftZyhScreenPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if ("brightness" == call.method) {
            result(UIScreen.main.brightness)
        }
        else if ("setBrightness" == call.method) {
            let brightness: CGFloat = (call.arguments as! Dictionary)["brightness", default: 1]
            UIScreen.main.brightness = brightness
            result(nil)
        }
        else if ("isKeptOn" == call.method) {
            let isIdleTimerDisabled: Bool =  UIApplication.shared.isIdleTimerDisabled
            result(isIdleTimerDisabled)
        }
        else if ("keepOn" == call.method) {
            let b: Bool = (call.arguments as! Dictionary)["on", default: true]
            UIApplication.shared.isIdleTimerDisabled = b
            result(nil)
        }
        else {
            result(FlutterMethodNotImplemented)
        }
  }
}
