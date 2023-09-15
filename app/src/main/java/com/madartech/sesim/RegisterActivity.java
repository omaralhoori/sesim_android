package com.madartech.sesim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.madartech.sesim.utils.Validation;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText nameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private Button registerBtn;
    //private Button googleBtn;
    private TextView loginNow;

    private ProgressDialog dialog;
    private String TAG = "REGISTER_TAG";
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        nameView = findViewById(R.id.register_name);
        emailView = findViewById(R.id.register_email);
        passwordView = findViewById(R.id.register_password);
        confirmPasswordView = findViewById(R.id.register_password_confirm);

        registerBtn = findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);
        loginNow = findViewById(R.id.login_now);
        loginNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                register();
                break;
//            case R.id.login_google_btn:
//                googleSignIn();
//                break;
            case R.id.login_now:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void register() {
        if(!Validation.validateEmail(emailView.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Email is not correct.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(!Validation.validatePassword(passwordView.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Password must be longer than 8 characters.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(!Validation.passwordsMatch(passwordView.getText().toString(), confirmPasswordView.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Passwords aren't match.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        dialog.setMessage("Signing up...");
        dialog.show();

        mAuth.createUserWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        dialog.dismiss();
        if(user != null){
            addUserToDB(user);
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void addUserToDB(FirebaseUser userAuth) {
//        // Save user to database by its UID
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> user= new HashMap<>();
//        user.put("name", nameView.getText().toString());
//        user.put("email", emailView.getText().toString());
//
//        // Add a new document with a generated ID
//        db.collection("users")
//                .document(userAuth.getUid()).set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + aVoid.toString());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                }
        String userID = userAuth.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", userID);
        map.put("email", emailView.getText().toString());
        map.put("name", nameView.getText().toString());
        map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/sesim-e8aca.appspot.com/o/user_profile.png?alt=media&token=9e367918-46f3-4e3e-b056-c99ae45f6266");
        map.put("bio", "");

        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "DocumentSnapshot added with ID: " + task.toString());
                }
            }
        });
    }

}