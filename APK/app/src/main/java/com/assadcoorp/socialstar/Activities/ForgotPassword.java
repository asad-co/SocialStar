package com.assadcoorp.socialstar.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.assadcoorp.socialstar.Fragments.ForgotPasswordEmail;
import com.assadcoorp.socialstar.Fragments.SignupEmail;
import com.assadcoorp.socialstar.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.forgot_password_frame_layout_id, new ForgotPasswordEmail());
        fragmentTransaction.commit();
    }
}