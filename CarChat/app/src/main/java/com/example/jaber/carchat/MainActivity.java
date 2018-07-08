package com.example.jaber.carchat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;
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
import Models.Users;

public class MainActivity extends AppCompatActivity {

    Button signinBtn;
    Button registerBtn;
    private Toolbar mToolbar;

    private FirebaseDatabase database;

    public static String loggedInUser = "";
    public static String trick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedInUser = GetUserName();

        database = FirebaseDatabase.getInstance();
        signinBtn = (Button) findViewById(R.id.signinBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);



        signinBtn.setOnClickListener((new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }

        }));

        registerBtn.setOnClickListener((new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }

        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            if (GetSharedPreferences() != false ) {
                if (loggedInUser == null) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                } else {
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    boolean GetSharedPreferences(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User,Pass;
        User = sharedPreferences.getString("User" , "");
        Pass = sharedPreferences.getString("Pass" , "");

        if(User != "" && Pass != "")
            return  true;
        return false;
    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }
}