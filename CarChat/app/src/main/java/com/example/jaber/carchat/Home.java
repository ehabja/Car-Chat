package com.example.jaber.carchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.Friends;
import Models.FriendsRequest;
import Models.SectionsPagerAdapter;
import Models.Users;

public class Home extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference table_users;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    public static String loggedInUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try{
            init();
        }
        catch (Exception ex){
            Toast.makeText(Home.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void init() {

        loggedInUser = GetUserName();

        mViewPager = (ViewPager)findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(1);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);

        mTabLayout.setupWithViewPager(mViewPager);

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Car Chat");

    }

    void DeleteSharedPreferences(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){
            DeleteSharedPreferences();
            loggedInUser = null;
            startActivity(new Intent(Home.this, MainActivity.class));
//            if(loggedInUser != null){
//                table_users.child(loggedInUser).child("online").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//
//                        }
//                    }
//                });
//            }

        }

        if(item.getItemId() == R.id.main_settings_btn){
            startActivity(new Intent(Home.this, Settings.class));
        }

        return true;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        table_users.child(loggedInUser).child("online").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//
//                }
//            }
//        });
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(loggedInUser != null) {
//
//            table_users.child(loggedInUser).child("online").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//
//                    }
//                }
//            });
//        }
//
//    }

    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }


}
