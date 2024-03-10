package com.assadcoorp.socialstar.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.assadcoorp.socialstar.Activities.LogIN;
import com.assadcoorp.socialstar.R;
import com.google.android.material.textfield.TextInputEditText;


public class ForgotPasswordEmail extends Fragment {

    Button next,back;
    TextInputEditText email;
    TextView warning;

    public ForgotPasswordEmail() {
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
        View view= inflater.inflate(R.layout.fragment_forgot_password_email, container, false);
        email=view.findViewById(R.id.forgotp_email_edit_id);
        next=view.findViewById(R.id.forgotp_email_next_but_id);
        back=view.findViewById(R.id.forgotp_email_back_but_id);
        warning=view.findViewById(R.id.forgotp_email_alert_txt_id);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(email.getText().toString().isEmpty())) {
                    warning.setVisibility(View.INVISIBLE);
                    String user_email = email.getText().toString().trim();

                    Bundle bundle = new Bundle();
                    bundle.putString("reset_email", user_email);

                    ForgotPasswordVerification forgotPasswordVerification = new ForgotPasswordVerification();

                    forgotPasswordVerification.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.forgot_password_frame_layout_id, forgotPasswordVerification);
                    fragmentTransaction.commit();

                } else {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("Please Enter Email");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LogIN.class));
                getActivity().finish();
            }
        });

        return view;
    }
}