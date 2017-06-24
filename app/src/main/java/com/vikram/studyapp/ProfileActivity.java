package com.vikram.studyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Vikram on 6/11/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewName;
    private TextView textViewEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

          textViewName = (TextView)findViewById(R.id.nameProf);
          textViewEmail = (TextView)findViewById(R.id.emailProf);
          toolbar = (Toolbar)findViewById(R.id.toolbarProf);
          setSupportActionBar(toolbar);
          getSupportActionBar().setDisplayShowTitleEnabled(false);
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myDatabase = FirebaseDatabase.getInstance();
        myRef = myDatabase.getReference();
        final DatabaseReference ref = myRef.child("Users");

        String test[] = user.getEmail().split("@");

        ref.child(test[0]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //textViewName.setText(ref.getKey());
                        if(dataSnapshot.exists()) {
                            for(DataSnapshot data : dataSnapshot.getChildren()) {
                                User user = data.getValue(User.class);
                                textViewEmail.setText(user.getEmail());
                                textViewName.setText(user.getName());
                            }
                        }
                    }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

