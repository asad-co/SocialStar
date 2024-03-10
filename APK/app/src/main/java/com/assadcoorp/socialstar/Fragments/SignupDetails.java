package com.assadcoorp.socialstar.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.assadcoorp.socialstar.Activities.MainActivity;
import com.assadcoorp.socialstar.Activities.SignUp;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class SignupDetails extends Fragment {
    TextInputEditText  name, profession;
    TextView info,warning;
    FirebaseDatabase database_signup;
    FirebaseAuth auth_signup;
    Button signup;
    String email,password,status="Its my status";

    public SignupDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database_signup=FirebaseDatabase.getInstance();
        auth_signup=FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signup_details, container, false);
        Bundle bundle=this.getArguments();
        email=bundle.getString("email");
        password= bundle.getString("password");

        signup=view.findViewById(R.id.signup_signup_btn_id);
        name=view.findViewById(R.id.signup_name_edit_id);
        profession=view.findViewById(R.id.signup_profession_edit_id);
        info=view.findViewById(R.id.signup_details_info_txt_id);
        warning=view.findViewById(R.id.signup_details_warning_txt_id);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(profession.getText().toString().isEmpty())&&!(name.getText().toString().isEmpty())
                &&(name.getText().length()<26)){

                    warning.setVisibility(View.INVISIBLE);

                    auth_signup.signInWithEmailAndPassword(email.toString().trim(),password.toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        warning.setVisibility(View.INVISIBLE);
                                        UserDataType user=new UserDataType(name.getText().toString(),
                                                email, profession.getText().toString());
                                        user.setFollowerCount(0);
                                        user.setBio(status);
                                        user.setFriendCount(0);
                                        user.setPostCount(0);
                                        String id=task.getResult().getUser().getUid();
                                        user.setUserID(id);
                                        database_signup.getReference().child("Users").child(id).setValue(user);
                                        saveProfile(view);
                                        startActivity(new Intent(getContext(),MainActivity.class));
                                        getActivity().finish();
                                    } else {
                                        warning.setVisibility(View.VISIBLE);
                                        String error= String.valueOf(task.getException());
                                        warning.setText("Creating User Failed: "+error.substring(error.lastIndexOf(":")+1));
                                    }
                                }
                            });



                }
                else{
                    warning.setVisibility(View.VISIBLE);
                    if(profession.getText().toString().isEmpty()){
                        warning.setText("Please Enter Profession");
                    }
                    else if(name.getText().toString().isEmpty()){
                        warning.setText("Please Enter Name");
                    }
                    else if (name.getText().length()>25){
                        warning.setText("Please dont exceed name length limit");
                    }
                }
            }
        });


        return  view;
    }
    private void saveProfile(View view){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Current User Details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", String.valueOf(name.getText()));
        editor.putString("Profession",String.valueOf(profession.getText()));
        editor.putString("Status",String.valueOf(status));
        editor.apply();
    }
}