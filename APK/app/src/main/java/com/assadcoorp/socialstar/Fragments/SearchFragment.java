package com.assadcoorp.socialstar.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.SearchAdapter;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    ArrayList<UserDataType> user_list=new ArrayList<>();
    FirebaseAuth search_auth;
    FirebaseDatabase search_database;
    ShimmerRecyclerView search_rv;
    EditText search_keys;
    ImageView search_img;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search_auth=FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        search_database=FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        search_rv=view.findViewById(R.id.search_rv_id);
        search_keys=view.findViewById(R.id.search_searchfield_edit_id);
        search_img=view.findViewById(R.id.search_searchfield_img_id);

        search_rv.setHasFixedSize(true);
        SearchAdapter search_adapter=new SearchAdapter(getContext(),user_list);
        LinearLayoutManager search_linearlayoutmanager = new LinearLayoutManager(getContext());
        search_rv.setLayoutManager(search_linearlayoutmanager);
        search_rv.setAdapter(search_adapter);
        //getAllUsers();

        search_keys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!search_keys.getText().equals(null)) {
                    searchusers(String.valueOf(search_keys.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(search_keys.getText().length()>0){
                    //perform searching
                    searchusers(String.valueOf(search_keys.getText()));
                }
            }
        });
        return view;
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserDataType modelUsers = dataSnapshot1.getValue(UserDataType.class);
                    if (modelUsers.getUserID() != null && !modelUsers.getUserID().equals(firebaseUser.getUid())) {
                        user_list.add(modelUsers);
                    }
                    SearchAdapter adapterUsers = new SearchAdapter(getActivity(), user_list);
                    search_rv.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void searchusers(final String entered_string) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserDataType modelUsers = dataSnapshot1.getValue(UserDataType.class);
                    if (modelUsers.getUserID() != null && !modelUsers.getUserID().equals(firebaseUser.getUid())) {
                        if (modelUsers.getName().toLowerCase().contains(entered_string.toLowerCase()) ||
                                modelUsers.getEmail().toLowerCase().contains(entered_string.toLowerCase())) {
                            user_list.add(modelUsers);
                        }
                    }
                    SearchAdapter search_adapter = new SearchAdapter(getActivity(), user_list);
                    search_adapter.notifyDataSetChanged();
                    search_rv.setAdapter(search_adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}