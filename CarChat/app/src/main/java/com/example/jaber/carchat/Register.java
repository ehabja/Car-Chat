package com.example.jaber.carchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Models.Users;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputCarNumber, inputPassword;
    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutCarNumber, inputLayoutPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private FirebaseDatabase database;
    private DatabaseReference table_users;
    private ProgressDialog mRegProgress;
    private Toolbar rToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        txtLogin = (TextView)findViewById(R.id.txtLogin);

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_firstName);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_lastName);
        inputLayoutCarNumber = (TextInputLayout) findViewById(R.id.input_layout_carNumber);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputFirstName = (EditText) findViewById(R.id.input_firstName);
        inputLastName = (EditText) findViewById(R.id.input_lastName);
        inputCarNumber = (EditText) findViewById(R.id.input_carNumber);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        rToolbar = (Toolbar) findViewById(R.id.register_toolbar);

        inputFirstName.addTextChangedListener(new MyTextWatcher(inputFirstName));
        inputLastName.addTextChangedListener(new MyTextWatcher(inputLastName));
        inputCarNumber.addTextChangedListener(new MyTextWatcher(inputCarNumber));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("Users");

        setSupportActionBar(rToolbar);
        getSupportActionBar().setTitle("Create Account");

        mRegProgress = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this , MainActivity.class));
            }
        });
    }

    private void submitForm() {
        if (!validateEmptyFirstName()) {
            return;
        }
        if (!validateEmptyLastName()) {
            return;
        }
        if (!validateFirstName()) {
            return;
        }
        if (!validateLastName()) {
            return;
        }
        if (!validteCarNumber()) {
            return;
        }
//        if (!validateUser()) {
//            return;
//        }
        if (!validatePassword()) {
            return;
        }


        RegisterNow();
    }

    void RegisterNow(){
        table_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(inputCarNumber.getText().toString()).exists()){
                    inputLayoutCarNumber.setError(getString(R.string.err_msg_inUse));
                    requestFocus(inputCarNumber);

                }else{

                    mRegProgress.setTitle("Regisering User");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    Users user = new Users(inputFirstName.getText().toString(), inputLastName.getText().toString(), inputPassword.getText().toString(), "default", "Hi there, Im using car chat", "default", "false", deviceToken);
                    table_users.child(inputCarNumber.getText().toString()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                inputLayoutCarNumber.setErrorEnabled(false);
                                Intent mainIntent = new Intent(Register.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }else{
                                mRegProgress.hide();
                                Toast.makeText(Register.this, "Error Registering", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validateFirstName() {
        for(int i = 0; i < inputFirstName.getText().length(); i++){
            if(inputFirstName.getText().charAt(i) < 'A' || inputFirstName.getText().charAt(i) > 'Z' && inputFirstName.getText().charAt(i) < 'a' || inputFirstName.getText().charAt(i) > 'z')
            {
                inputLayoutFirstName.setError(getString(R.string.err_msg_firstName));
                requestFocus(inputFirstName);
                return false;
            }else{
                inputLayoutFirstName.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateLastName() {
        for(int i = 0; i < inputLastName.getText().length(); i++){
            if(inputLastName.getText().charAt(i) < 'A' || inputLastName.getText().charAt(i) > 'Z' && inputLastName.getText().charAt(i) < 'a' || inputLastName.getText().charAt(i) > 'z')
            {
                inputLayoutLastName.setError(getString(R.string.err_msg_lastName));
                requestFocus(inputLastName);
                return false;
            }else{
                inputLayoutLastName.setErrorEnabled(false);
            }
        }
        return true;
    }


    private boolean validateEmptyFirstName() {
        if (inputFirstName.getText().toString().trim().isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.err_msg_emptyFirstName));
            requestFocus(inputFirstName);
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmptyLastName() {
        if (inputLastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_emptyLastName));
            requestFocus(inputLastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validteCarNumber() {
        if (inputCarNumber.getText().length() != 8 && inputCarNumber.getText().length() != 7){
            inputLayoutCarNumber.setError(getString(R.string.err_msg_carNumber));
            requestFocus(inputCarNumber);
            return false;
        } else {
            inputLayoutCarNumber.setErrorEnabled(false);
        }

        return true;
    }

//    boolean isValidUser;
//
//    private boolean validateUser() {
//
//        table_users.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(inputCarNumber.getText().toString()).exists()){
//                    inputLayoutCarNumber.setError(getString(R.string.err_msg_inUse));
//                    requestFocus(inputCarNumber);
//                    isValidUser = false;
//                }else{
//                    inputLayoutCarNumber.setErrorEnabled(false);
//                    isValidUser = true;
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        return isValidUser;
//     }

        private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

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
                case R.id.input_firstName:
                    validateFirstName();
                    break;
                case R.id.input_lastName:
                    validateLastName();
                    break;
                case R.id.input_carNumber:
                    validteCarNumber();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
