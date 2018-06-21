package com.danielthornhill.flutter.audiorecorder.pcm16khzaudiorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;


/**
 * Pcm16khzAudioRecorderPlugin
 */
public class Pcm16khzAudioRecorderPlugin implements MethodCallHandler {
  private final Registrar registrar;
  private boolean isRecording = false;
  private static final String LOG_TAG = "AudioRecorder";
  private MediaRecorder mRecorder = null;
  private static String mFilePath = null;
  private Date startTime = null;
  private String mExtension = "";

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "pcm16khz_audio_recorder");
    channel.setMethodCallHandler(new Pcm16khzAudioRecorderPlugin(registrar));
  }

  private Pcm16khzAudioRecorderPlugin(Registrar registrar) {
    this.registrar = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "start":
//                Log.d(LOG_TAG, "Start");
        String path = call.argument("path");
        mExtension = call.argument("extension");
        startTime = Calendar.getInstance().getTime();
        if (path != null) {
          mFilePath = path;
        } else {
          String fileName = String.valueOf(startTime.getTime());
          mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + mExtension;
        }
//                Log.d(LOG_TAG, mFilePath);
        startRecording();
        isRecording = true;
        result.success(null);
        break;
      case "stop":
//                Log.d(LOG_TAG, "Stop");
        stopRecording();
        long duration = Calendar.getInstance().getTime().getTime() - startTime.getTime();
//                Log.d(LOG_TAG, "Duration : " + String.valueOf(duration));
        isRecording = false;
        HashMap<String, Object> recordingResult = new HashMap<>();
        recordingResult.put("duration", duration);
        recordingResult.put("path", mFilePath);
        recordingResult.put("audioOutputFormat", mExtension);
        result.success(recordingResult);
        break;
      case "isRecording":
//                Log.d(LOG_TAG, "Get isRecording");
        result.success(isRecording);
        break;
      case "hasPermissions":
//                Log.d(LOG_TAG, "Get hasPermissions");
        Context context = registrar.context();
        PackageManager pm = context.getPackageManager();
        int hasStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName());
        int hasRecordPerm = pm.checkPermission(Manifest.permission.RECORD_AUDIO, context.getPackageName());
        boolean hasPermissions = hasStoragePerm == PackageManager.PERMISSION_GRANTED && hasRecordPerm == PackageManager.PERMISSION_GRANTED;
        result.success(hasPermissions);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void startRecording() {
    mRecorder = new MediaRecorder();
    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mRecorder.setOutputFormat(getOutputFormatFromString(mExtension));
    mRecorder.setOutputFile(mFilePath);
    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    try {
      mRecorder.prepare();
    } catch (IOException e) {
      Log.e(LOG_TAG, "prepare() failed");
    }

    mRecorder.start();
  }

  private void stopRecording() {
    if (mRecorder != null) {
      mRecorder.stop();
      mRecorder.reset();
      mRecorder.release();
      mRecorder = null;
    }
  }

  private int getOutputFormatFromString(String outputFormat) {
    switch (outputFormat) {
      case ".mp4":
      case ".aac":
      case ".m4a":
        return MediaRecorder.OutputFormat.MPEG_4;
      default:
        return MediaRecorder.OutputFormat.MPEG_4;
    }
  }
}

