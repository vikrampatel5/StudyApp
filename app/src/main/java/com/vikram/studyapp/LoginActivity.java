package com.vikram.studyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Vikram on 6/9/2017.
 */

public class LoginActivity extends AppCompatActivity{
    private TextView register;
    private Button loginButton;
    private SignInButton googleSignIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    private TextInputLayout editTextEmail;
    private TextInputLayout editTextPassword;

    //Google Sign In

    private static final int SIGN_IN_REQUEST_CODE = 1;
    //private static final int RC_SIGN_IN = 9003;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView)findViewById(R.id.register);
        loginButton = (Button)findViewById(R.id.button_login);
        googleSignIn = (SignInButton)findViewById(R.id.googleSignIn);
        editTextEmail = (TextInputLayout)findViewById(R.id.login_email);
        editTextPassword = (TextInputLayout)findViewById(R.id.login_password);

        progressDialog = new ProgressDialog(this);

        //SharedPreference



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();




        if(user!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .build(),
                SIGN_IN_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
                finish();
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        }

    private void loginActivity() {
        StringBuilder errorMessage = new StringBuilder("Please Enter ");
        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {

            if (TextUtils.isEmpty(email)) {
                errorMessage.append("Email Address, ");
            }
            if (TextUtils.isEmpty(password)) {
                errorMessage.append("Password ");
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Signing in Please Wait......");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Signing in Error!!", Toast.LENGTH_SHORT);
                }
                progressDialog.dismiss();
            }
        });
    }
}
