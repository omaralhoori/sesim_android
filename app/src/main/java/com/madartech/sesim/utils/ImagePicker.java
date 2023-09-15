package com.madartech.sesim.utils;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.madartech.sesim.HomeActivity;
import com.madartech.sesim.service.Uploader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ImagePicker {
    private String TAG = "PICKING_TAG";
    private String userUID;
    private String fileName;
    //private String filePath;
    private Uri uri;
    private AppCompatActivity activity;
    private Uploader uploader;

    public ImagePicker(AppCompatActivity activity, String userUID, ProgressDialog progressDialog){
        this.userUID = userUID;
        this.activity = activity;

        uploader = new Uploader(userUID);
    }


    public void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        this.activity.startActivityForResult(Intent.createChooser(intent,"select images"), 0);
    }

    public void setPickingResult(@Nullable Intent data, ImageView imageView){
        ArrayList<Uri> uploadImgs = new ArrayList<>();
        if(data.getData()!=null){
            Uri mImageUri=data.getData();
            this.uri = mImageUri;
            uploadImgs.add(mImageUri);
            imageView.setImageURI(mImageUri);
        } else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    this.uri = uri;
                    //filePath = uri.getPath();
                    imageView.setImageURI(uri);
                    uploadImgs.add(uri);
                }
            }
        }
    }
    public void uploadImage(){
        fileName = String.valueOf(new Date().getTime()) + ".jpg";
        uploader.uploadImage(fileName,uri);
    }
}
