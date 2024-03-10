package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Fragments.HomeFragment;
import com.assadcoorp.socialstar.Fragments.SignupEmail;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.signup_frame_layout_id, new SignupEmail());
        fragmentTransaction.commit();




        /*signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirm_password.getText().toString() .equals(password.getText().toString())&&!(email.getText().toString().isEmpty())&&
                !(profession.getText().toString().isEmpty())&&!(name.getText().toString().isEmpty())&& password.getText().toString().length()>7) {
                    warning.setVisibility(View.INVISIBLE);
                    auth_signup.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                warning.setVisibility(View.INVISIBLE);
                                UserDataType user=new UserDataType(name.getText().toString(),
                                        email.getText().toString(),password.getText().toString(),
                                        profession.getText().toString());
                                user.setFollowerCount(0);
                                user.setBio("Its my bio");
                                user.setFriendCount(0);
                                user.setPostCount(0);
                                String id=task.getResult().getUser().getUid();
                                user.setUserID(id);
                                database_signup.getReference().child("Users").child(id).setValue(user);
                                startActivity(new Intent(SignUp.this,MainActivity.class));
                                finish();
                            } else {
                                warning.setVisibility(View.VISIBLE);
                                String error= String.valueOf(task.getException());
                                warning.setText("Creating User Failed: "+error.substring(error.lastIndexOf(":")+1));
                            }
                        }
                    });
                } else {
                    warning.setVisibility(View.VISIBLE);
                    if (!(confirm_password.getText().toString().equals(password.getText().toString()))){
                         warning.setText("Password Dosen't Match");
                    }
                    else if(password.getText().toString().length()<8){
                        warning.setText("Password must be 8 letters");
                    }
                    else if(email.getText().toString().isEmpty()){
                        warning.setText("Please Enter Email");
                    }
                    else if(profession.getText().toString().isEmpty()){
                        warning.setText("Please Enter Profession");
                    }
                    else if(name.getText().toString().isEmpty()){
                        warning.setText("Please Enter Name");
                    }
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, LogIN.class));
            }
        });*/
    }

}