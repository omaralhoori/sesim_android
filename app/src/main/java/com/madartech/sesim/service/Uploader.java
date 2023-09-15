package com.madartech.sesim.service;

import android.app.ProgressDialog;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Uploader {
    private StorageReference mStorageRef;
    private String userUID;
    private ArrayList<String> uploads;

    public Uploader(String userUID ) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        this.userUID = userUID;
        uploads = new ArrayList<String>();
    }

    public void uploadAudio(String fileName, String filePath){
        uploads.add("audio");
        StorageReference filepath = mStorageRef.child("Audio").child(userUID).child(fileName);
        Uri uri = Uri.fromFile(new File(filePath));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progressDialog.dismiss();
                uploads.remove("audio");
                uploadFinish();
            }
        });
    }
    public void uploadImage(String fileName, Uri uri){//String filePath){
        uploads.add("image");
        StorageReference filepath = mStorageRef.child("Images").child(userUID).child(fileName);
        //Uri uri = Uri.fromFile(new File(filePath));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progressDialog.dismiss();
                uploads.remove("image");
                uploadFinish();
            }
        });
    }

    private void uploadFinish(){
        if(uploads.size() == 0){
            //progressDialog.dismiss();
        }
    }
}
