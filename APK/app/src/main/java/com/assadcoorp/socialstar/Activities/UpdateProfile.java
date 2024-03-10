package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {
    TextInputEditText name,profession,status;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Button save,back;
    TextView warning;
    ProgressDialog dialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpdateProfile.this,MainActivity.class);
        intent.putExtra("fragment","profile");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        name=findViewById(R.id.updatep_name_edit_id);
        profession=findViewById(R.id.updatep_profession_edit_id);
        status=findViewById(R.id.updatep_status_edit_id);
        save=findViewById(R.id.updatep_save_but_id);
        back=findViewById(R.id.updatep_back_but_id);
        warning=findViewById(R.id.updatep_warning_txt_id);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(UpdateProfile.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Account Updating");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        warning.setVisibility(View.GONE);
        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user=snapshot.getValue(UserDataType.class);
                        name.setText(user.getName());
                        profession.setText(user.getProfession());
                        status.setText(user.getBio());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(profession.getText().toString().isEmpty())&&!(name.getText().toString().isEmpty()) &&(name.getText().length()<26)){
                    dialog.show();
                    database.getReference().child("Users").child(auth.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserDataType user=snapshot.getValue(UserDataType.class);
                                    user.setName(name.getText().toString());
                                    user.setBio(status.getText().toString());
                                    user.setProfession(profession.getText().toString());
                                    database.getReference().child("Users").child(auth.getUid()).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            saveProfile(view);
                                            Intent intent=new Intent(UpdateProfile.this, MainActivity.class);
                                            dialog.dismiss();
                                            Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                            intent.putExtra("fragment","profile");
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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


    }

    private void saveProfile(View view){
        SharedPreferences sharedPreferences =getSharedPreferences("Current User Details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", String.valueOf(name.getText()));
        editor.putString("Profession",String.valueOf(profession.getText()));
        editor.putString("Status",String.valueOf(status.getText()));
        editor.apply();
    }
}