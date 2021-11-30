package com.example.parkingassist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.example.parkingassist.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * This class creates the UI for registration.
 **/
public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView headline, registerBtn;
    private EditText fullName, age, email, password;
    private FirebaseAuth mAuth;

    static boolean success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        headline = (TextView) findViewById(R.id.RegisterHeadline);
        headline.setOnClickListener(this);

        registerBtn = (Button) findViewById(R.id.registerUser);
        registerBtn.setOnClickListener(this);

        fullName = (EditText) findViewById(R.id.editTextRegisterName);
        age = (EditText) findViewById(R.id.editTextRegisterAge);
        email = (EditText) findViewById(R.id.editTextRegisterEmail);
        password = (EditText) findViewById(R.id.editTextRegisterPassword);
        success = false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RegisterHeadline:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    //checks if login info is input correctly and creates a user data via firebase email and password method//
    private void registerUser() {
        String emailReg = email.getText().toString().trim();
        String ageReg = age.getText().toString().trim();
        String fullNameReg = fullName.getText().toString().trim();
        String passwordReg = password.getText().toString().trim();

        if (fullNameReg.isEmpty()) {
            fullName.setError("Full name is required");
            fullName.requestFocus();
            return;
        }
        if (ageReg.isEmpty()) {
            age.setError("Age is required");
            age.requestFocus();
            return;
        }
        if (emailReg.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailReg).matches()) {
            email.setError("please provide valid email!");
            email.requestFocus();
            return;
        }
        if (passwordReg.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        if (passwordReg.length() < 6) {
            password.setError("Password is required to be min 6 characters!");
            password.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailReg, passwordReg)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullNameReg, ageReg, emailReg,10.0,false);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterUserActivity.this,
                                                "User has been registered successfully", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(RegisterUserActivity.this,
                                                "Please login to get a verification email", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterUserActivity.this, MainActivity.class));
                                    } else
                                        Toast.makeText(RegisterUserActivity.this,
                                                "Registration Failed, try again", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(RegisterUserActivity.this,
                                    "Registration Failed, try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }// End RegisterUser
}
