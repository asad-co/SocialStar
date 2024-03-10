package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIN extends AppCompatActivity {
    Button login;
    TextView signup, alert, forgotp;
    FirebaseAuth auth;
    FirebaseUser current_user;
    TextInputEditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        signup = findViewById(R.id.login_signup_txt_id);
        login = findViewById(R.id.login_login_but_id);
        email = findViewById(R.id.login_email_edit_id);
        password = findViewById(R.id.login_password_edit_id);
        alert = findViewById(R.id.login_alert_txt_id);
        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser();
        forgotp = findViewById(R.id.login_forgotp_txt_id);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIN.this, SignUp.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().length() > 7 && !(email.getText().toString() == null) && !(password.getText().toString() == null) &&
                        email.getText().toString().length() > 0) {
                    alert.setVisibility(View.INVISIBLE);
                    auth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                alert.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LogIN.this, MainActivity.class));
                                finish();

                            } else {
                                alert.setVisibility(View.VISIBLE);
                                String error = String.valueOf(task.getException());
                                alert.setText("Login Failed: " + error.substring(error.lastIndexOf(":") + 1));
                            }
                        }
                    });
                } else {
                    alert.setVisibility(View.VISIBLE);
                    if (email.getText().toString() == null || email.getText().toString().length() == 0) {
                        alert.setText("Please Enter Email Address");
                    } else if (password.getText().toString() == null || password.getText().toString().length() == 0) {
                        alert.setText("Please Enter Password of 8 characters");
                    }
                }
            }
        });

        forgotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIN.this, ForgotPassword.class));
            }
        });
    }


}