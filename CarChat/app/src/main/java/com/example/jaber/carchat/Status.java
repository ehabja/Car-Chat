package com.example.jaber.carchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Status extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout status_input;
    private Button status_save_Btn;

    private FirebaseDatabase database;
    private DatabaseReference table_users;

    public static String loggedInUser = "";

    private ProgressDialog mProgress;

    private String status_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Bundle extras = getIntent().getExtras();

        status_value= extras.getString("status_value");

        loggedInUser = GetUserName();

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users").child(loggedInUser);

        mToolbar = (Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status_input = (TextInputLayout)findViewById(R.id.status_input);
        status_save_Btn = (Button)findViewById(R.id.status_save_Btn);

        status_input.getEditText().setText(status_value);


        status_save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = status_input.getEditText().getText().toString();
                table_users.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(Status.this, Settings.class));

                    }
                });


//                mProgress = new ProgressDialog(Status.this);
//                mProgress.setTitle("Saving Changes");
//                mProgress.setMessage("Please wait while we save the changes");
//                mProgress.show();

                //Toast.makeText(Status.this, "hellloooo", Toast.LENGTH_LONG).show();

                //mProgress.dismiss();

//                String status = status_input.getEditText().getText().toString();
//                table_users.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            mProgress.dismiss();
//                            startActivity(new Intent(Status.this, Settings.class));
//                        }else {
//                            Toast.makeText(getApplicationContext(), "There was some error in saving changes", Toast.LENGTH_SHORT).show();
//                            mProgress.dismiss();
//                        }
//                    }
//                });
            }
        });
    }

    public String GetUserName(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("ConnectionDetails" , MODE_PRIVATE);
        String User;
        User = sharedPreferences.getString("User" , "");
        return User;
    }

    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

}
