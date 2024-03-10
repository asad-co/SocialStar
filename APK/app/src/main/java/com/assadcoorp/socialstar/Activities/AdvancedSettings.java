package com.assadcoorp.socialstar.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AdvancedSettings extends AppCompatActivity {

    Button delete_account, back;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdvancedSettings.this, MainActivity.class);
        intent.putExtra("fragment", "profile");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);
        back = findViewById(R.id.advancedp_back_but_id);
        delete_account = findViewById(R.id.advancedp_delete_account_but_id);
        auth=FirebaseAuth.getInstance();

        dialog = new ProgressDialog(AdvancedSettings.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Account Deleting");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedSettings.this);




                // Set Alert Title
                builder.setMessage("Are you sure about deleting your account permanently? This action is" +
                        "irreversible. Please wait 15 seconds to delete your account.").setTitle("Delete Account?");



                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog1, which) -> {
                    //delete
                    dialog.show();
                    FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseAuth.getInstance().getCurrentUser().delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(AdvancedSettings.this,LogIN.class));
                                                    finish();
                                                }
                                            });

                                }
                            });


                });


                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog1, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog1.cancel();
                });


                //for disablling a positive responese
                //AlertDialog dialog = builder.create();



                // Create the Alert dialog
                AlertDialog alertDialog = builder.show();
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                // Show the Alert Dialog box
                //alertDialog.show();


                //timer and message
                new CountDownTimer(15000, 1000) {
                    @Override
                    public void onTick(long timer) {

                        button.setEnabled(false);

                    }
                    @Override
                    public void onFinish() {
                        //builder.setMessage("Your account is ready to delete!");
                        button.setEnabled(true);
                    }
                }.start();

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }


}