package com.madartech.sesim.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madartech.sesim.service.Uploader;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VoiceRecorder {
    private MediaRecorder recorder;
    private Uploader uploader;

    private String TAG = "RECORDING_TAG";
    private String userUID;
    private String fileName;
    private String filePath;
    private File DIR;//Environment.getExternalStorageDirectory().getAbsolutePath();
    private Context context;
    public VoiceRecorder(Context context, String userUID){
        this.userUID = userUID;
        this.context = context;
        DIR = new File(context.getFilesDir(), "Recording");
        if(!DIR.exists()){
            DIR.mkdir();
        }
        uploader = new Uploader(userUID);
    }


    public void startRecording() {
        Toast.makeText(context, "startRecording", Toast.LENGTH_SHORT).show();
        fileName = String.valueOf(new Date().getTime()) + ".3gp";
        filePath = DIR.getAbsolutePath() + "/" + fileName;
                recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            Log.e(TAG, "prepared successfully");
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }
    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void audioPlayer(){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void audioUrlPlayer(String audioUrl){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(audioUrl);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getRecordedFileName(){
        return this.filePath;
    }
    public void uploadAudio() {
        if(fileName != null)
        {
            uploader.uploadAudio(fileName,filePath);
        }
        else{
            Toast.makeText(context, "Unable to upload.", Toast.LENGTH_SHORT).show();
        }
    }
}
