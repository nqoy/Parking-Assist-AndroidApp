package com.example.parkingassist.activities;
//https://console.firebase.google.com/project/parking-assist-7b442/database/parking-assist-7b442-default-rtdb/data

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class enables login and register options and using authentication via firebase.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private EditText textEmail, textPassword;
    private Button signIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.btnLogin);
        signIn.setOnClickListener(this);

        textEmail = (EditText) findViewById(R.id.editTextEmail);

        textPassword = (EditText) findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
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
     *checks if login info is input correctly and provides authentication via
     * firebase email and password method
     */
    private void userLogin() {
        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        if (email.isEmpty()) {
            textEmail.setError("Email is required!");
            textEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textEmail.setError("Please enter a valid email!");
            textEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            textPassword.setError("Password is required!");
            textPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            textPassword.setError("Password is required to be min 6 characters!");
            textPassword.requestFocus();
            return;
        }
        //firebase email and password authentication method//
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, navigationActivity.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this,
                                "Please check email for Verification", Toast.LENGTH_LONG).show();
                        hideKeyboard();
                    }
                } else
                    Toast.makeText(MainActivity.this,
                            "Failed to login! please check your login info", Toast.LENGTH_LONG).show();
            }
        });
    }// End user login //

    /**
     * Hides keyboard to help see Toast massage.
     */
    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}