package com.vikram.studyapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Vikram on 6/9/2017.
 */

public class HomeActivity extends AppCompatActivity{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button choose;
    private Button upload;
    protected File directory;
    private Uri filePath;
    private Uri downloadUrl;
    private TextView chooseFileText;
    private ImageView imageView;

    private static final int FILE_SELECT_CODE= 0;
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        choose = (Button)findViewById(R.id.buttonChoose);
        upload = (Button)findViewById(R.id.buttonUpload);
        imageView = (ImageView)findViewById(R.id.imageViewH);
        chooseFileText = (TextView)findViewById(R.id.chooseFileText);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();

        if(user==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        else {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photo = user.getPhotoUrl();
            String uid = user.getUid();


            editor.putString("Email",user.getEmail());
            editor.putString("Name",user.getDisplayName());
            editor.commit();
        }

        //For Navigation Drawer

        initNavigation();

        //For File Upload
        fileChooser();
        fileUpload();


    }

    private void fileUpload() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath!=null){
                    final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setTitle("Uploading..");
                    progressDialog.show();

                    StorageReference picRef = mStorageRef.child("Files/"+ user.getUid()+"/" + new File(filePath.getPath()).getName());
                    picRef.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "File Uploaded!!", Toast.LENGTH_SHORT).show();
                                    //Storing Image Details
                                    Upload upload = new Upload("",taskSnapshot.getDownloadUrl().toString());
                                    //adding an upload to firebase database
                                    String uploadId = mDatabase.push().getKey();
                                    mDatabase.child(uploadId).setValue(upload);
                                 }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%...");

                                   // downloadUrl = taskSnapshot.getDownloadUrl();
                                }
                            });
                }
                else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertDialogBuilder.setTitle("Upload Error").setMessage("Please Select A File to Upload").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }
        });
    }

    private void fileChooser() {
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                String[] mimetypes = {"image/*", "application/pdf", "application/mspowerpoint","application/msword","text/plain","application/excel"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent,"Select File to Upload"),FILE_SELECT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILE_SELECT_CODE && resultCode == RESULT_OK && data != null && data.getData()!=null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            chooseFileText.setText(filePath.getPath());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }

    // Navigation View
    private void initNavigation() {

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.home:
                        break;
                    case R.id.user_logout:
                        logout();
                        break;
                    case R.id.myAccount:
                        editAccount();
                        break;
                    case R.id.posts:
                        posts();
                        break;
                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText(user.getEmail());

        drawerLayout.addDrawerListener(mToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToggle.syncState();
    }

    private void posts() {
        startActivity(new Intent(HomeActivity.this,PostActivity.class));
    }

    private void editAccount() {
        startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
    }

    //get the File Extention of the file

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}

