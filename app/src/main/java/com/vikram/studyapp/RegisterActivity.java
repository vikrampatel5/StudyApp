package com.vikram.studyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vikram on 6/9/2017.
 */
public class RegisterActivity extends AppCompatActivity{

    private TextView login_here;
    private Button register_button;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private AppCompatSpinner spinner;
    private ProgressDialog progressDialog;

    private TextInputLayout editTextUsername;
    private TextInputLayout editTextEmail;
    private TextInputLayout editTextPassword;
   // private  classChoosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize Views
        editTextUsername  = (TextInputLayout) findViewById(R.id.name);
        editTextEmail = (TextInputLayout) findViewById(R.id.email);
        editTextPassword = (TextInputLayout) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);

        spinner = (AppCompatSpinner)findViewById(R.id.class_spinner);
        String[] classes = getResources().getStringArray(R.array.ClassMCA);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item, classes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

       if(user!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        login_here = (TextView)findViewById(R.id.login_here);
        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register_button = (Button)findViewById(R.id.button_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser(){



        StringBuilder errorMessage = new StringBuilder("Please Enter ");
        final String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();
        final String name = editTextUsername.getEditText().getText().toString().trim();

        if (name.contains(" ")) {
            editTextUsername.getEditText().setError("No Spaces Allowed");
            Toast.makeText(RegisterActivity.this, "No Spaces Allowed", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(name)) {

            if (TextUtils.isEmpty(name)) {
                errorMessage.append("Name, ");
            }
            if (TextUtils.isEmpty(email)) {
                errorMessage.append("Email Address, ");
            }
            if (TextUtils.isEmpty(password)) {
               errorMessage.append("Password ");
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Registering Please Wait......");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DatabaseReference userRef = myRef.child("Users");
                    Map<String,User> users = new HashMap<>();
                    users.put(name,new User(name,email,"MCA II"));
                    String test[] = email.split("@");
                    userRef.child(test[0]).setValue(users);

                    finish();
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }
                else {
                        Toast.makeText(RegisterActivity.this, "Registration Error!!", Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
            }
        });
    }
}
