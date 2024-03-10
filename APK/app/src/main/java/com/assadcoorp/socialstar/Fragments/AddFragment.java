package com.assadcoorp.socialstar.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assadcoorp.socialstar.DataTypes.PostDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class AddFragment extends Fragment {
    EditText post_descrip;
    Button post;
    ImageView add_photo, post_image, profile_pic;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextView name, profession;
    ProgressDialog dialog;
    private static boolean added_photo = false;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        post_descrip = view.findViewById(R.id.add_postdescrip_edit_id);
        post = view.findViewById(R.id.add_post_btn_id);
        add_photo = view.findViewById(R.id.add_addphoto_img_id);
        post_image = view.findViewById(R.id.add_postimage_img_id);
        profile_pic = view.findViewById(R.id.add_profile_pic_img_id);
        name = view.findViewById(R.id.add_user_name_txt_id);
        profession = view.findViewById(R.id.add_user_profession_txt_id);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            UserDataType user = snapshot.getValue(UserDataType.class);
                            Picasso.get().load(user.getProfilephoto())
                                    .placeholder(R.drawable.profile_icon)
                                    .into(profile_pic);
                            name.setText(user.getName());
                            profession.setText(user.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });
        post_descrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!post_descrip.getText().toString().isEmpty()) {
                    post.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_button));
                    post.setTextColor(getContext().getResources().getColor(R.color.white));
                    post.setEnabled(true);
                } else {
                    post.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.disable_button));
                    post.setTextColor(getContext().getResources().getColor(R.color.grey_1));
                    post.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                if (added_photo) {
                    final StorageReference reference = storage.getReference().child("posts").child(FirebaseAuth.getInstance().getUid())
                            .child(new Date().getTime() + "");
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    PostDataType post = new PostDataType();
                                    post.setPostimage(uri.toString());
                                    post.setPostedbyID(FirebaseAuth.getInstance().getUid());
                                    post.setPostdescription(post_descrip.getText().toString());
                                    post.setPostedat(new Date().getTime());
                                    post.setPostlike(0);
                                    post.setPostdislike(0);
                                    post.setComment(0);
                                    post.setShare(0);
                                    String folder_name=String.valueOf(new Date().getTime())+" "+name.getText().toString();
                                    post.setPostID(folder_name);
                                    database.getReference().child("Posts")
                                            .child(folder_name).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                            .child("posts").child(folder_name).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getReference().child("Users")
                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            UserDataType user=snapshot.getValue(UserDataType.class);
                                                                                            database.getReference().child("Users")
                                                                                                    .child(FirebaseAuth.getInstance().getUid())
                                                                                                            .child("postCount").setValue(user.getPostCount()+1);

                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                                                                                            post_descrip.setText("");
                                                                                            added_photo = false;
                                                                                            post_image.setVisibility(View.GONE);
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });

                                                                }
                                                            });
                                                }
                                            });

                                }
                            });
                        }
                    });
                } else {
                    PostDataType post = new PostDataType();
                    post.setPostimage(null);
                    post.setPostedbyID(FirebaseAuth.getInstance().getUid());
                    post.setPostdescription(post_descrip.getText().toString());
                    post.setPostedat(new Date().getTime());
                    post.setPostlike(0);
                    post.setPostdislike(0);
                    post.setComment(0);
                    post.setShare(0);
                    String folder_name=String.valueOf(new Date().getTime())+" "+name.getText().toString();
                    post.setPostID(folder_name);
                    database.getReference().child("Posts")
                            .child(folder_name).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                            .child("posts").child(folder_name).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    UserDataType user=snapshot.getValue(UserDataType.class);
                                                                    database.getReference().child("Users")
                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                            .child("postCount").setValue(user.getPostCount()+1);
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                                                                    post_descrip.setText("");
                                                                    added_photo = false;
                                                                    post_image.setVisibility(View.GONE);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });


                                                }
                                            });
                                }
                            });


                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            uri = data.getData();
            post_image.setImageURI(uri);
            post_image.setVisibility(View.VISIBLE);
            post.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_button));
            post.setTextColor(getContext().getResources().getColor(R.color.white));
            post.setEnabled(true);
            added_photo = true;

        }
    }
}