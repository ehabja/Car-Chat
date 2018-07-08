package com.example.jaber.carchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private Toolbar mToolbar;


    private FirebaseDatabase database;
    private DatabaseReference table_users;



    public static String loggedInUser = "";

    private CircleImageView settings_img;
    private TextView settings_name;
    private TextView settings_status;
    private Button settings_status_Btn;
    private Button settings_img_Btn;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar)findViewById(R.id.settings_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings_img = (CircleImageView)findViewById(R.id.settings_img);
        settings_name = (TextView) findViewById(R.id.settings_name);
        settings_status = (TextView)findViewById(R.id.settings_status);
        settings_img_Btn = (Button)findViewById(R.id.settings_img_Btn);
        settings_status_Btn = (Button)findViewById(R.id.settings_status_Btn);

        loggedInUser = GetUserName();

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users").child(loggedInUser);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        table_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String thumImg = dataSnapshot.child("thumbImg").getValue().toString();

                settings_name.setText(firstName + " " + lastName);
                settings_status.setText(status);

                if(!img.equals("default")){
                    Picasso.with(Settings.this).load(img).placeholder(R.drawable.default_avatar).into(settings_img);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settings_status_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = settings_status.getText().toString();
                Intent status_intent = new Intent(Settings.this, Status.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });

        settings_img_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(Settings.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(Settings.this);
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                StorageReference filepath = mImageStorage.child("profile_images").child(loggedInUser + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String download_url = task.getResult().getDownloadUrl().toString();

                            table_users.child("img").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mProgressDialog.dismiss();
                                    //startActivity(new Intent(Settings.this, Settings.class));
                                }

                            });



                        }else{
                            Toast.makeText(Settings.this, "Error in uploading", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
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
