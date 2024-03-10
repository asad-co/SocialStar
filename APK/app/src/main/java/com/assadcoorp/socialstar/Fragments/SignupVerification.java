package com.assadcoorp.socialstar.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import com.assadcoorp.socialstar.Activities.MainActivity;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignupVerification extends Fragment {

    String email,password;
    ProgressBar pb;
    FirebaseAuth auth;
    TextView info;
    Button next,back;
    public SignupVerification() {
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
        View view= inflater.inflate(R.layout.fragment_signup_verification, container, false);
        Bundle bundle=this.getArguments();
        email=bundle.getString("email");
        password= bundle.getString("password");
        info=view.findViewById(R.id.signup_veri_info_txt_id);
        pb=view.findViewById(R.id.signup_veri_progressbar_pb_id);
        next=view.findViewById(R.id.signup_veri_next_but_id);
        back=view.findViewById(R.id.signup_veri_back_but_id);

        next.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        info.setText("Sending Verification link to your email");
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //sending link
                            FirebaseUser user=auth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //on successfully sending email
                                    pb.setVisibility(View.GONE);
                                    info.setText("Email has been sent!\n Please verify your account and then click next.\n\n Not seeing email? Make sure to check spam folder as well.");
                                    next.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //on failure
                                    pb.setVisibility(View.GONE);
                                    next.setVisibility(View.GONE);
                                    String error= String.valueOf(e);
                                    info.setText("Email unable to sent:"+error.substring(error.lastIndexOf(":")+1));
                                }
                            });
                        }
                        else{
                            pb.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            String error= String.valueOf(task.getException());
                            info.setText("Creating User Failed: "+error.substring(error.lastIndexOf(":")+1));

                        }
                    }
                });

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user=auth.getCurrentUser();
                        if(user.isEmailVerified()){
                            pb.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            bundle.putString("password",password);


                            SignupDetails signupDetails = new SignupDetails();

                            signupDetails.setArguments(bundle);

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.signup_frame_layout_id, signupDetails);
                            fragmentTransaction.commit();
                        }
                        else{
                            pb.setVisibility(View.GONE);

                            info.setTextColor(Color.parseColor("#FF0000"));
                            info.setText("First Verify your account from link in email and then click next.\n\n Not seeing email? Make sure to check spam folder as well.");


                        }
                    }
                });

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }
                SignupEmail signupEmail = new SignupEmail();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.signup_frame_layout_id, signupEmail);
                fragmentTransaction.commit();
            }
        });




        return view;
    }
}