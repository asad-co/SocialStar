package com.assadcoorp.socialstar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.DataTypes.FollowerDataType;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.RequestDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {
    Context context;
    ArrayList<UserDataType> list;

    public SearchAdapter(Context context, ArrayList<UserDataType> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_search, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserDataType model = list.get(position);
        Picasso.get().load(model.getProfilephoto()).placeholder(R.drawable.profile_icon).into(holder.profile);
        holder.name.setText(model.getName());
        holder.profession.setText(model.getProfession());
        if (FirebaseAuth.getInstance().getUid().equals(model.getUserID())){
            holder.addfriend.setVisibility(View.GONE);
            holder.follow.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(model.getUserID())
                .child("friends")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.addfriend.setImageResource(R.drawable.added_icon);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Requests")
                                    .child(model.getUserID())
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("NewApi")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                holder.addfriend.setImageResource(R.drawable.add_friend_icon);
                                                holder.addfriend.setColorFilter(ContextCompat.getColor(context, R.color.white),
                                                        PorterDuff.Mode.SRC_IN);
                                                holder.addfriend.setBackgroundTintList(context.getResources().
                                                        getColorStateList(R.color.grey));
                                            } else {
                                                holder.addfriend.setImageResource(R.drawable.add_friend_icon);
                                                holder.addfriend.setBackgroundTintList(context.getResources().
                                                        getColorStateList(R.color.light_blue));
                                                holder.addfriend.setColorFilter(ContextCompat.getColor(context, R.color.white),
                                                        PorterDuff.Mode.SRC_IN);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getUserID())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.follow.setColorFilter(ContextCompat.getColor(context, R.color.white),
                                    PorterDuff.Mode.SRC_IN);
                            holder.follow.setBackgroundTintList(context.getResources().
                                    getColorStateList(R.color.grey));
                        } else {
                            holder.follow.setBackgroundTintList(context.getResources().
                                    getColorStateList(R.color.light_blue));
                            holder.follow.setColorFilter(ContextCompat.getColor(context, R.color.white),
                                    PorterDuff.Mode.SRC_IN);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getUserID())
                        .child("followers")
                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(model.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(model.getUserID())
                                                            .child("followerCount")
                                                            .setValue(model.getFollowerCount() - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "UnFollowed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                } else {
                                    FollowerDataType follow = new FollowerDataType();
                                    follow.setFollowedby(FirebaseAuth.getInstance().getUid());
                                    follow.setTime(new Date().getTime());
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(model.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (model.getFollowerCount() > 0) {

                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Users")
                                                                .child(model.getUserID())
                                                                .child("followerCount")
                                                                .setValue(model.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @SuppressLint("NewApi")
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                                                                        NotificationDataType notification = new NotificationDataType();
                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                        notification.setNotificationAt(new Date().getTime());
                                                                        notification.setType("follow");
                                                                        notification.setCheckOpen(false);

                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Notification").child(model.getUserID())
                                                                                .push().setValue(notification);


                                                                    }
                                                                });
                                                    } else {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Users")
                                                                .child(model.getUserID())
                                                                .child("followerCount")
                                                                .setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @SuppressLint("NewApi")
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        holder.addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(model.getUserID())
                        .child("friends")
                        .child(FirebaseAuth.getInstance().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            alertunfriend(model);
                                        }
                                        else{
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Requests")
                                                    .child(model.getUserID()).child(FirebaseAuth.getInstance().getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Requests")
                                                                        .child(model.getUserID()).child(FirebaseAuth.getInstance().getUid())
                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(context, "Request Unsent", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                            else {
                                                                RequestDataType request = new RequestDataType();
                                                                request.setSentAt(new Date().getTime());
                                                                request.setSentbyID(FirebaseAuth.getInstance().getUid());
                                                                request.setSenttoID(model.getUserID());

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Requests")
                                                                        .child(model.getUserID()).child(FirebaseAuth.getInstance().getUid())
                                                                        .setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


            }
        });

        //visiting profile
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getUserID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getUserID());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getUserID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getUserID());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile, addfriend, follow;
        TextView name, profession;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.search_profile_pic_img_id);
            name = itemView.findViewById(R.id.search_user_name_txt_id);
            profession = itemView.findViewById(R.id.search_user_profession_txt_id);
            follow = itemView.findViewById(R.id.search_follow_img_id);
            addfriend = itemView.findViewById(R.id.search_addfriend_img_id);
        }
    }

    private void alertunfriend(UserDataType model){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to unfirend this user?");

        // Set Alert Title
        builder.setTitle("Unfriend");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will perform action
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(model.getUserID())
                    .child("friends")
                    .child(FirebaseAuth.getInstance().getUid())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("friends")
                                    .child(model.getUserID()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Unfriend", Toast.LENGTH_SHORT).show();

                                            NotificationDataType notification = new NotificationDataType();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setType("unfriend");
                                            notification.setCheckOpen(false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Notification").child(model.getUserID())
                                                    .push().setValue(notification);
                                        }
                                    });


                        }
                    });
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}
