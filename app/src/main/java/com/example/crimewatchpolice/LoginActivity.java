package com.example.crimewatchpolice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    String uniqueID = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(R.id.sign_up_screen_call);
        image = findViewById(R.id.logoImage);
        login_btn = findViewById(R.id.login);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);



        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                Pair[] pairs = new Pair[7];
//
//                pairs[0] = new Pair<View, String>(image, "logo_image");
//                pairs[1] = new Pair<View, String>(logoText, "logo_text");
//                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
//                pairs[3] = new Pair<View, String>(username, "username_tran");
//                pairs[4] = new Pair<View, String>(password, "password_tran");
//                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
//                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");
//
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
//                startActivity(intent, options.toBundle());
                startActivity(intent);

            }
        });
    }

    private Boolean validateUsername(){
        String val = username.getEditText().getText().toString();
        String noWhiteSpaces = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = password.getEditText().getText().toString();

        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    public void loginUSer(View view) {
        if(!validateUsername() || !validatePassword()){
            return;
        }
        else{
            isUser();
        }
    }

    private void isUser() {
        String userEnteredUsername = username.getEditText().getText().toString();
        String userEnteredPassword = password.getEditText().getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("police");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    username.setError(null);
                    username.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(), MainMapActivity.class);
                    intent.putExtra("username", userEnteredUsername);

                    startActivity(intent);

                    String passwordFromDb = snapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    System.out.println("Password from database:"+snapshot.child("username"));
//                    if(passwordFromDb.equals(userEnteredPassword)){
//                        String nameFromDb = snapshot.child(userEnteredPassword).child("fullName").getValue(String.class);
//                        String usernameFromDb = snapshot.child(userEnteredPassword).child("username").getValue(String.class);
//                        String emailFromDb = snapshot.child(userEnteredPassword).child("email").getValue(String.class);
//                        String phoneFromDb = snapshot.child(userEnteredPassword).child("phone").getValue(String.class);
//
//                        Intent intent = new Intent(getApplicationContext(), MainMapActivity.class);
//                        intent.putExtra("fullName", nameFromDb);
//                        intent.putExtra("username", usernameFromDb);
//                        intent.putExtra("email", emailFromDb);
//                        intent.putExtra("phone", phoneFromDb);
//
//                        startActivity(intent);
//                    }else{
//                        password.setError("Wrong Password");
//                        password.requestFocus();
//                    }
//                    Intent intent = new Intent(getApplicationContext(), MainMapActivity.class);
//                    startActivity(intent);
                }
                else{
                    username.setError("No such user exist");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}