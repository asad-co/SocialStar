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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assadcoorp.socialstar.Activities.LogIN;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordVerification extends Fragment {
    String email;
    Button next,back;
    FirebaseAuth auth;
    TextView info;
    ProgressBar pb;
    public ForgotPasswordVerification() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_fotgot_password_verification, container, false);
        next=view.findViewById(R.id.forgotp_veri_next_but_id);
        back=view.findViewById(R.id.forgotp_veri_back_but_id);
        info=view.findViewById(R.id.forgotp_veri_info_txt_id);
        pb=view.findViewById(R.id.forgotp_veri_progressbar_pb_id);

        pb.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        info.setText("Sending Reset link to your email");

        Bundle bundle=this.getArguments();
        email=bundle.getString("reset_email");


        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pb.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                info.setText("Email has been sent!\n Please Enter Your new Password there.\n\n Not seeing email? Make sure to check spam folder as well.");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                String error= String.valueOf(e);
                info.setText("Email unable to sent:"+error.substring(error.lastIndexOf(":")+1));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LogIN.class));
                getActivity().finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordEmail forgotPasswordEmail = new ForgotPasswordEmail();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.forgot_password_frame_layout_id, forgotPasswordEmail);
                fragmentTransaction.commit();
            }
        });
       return view;
    }
}