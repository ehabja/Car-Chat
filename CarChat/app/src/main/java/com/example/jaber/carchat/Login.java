package com.example.jaber.carchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import Models.Users;

public class Login extends AppCompatActivity {

    private EditText input_username, input_pass;
    private TextInputLayout layout_username, layout_pass;
    private Button btn_signin;
    private TextView txtRegister;
    private FirebaseDatabase database;
    private DatabaseReference table_users;
    private Toolbar lToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtRegister = (TextView)findViewById(R.id.txtRegister);

        layout_username = (TextInputLayout) findViewById(R.id.layout_username);
        layout_pass = (TextInputLayout) findViewById(R.id.layout_pass);

        input_username = (EditText) findViewById(R.id.input_username);
        input_pass = (EditText) findViewById(R.id.input_pass);

        btn_signin = (Button) findViewById(R.id.btn_signin);

        lToolbar = (Toolbar) findViewById(R.id.login_toolbar);

        input_username.addTextChangedListener(new MyTextWatcher(input_username));
        input_pass.addTextChangedListener(new MyTextWatcher(input_pass));

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users");

        setSupportActionBar(lToolbar);
        getSupportActionBar().setTitle("Login");


        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                table_users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(input_username.getText().toString()).exists() && validateEmptyUsername()){
                            layout_username.setErrorEnabled(false);
                            Users users = dataSnapshot.child(input_username.getText().toString()).getValue(Users.class);
                            try{
                                if(users.getPassword().equals(input_pass.getText().toString())){
                                    layout_pass.setErrorEnabled(false);
                                    CreateSharedPreferences();
                                    Intent mainIntent = new Intent(Login.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                }
                                else{
                                    layout_pass.setError(getString(R.string.err_msg_faild));
                                    requestFocus(input_pass);
                                }
                            }catch(Exception ex){
                                Toast.makeText(Login.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            layout_username.setError(getString(R.string.err_msg_carNumber));
                            requestFocus(input_username);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this , MainActivity.class));
            }
        });
    }

//    void submitForm(){
//        if (!validateEmptyPassword() && !validateEmptyUsername()) {
//            return;
//        }
//        if (!validateEmptyPassword()) {
//            return;
//        }
//        if (!validatePassword()) {
//            return;
//        }
//        if (!validateCarNumber()) {
//            return;
//        }
//        if (!validateEmptyUsername()) {
//            return;
//        }
//
//        //CreateSharedPreferences();
//        startActivity(new Intent(Login.this , Register.class));
//    }

    private boolean validateEmptyUsername() {
        if (input_username.getText().toString().trim().isEmpty()) {
            layout_username.setError(getString(R.string.err_msg_emptyusername));
            requestFocus(input_username);
            return false;
        } else {
            layout_username.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmptyPassword() {
        if (input_pass.getText().toString().trim().isEmpty()) {
            layout_pass.setError(getString(R.string.err_msg_password));
            requestFocus(input_pass);
            return false;
        } else {
            layout_pass.setErrorEnabled(false);
        }
        return true;
    }

//    private boolean validateCarNumber() {
//        final int[] flag = {0};
//        try{
//            table_users.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.child(input_username.getText().toString()).exists()){
//                        layout_username.setError(getString(R.string.err_msg_carNumber));
//                        requestFocus(input_username);
//                    }else{
//                        layout_username.setErrorEnabled(false);
//                        flag[0] = 1;
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }catch (Exception ex){
//            Toast.makeText(Login.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return flag[0] == 1;
//    }
//
//    private boolean validatePassword() {
//        final int[] flag = {0};
//        try{
//            table_users.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.child(input_username.getText().toString()).exists()){
//                        Toast.makeText(Login.this, "no", Toast.LENGTH_SHORT).show();
//                        layout_pass.setError(getString(R.string.err_msg_faild));
//                        requestFocus(input_pass);
//                    }
//                    else{
//                        if(!dataSnapshot.child(input_username.getText().toString()).child("Password").getValue().toString().equals(input_pass.getText().toString())){
//                            Toast.makeText(Login.this, "no", Toast.LENGTH_SHORT).show();
//                            layout_pass.setError(getString(R.string.err_msg_faild));
//                            requestFocus(input_pass);
//                        }else{
//                            Toast.makeText(Login.this, "yeah", Toast.LENGTH_SHORT).show();
//                            layout_pass.setErrorEnabled(false);
//                            flag[0] = 1;
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }catch (Exception ex){
//            Toast.makeText(Login.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return flag[0] ==1;
//    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_username:
                    validateEmptyUsername();
                    break;
                case R.id.input_pass:
                    validateEmptyPassword();
                    break;
            }
        }
    }

    void CreateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ConnectionDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("User", input_username.getText().toString());
        editor.putString("Pass", input_pass.getText().toString());

        editor.commit();
    }

    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
