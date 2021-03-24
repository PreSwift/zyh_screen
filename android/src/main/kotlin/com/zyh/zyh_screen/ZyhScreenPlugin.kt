package com.zyh.zyh_screen

import android.app.Activity
import android.content.res.Resources
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.core.math.MathUtils
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
      // 取到了默认值
      val maxBrightness = getBrightnessMax();
      if (maxBrightness != 255) {
        try {
          val current = Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
          if (current <= maxBrightness) {
            result = current * 1f / maxBrightness
          }
        } catch (ignore: java.lang.Exception) {
        }
      } else {
        try {
          result = Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255.toFloat()
        } catch (e: SettingNotFoundException) {
          result = 1.0f
          e.printStackTrace()
        }
      }
    }
    if (result.isNaN() || result < 0) {
      // 如果没有取值成功，那么就默认设置为一半亮度，防止突然变得很亮或很暗
      result = 0.5f
    }
    return result
  }

  private fun changeBrightness(change: Float) {
    if (activity == null) {
      return
    }
    var old: Float = activity!!.window.attributes.screenBrightness
    val none = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE // -1.0f
    if (old == none) {
      // 取到了默认值
      try {
        val current = Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        if (current <= getBrightnessMax()) {
          old = current * 1f / getBrightnessMax()
        }
      } catch (ignore: java.lang.Exception) {
      }
    }
    if (old == none || old <= 0) {
      // 如果没有取值成功，那么就默认设置为一半亮度，防止突然变得很亮或很暗
      old = 0.5f
    }
    val newBrightness = MathUtils.clamp(old + change, 0.01f, 1f)
    val params: WindowManager.LayoutParams = activity!!.window.attributes
    params.screenBrightness = newBrightness
    activity!!.window.attributes = params
  }

  private fun getBrightnessMax(): Int {
    try {
      val system: Resources = Resources.getSystem()
      val resId: Int = system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android")
      if (resId != 0) {
        return system.getInteger(resId)
      }
    } catch (ignore: Exception) {
    }
    return 255
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
