package com.madartech.sesim.utils;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

public class Permissions {
    public static int IMAGE_READ_CODE = 0;
    public static int AUDIO_READ_CODE = 1;
    public static int IMAGE_TAKE_CODE = 2;
    public static int AUDIO_RECORD_CODE = 3;

    public static void getImageReadPermission(AppCompatActivity activity){
        activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_READ_CODE);
    }
    public static void getAudioReadPermission(AppCompatActivity activity){
        activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},AUDIO_READ_CODE);
    }
    public static void getImageTakePermission(AppCompatActivity activity){
        activity.requestPermissions(new String[] {Manifest.permission.CAMERA},IMAGE_TAKE_CODE);
    }
    public static void getAudioRecordPermission(AppCompatActivity activity){
        activity.requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO},AUDIO_RECORD_CODE);
    }
}
