package com.zyh.zyh_screen

import android.app.Activity
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.WindowManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ZyhScreenPlugin */
class ZyhScreenPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  var activity: Activity? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "github.com/PreSwift/zyh_screen")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "brightness") {
      result.success(getBrightness())
    } else if (call.method == "setBrightness") {
      val brightness = call.argument<Double>("brightness")!!
      val layoutParams: WindowManager.LayoutParams? = activity?.window?.attributes
      if (layoutParams != null) {
        layoutParams.screenBrightness = brightness.toFloat()
      }
      activity?.window?.attributes = layoutParams
      result.success(null)
    } else if (call.method == "isKeptOn") {
      val flags: Int? = activity?.window?.attributes?.flags
      if (flags != null) {
        result.success(flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0)
      }
    } else if (call.method == "keepOn") {
      val on = call.argument<Boolean>("on")
      if (on!!) {
        println("Keeping screen on ")
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      } else {
        println("Not keeping screen on")
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      }
      result.success(null)
    } else {
      result.notImplemented()
    }
  }

  private fun getBrightness(): Float {
    if (activity == null) {
      return 1.0f
    }
    var result: Float = activity!!.window.attributes.screenBrightness
    if (result < 0) { // the application is using the system brightness
      try {
        result = Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255.toFloat()
      } catch (e: SettingNotFoundException) {
        result = 1.0f
        e.printStackTrace()
      }
    }
    return result
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    this.activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    this.activity = null
  }
}
