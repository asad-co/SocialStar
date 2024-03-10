package com.assadcoorp.socialstar.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.assadcoorp.socialstar.Activities.LogIN;
import com.assadcoorp.socialstar.Activities.MainActivity;
import com.assadcoorp.socialstar.Activities.SignUp;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;


public class SignupEmail extends Fragment {

    TextInputEditText email,password, confirm_password;
    Button next;
    TextView login,warning;
    String user_email,user_password;

    public SignupEmail() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signup_email, container, false);
        login = view.findViewById(R.id.signup_login_text_id);
        email = view.findViewById(R.id.signup_email_edit_id);
        next=view.findViewById(R.id.signup_next_but_id);
        password = view.findViewById(R.id.signup_password_edit_id);
        confirm_password = view.findViewById(R.id.signup_confirmpassword_edit_id);
        warning = view.findViewById(R.id.signup_match_alert_txt_id);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sending data to other fragment


                if (confirm_password.getText().toString() .equals(password.getText().toString())&&!(email.getText().toString().isEmpty())&&
                         password.getText().toString().length()>7) {
                    warning.setVisibility(View.INVISIBLE);
                    user_email=email.getText().toString().trim();
                    user_password=password.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", user_email);
                    bundle.putString("password",user_password);


                    SignupVerification signupVerification = new SignupVerification();

                    signupVerification.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.signup_frame_layout_id, signupVerification);
                    fragmentTransaction.commit();

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

                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LogIN.class));
                getActivity().finish();
            }
        });


        return view;
    }

}