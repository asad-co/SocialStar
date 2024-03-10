package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.assadcoorp.socialstar.Fragments.AddFragment;
import com.assadcoorp.socialstar.Fragments.HomeFragment;
import com.assadcoorp.socialstar.Fragments.NotificationFragment;
import com.assadcoorp.socialstar.Fragments.ProfileFragment;
import com.assadcoorp.socialstar.Fragments.SearchFragment;
import com.assadcoorp.socialstar.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_view;
    FloatingActionButton fab;
    String fragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_view=findViewById(R.id.bottomNavigationView);
        bottom_view.setBackground(null);
        bottom_view.getMenu().getItem(2).setEnabled(false);
        fab=findViewById(R.id.fab_id);

        //setting default Fragment
        try{
            Intent intent=getIntent();
            fragment=intent.getStringExtra("fragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment==null){
        FragmentTransaction home_frag= getSupportFragmentManager().beginTransaction();
        home_frag.replace(R.id.frame_layout_id, new HomeFragment());
        home_frag.commit();
        bottom_view.getMenu().getItem(0).setChecked(true);
        }
        else if(fragment.equals("profile")){
            FragmentTransaction home_frag= getSupportFragmentManager().beginTransaction();
            home_frag.replace(R.id.frame_layout_id, new ProfileFragment());
            home_frag.commit();
            bottom_view.getMenu().getItem(4).setChecked(true);
        }


        bottom_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragment_Trans= getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    //home fragment
                    case R.id.home_id:
                        bottom_view.getMenu().getItem(0).setChecked(true);
                        fab.setBackgroundTintList(getApplicationContext().getResources().
                                getColorStateList(R.color.light_yellow));
                        fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red),
                                android.graphics.PorterDuff.Mode.SRC_IN);

                        fragment_Trans.replace(R.id.frame_layout_id, new HomeFragment());
                        break;
                    //notification fragment
                    case  R.id.notification_id:
                        bottom_view.getMenu().getItem(1).setChecked(true);
                        fab.setBackgroundTintList(getApplicationContext().getResources().
                                getColorStateList(R.color.light_yellow));
                        fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red),
                                android.graphics.PorterDuff.Mode.SRC_IN);

                        fragment_Trans.replace(R.id.frame_layout_id, new NotificationFragment());
                        break;

                    //search fragment
                    case  R.id.search_id:
                        bottom_view.getMenu().getItem(3).setChecked(true);
                        fab.setBackgroundTintList(getApplicationContext().getResources().
                                getColorStateList(R.color.light_yellow));
                        fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red),
                                android.graphics.PorterDuff.Mode.SRC_IN);

                        fragment_Trans.replace(R.id.frame_layout_id, new SearchFragment());
                        break;

                    //profile fragment
                    case  R.id.profile_id:
                        bottom_view.getMenu().getItem(4).setChecked(true);
                        fab.setBackgroundTintList(getApplicationContext().getResources().
                                getColorStateList(R.color.light_yellow));
                        fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red),
                                android.graphics.PorterDuff.Mode.SRC_IN);

                        fragment_Trans.replace(R.id.frame_layout_id, new ProfileFragment());
                        break;
                }
                fragment_Trans.commit();
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                FragmentTransaction fragment_Trans= getSupportFragmentManager().beginTransaction();
                bottom_view.getMenu().getItem(2).setChecked(true);
                fab.setBackgroundTintList(getApplicationContext().getResources().
                        getColorStateList(R.color.red));
                fab.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.light_yellow),
                        PorterDuff.Mode.SRC_IN);

                fragment_Trans.replace(R.id.frame_layout_id, new AddFragment());
                fragment_Trans.commit();
            }
        });


    }
}