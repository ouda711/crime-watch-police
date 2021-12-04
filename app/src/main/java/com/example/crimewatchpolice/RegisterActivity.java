package com.example.crimewatchpolice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    Button callLogin;
    TextInputLayout regUsername, regEmail,regPhone, regFullName, regPassword;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String uniqueID = UUID.randomUUID().toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        callLogin = findViewById(R.id.call_login_screen);
        regFullName = findViewById(R.id.regFullName);
        regUsername = findViewById(R.id.regUsername);
        regEmail = findViewById(R.id.regEmail);
        regPhone = findViewById(R.id.regPhone);
        regPassword = findViewById(R.id.regPassword);

        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private Boolean validateName(){
        String val = regFullName.getEditText().getText().toString();

        if(val.isEmpty()){
            regFullName.setError("Field cannot be empty");
            return false;
        }else{
            regFullName.setError(null);
            return true;
        }
    }

    private Boolean validateUsername(){
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpaces = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            regUsername.setError("Field cannot be empty");
            return false;
        }else if(val.length()>=20){
            regUsername.setError("Username too long");
            return false;
        }else if(!val.matches(noWhiteSpaces)){
            regUsername.setError(null);
            return false;
        }else{
            regUsername.setError(null);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty()){
            regEmail.setError("Field cannot be empty");
            return false;
        }else if(!val.matches(emailPattern)){
            regEmail.setError("Invalid email address");
            return false;
        }
        else{
            regEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePhone(){
        String val = regPhone.getEditText().getText().toString();

        if(val.isEmpty()){
            regPhone.setError("Field cannot be empty");
            return false;
        }else{
            regPhone.setError(null);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = regPassword.getEditText().getText().toString();

        if(val.isEmpty()){
            regPassword.setError("Field cannot be empty");
            return false;
        }else{
            regPassword.setError(null);
            return true;
        }
    }

    public void registerUser(View view) {

        if(!validateName() || !validateUsername() || !validateEmail() || !validatePassword() || !validatePassword()){
            return;
        }

        String fname = regFullName.getEditText().getText().toString();
        String user = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String phone = regPhone.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("police");
        UserHelper userHelper = new UserHelper(fname,user,email,phone,password);
        reference.child(uniqueID).setValue(userHelper);
    }

    public void updateUser(View view) {

    }
}
