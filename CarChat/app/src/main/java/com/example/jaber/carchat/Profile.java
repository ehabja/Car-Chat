package com.example.jaber.carchat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseDatabase database;
    private DatabaseReference table_users;

    private CircleImageView profile_img;
    private TextView profile_name;
    private TextView profile_status;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar)findViewById(R.id.profile_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");

        Bundle extras = getIntent().getExtras();

        id= extras.getString("user_id");

        profile_img = (CircleImageView)findViewById(R.id.profile_img);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_status = (TextView)findViewById(R.id.profile_status);

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users").child(id);

        table_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String thumImg = dataSnapshot.child("thumbImg").getValue().toString();


                profile_name.setText(firstName + " " + lastName);
                profile_status.setText(status);

                if(!img.equals("default")){
                    Picasso.with(Profile.this).load(img).placeholder(R.drawable.default_avatar).into(profile_img);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
