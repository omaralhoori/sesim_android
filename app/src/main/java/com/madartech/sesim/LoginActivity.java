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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madartech.sesim.utils.Validation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mLoginBtn;
    //private Button mGoogleBtn;
    private TextView joinUs;
    private FirebaseAuth mAuth;

    private ProgressDialog dialog;
    private String TAG = "LOGIN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_btn);
        //mGoogleBtn = findViewById(R.id.login_google_btn);
        mLoginBtn.setOnClickListener(this);
        //mGoogleBtn.setOnClickListener(this);
        joinUs = findViewById(R.id.join_us);
        joinUs.setOnClickListener(this);
    }

    private void loginWithEmail(){
        if(!Validation.validateEmail(mEmailView.getText().toString())){
            Toast.makeText(LoginActivity.this, "Email is not correct.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        dialog.setMessage("Signing in...");
        dialog.show();
        mAuth.signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        dialog.dismiss();
        if(user != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                loginWithEmail();
            break;
//            case R.id.login_google_btn:
//
//            break;
            case R.id.join_us:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}