package com.madartech.sesim;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.madartech.sesim.utils.Permissions;
import com.madartech.sesim.utils.VoiceRecorder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity implements View.OnTouchListener {

    private Uri mImageUri;
    String miUrlOk = "";
    String audioUrlOk = "";
    boolean lock = false;
    boolean audioFile = false;
    private StorageTask uploadTask;
    StorageReference storageRef;
    StorageReference storageAudioRef;


    ImageView close, image_added;
    TextView post;
    TextView recordingSts;
    Button recordBtn;
    Button playBtn;
    VoiceRecorder recorder;
    FirebaseUser user;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user = FirebaseAuth.getInstance().getCurrentUser();
        recorder = new VoiceRecorder(this, user.getUid());
        pd = new ProgressDialog(this);
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        recordBtn = findViewById(R.id.record_btn);
        playBtn = findViewById(R.id.play_btn);
        recordingSts = findViewById(R.id.recording_status);
        storageRef = FirebaseStorage.getInstance().getReference("posts");
        storageAudioRef = FirebaseStorage.getInstance().getReference("audio");


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAction();
            }
        });

        recordBtn.setOnTouchListener(this);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.audioPlayer();
            }
        });
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage(){
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();
                        if(audioFile){
                            if(audioUrlOk.length() > 0 && !lock){
                                lock = true;
                                storePost();
                            }
                        }else{
                            storePost();
                        }
                    } else {
                        Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(PostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadAudio(String filePath){
        final StorageReference fileReference = storageAudioRef.child(System.currentTimeMillis()
                + "." + "3gp");
        Uri uri = Uri.fromFile(new File(filePath));
        uploadTask = fileReference.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    audioUrlOk = downloadUri.toString();
                    if(miUrlOk.length() > 0 && !lock){
                        lock = true;
                        storePost();
                    }
                }
            }
        });

    }

    private void storePost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        String postid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", postid);
        hashMap.put("postimage", miUrlOk);
        hashMap.put("postAudio", audioUrlOk);
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child(postid).setValue(hashMap);

        startActivity(new Intent(PostActivity.this, MainActivity.class));
        finish();
        pd.dismiss();
    }
    void postAction(){
        pd.setMessage("Posting");
        pd.show();
        String audioPath = recorder.getRecordedFileName();
        if(audioPath != null && audioPath.length() > 0){
            audioFile = true;
            uploadAudio(audioPath);
        }
        uploadImage();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            image_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            Permissions.getAudioRecordPermission(this);
        }else {
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("RECORDING_TAG", "initialize the recorder");
                    recorder.startRecording();
                    recordingSts.setText("Recording Start");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    recorder.stopRecording();
                    recordingSts.setText("Recording Stop");
                }
            }catch (Exception e){

            }
        }
        return false;
    }
}