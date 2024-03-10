package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth=FirebaseAuth.getInstance();
        current_user= auth.getCurrentUser();
        if (current_user != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserDataType user = snapshot.getValue(UserDataType.class);
                                if (user.getName() == null) {
                                    current_user.delete();
                                } else {
                                    startActivity(new Intent(Splash.this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                current_user.delete();
                                startActivity(new Intent(Splash.this, LogIN.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
        else {
            startActivity(new Intent(Splash.this, LogIN.class));
            finish();
        }
    }
}