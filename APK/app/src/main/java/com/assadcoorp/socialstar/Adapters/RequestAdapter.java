package com.assadcoorp.socialstar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.DataTypes.RequestDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.viewHolder>{
    Context context;
    ArrayList<RequestDataType> list;

    public RequestAdapter(ArrayList<RequestDataType> list,Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_request, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        RequestDataType model=list.get(position);
        String time_ago = TimeAgo.using(model.getSentAt());
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getSentbyID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user=snapshot.getValue(UserDataType.class);
                        holder.time.setText(time_ago);
                        Picasso.get().load(user.getProfilephoto())
                                .placeholder(R.drawable.profile_icon)
                                .into(holder.profile);
                        holder.main.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" sent you friend request"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType current=snapshot.getValue(UserDataType.class);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("friends")
                                        .child(model.getSentbyID())
                                        .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Users")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("friendCount")
                                                        .setValue(current.getFriendCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Users").child(model.getSentbyID())
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                UserDataType current=snapshot.getValue(UserDataType.class);
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Users")
                                                                                        .child(model.getSentbyID())
                                                                                        .child("friends")
                                                                                        .child(FirebaseAuth.getInstance().getUid())
                                                                                        .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                        .child("Users")
                                                                                                        .child(model.getSentbyID())
                                                                                                        .child("friendCount")
                                                                                                        .setValue(current.getFriendCount()+1)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void unused) {
                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                        .child("Requests").child(model.getSentbyID())
                                                                                                                        .child(FirebaseAuth.getInstance().getUid())
                                                                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                                if(snapshot.exists())
                                                                                                                                {
                                                                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                                                                            .child("Requests").child(model.getSentbyID())
                                                                                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                                                                                            .removeValue();
                                                                                                                                }

                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                            }
                                                                                                                        });
                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                        .child("Requests").child(FirebaseAuth.getInstance().getUid())
                                                                                                                        .child(model.getSentbyID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void unused) {
                                                                                                                                Toast.makeText(context, "Friend Added", Toast.LENGTH_SHORT).show();
                                                                                                                            }
                                                                                                                        });

                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
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

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Requests").child(FirebaseAuth.getInstance().getUid())
                        .child(model.getSentbyID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        //visiting profile
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getSentbyID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getSentbyID());
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
                        .child(model.getSentbyID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getSentbyID());
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
        Button confirm,delete;
        TextView main,time;
        ImageView profile;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            confirm=itemView.findViewById(R.id.request_addfriend_btn_id);
            delete=itemView.findViewById(R.id.request_rejection_btn_id);
            main=itemView.findViewById(R.id.request_message_txt_id);
            time=itemView.findViewById(R.id.request_timelapsed_txt_id);
            profile=itemView.findViewById(R.id.request_profile_pic_img_id);
        }
    }
}
