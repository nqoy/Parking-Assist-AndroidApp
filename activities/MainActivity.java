package com.example.parkingassist.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingassist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

/**
 * This class enables login and register options uses authentication and register via firebase.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText m_TextEmail, m_TextPassword;
    private FirebaseAuth m_FirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView registerBtn = findViewById(R.id.btnRegister);
        Button signInBtn = findViewById(R.id.btnLogin);

        registerBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
        m_TextEmail =  findViewById(R.id.editTextEmail);
        m_TextPassword =  findViewById(R.id.editTextPassword);
        m_FirebaseAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;

            case R.id.btnLogin:
                userLogin();
                break;
        }
    }

    /**
     * checks if login info is input correctly and provides authentication via
     * firebase email and password method
     */
    private void userLogin() {
        String email = m_TextEmail.getText().toString().trim();
        String password = m_TextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            m_TextEmail.setError("Email is required!");
            m_TextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            m_TextEmail.setError("Please enter a valid email!");
            m_TextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            m_TextPassword.setError("Password is required!");
            m_TextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            m_TextPassword.setError("Password is required to be min 6 characters!");
            m_TextPassword.requestFocus();
            return;
        }
        //firebase email and password authentication method//
        m_FirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Task -> {
            if (Task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                assert user != null;
                if (user.isEmailVerified()) {
                    Toast.makeText(MainActivity.this,
                            "Login successful, please wait", Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(new Intent(MainActivity.this, navigationActivity.class));
                } else {
                    user.sendEmailVerification();
                    Toast.makeText(MainActivity.this,
                            "Please check your email for Verification link", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this,
                        "Login failed! please check your login info", Toast.LENGTH_LONG).show();
            }
            MainActivity.this.hideKeyboard();
        });
    }// End user login //

    /**
     * Hides keyboard to help see Toast massage.
     */
    public void hideKeyboard() {   // Checks if no view has focus
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}