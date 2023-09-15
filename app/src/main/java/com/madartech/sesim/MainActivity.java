package com.madartech.sesim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private Button loginBtn;
    private Button registerBtn;
    //private Button googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.register_btn);
        //googleBtn = findViewById(R.id.login_google_btn);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.login_btn:
                intent = new Intent(MainActivity.this, LoginActivity.class);
            break;
            case R.id.register_btn:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
            break;
//            case R.id.login_google_btn:
//                intent = new Intent(MainActivity.this, LoginActivity.class);
//            break;
        }
        if(intent != null) {
            startActivity(intent);
        }
    }
}