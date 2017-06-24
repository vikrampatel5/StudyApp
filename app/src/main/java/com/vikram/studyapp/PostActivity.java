package com.vikram.studyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Vikram on 6/14/2017.
 */

public class PostActivity extends AppCompatActivity {

    StorageReference storageReference;
    ImageView imageView;
    ListView listView;
    private Toolbar toolbar;

    private final String post_title[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };

    private final String post_image[] = {
            "http://api.learn2crack.com/android/images/donut.png",
            "http://api.learn2crack.com/android/images/eclair.png",
            "http://api.learn2crack.com/android/images/froyo.png",
            "http://api.learn2crack.com/android/images/ginger.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "http://api.learn2crack.com/android/images/marshmallow.png"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

       toolbar = (Toolbar)findViewById(R.id.toolbar_post);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setTitle("Posts");
       initViews();

    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<PostData> myData = prepareData();
        DataAdapter adapter = new DataAdapter(myData,getApplicationContext());
        recyclerView.setAdapter(adapter);
        
    }

    private ArrayList prepareData() {
        ArrayList<PostData> myData = new ArrayList<>();
        for(int i=0;i<post_title.length;i++){
            PostData data = new PostData();
            data.setImage_title(post_title[i]);
            data.setImage_url(post_image[i]);
            myData.add(data);
        }
        return myData;
    }

}
