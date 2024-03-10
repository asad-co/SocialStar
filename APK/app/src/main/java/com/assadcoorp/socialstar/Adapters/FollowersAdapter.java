package com.assadcoorp.socialstar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.DataTypes.FollowerDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder>{
    ArrayList<FollowerDataType> list;
    Context context;

    public FollowersAdapter(ArrayList<FollowerDataType> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_followers,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowerDataType model= list.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getFollowedby()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user=snapshot.getValue(UserDataType.class);
                        Picasso.get().load(user.getProfilephoto()).placeholder(R.drawable.profile_icon)
                                .into(holder.profile);
                        holder.name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //visiting profile

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getFollowedby())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                        .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getFollowedby());
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

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.followers_profile_pic_img_id);
            name=itemView.findViewById(R.id.followers_username_txt_id);
        }
    }
}
