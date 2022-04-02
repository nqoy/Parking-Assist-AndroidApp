package com.example.parkingassist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * This class creates the UI for registration.
 **/
public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText m_FullName, m_Age, m_Email, m_Password;
    private FirebaseAuth m_Auth;
    //static boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        TextView headline = findViewById(R.id.RegisterHeadline);
        Button registerBtn = findViewById(R.id.registerUser);

        m_Auth = FirebaseAuth.getInstance();
        m_FullName = findViewById(R.id.editTextRegisterName);
        m_Age = findViewById(R.id.editTextRegisterAge);
        m_Email = findViewById(R.id.editTextRegisterEmail);
        m_Password = findViewById(R.id.editTextRegisterPassword);
        //isSuccess = false;
        headline.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
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

    //checks login info and creates a user data via firebase email and password method//
    private void registerUser() {
        String emailRegTxt = m_Email.getText().toString().trim();
        String ageRegTxt = m_Age.getText().toString().trim();
        String fullNameRegTxt = m_FullName.getText().toString().trim();
        String passwordRegTxt = m_Password.getText().toString().trim();

        if (fullNameRegTxt.isEmpty()) {
            m_FullName.setError("Full name is required");
            m_FullName.requestFocus();
            return;
        }
        if (ageRegTxt.isEmpty()) {
            m_Age.setError("Age is required");
            m_Age.requestFocus();
            return;
        }
        if (emailRegTxt.isEmpty()) {
            m_Email.setError("Email is required");
            m_Email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailRegTxt).matches()) {
            m_Email.setError("please provide valid email!");
            m_Email.requestFocus();
            return;
        }
        if (passwordRegTxt.isEmpty()) {
            m_Password.setError("Password is required");
            m_Password.requestFocus();
            return;
        }
        if (passwordRegTxt.length() < 6) {
            m_Password.setError("Password is required to be 6 characters min!");
            m_Password.requestFocus();
            return;
        }
        m_Auth.createUserWithEmailAndPassword(emailRegTxt, passwordRegTxt)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterUserActivity.this,
                                "Registration Failed, try again", Toast.LENGTH_LONG).show();
                        return;
                    }
                    User newUser = new User(
                            fullNameRegTxt, ageRegTxt, emailRegTxt, 10.0, false);

                    FirebaseDatabase.getInstance().getReference("Users").child(
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()
                            ).getUid()).setValue(newUser).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(RegisterUserActivity.this,
                                    "User has been registered successfully",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegisterUserActivity.this,
                                    "Please login to get a verification email",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(
                                    RegisterUserActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(RegisterUserActivity.this,
                                    "Registration Failed, try again", Toast.LENGTH_LONG).show();
                        }
                    });
                });
    }
}
