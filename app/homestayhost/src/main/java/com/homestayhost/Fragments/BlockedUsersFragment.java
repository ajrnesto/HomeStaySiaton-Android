package com.homestayhost.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homestayhost.Adapters.BlockedUsersAdapter;
import com.homestayhost.Adapters.ShopItemAdapter;
import com.homestayhost.Objects.ShopItem;
import com.homestayhost.Objects.User;
import com.homestayhost.R;
import com.homestayhost.Utils.Utils;

import java.util.ArrayList;

public class BlockedUsersFragment extends Fragment implements BlockedUsersAdapter.OnBlockedUsersListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    Query qryBlockedUsers;
    ListenerRegistration velShop;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    TextView tvEmpty;
    RecyclerView rvBlockedUsers;

    ArrayList<User> arrBlockedUsers;
    BlockedUsersAdapter blockedUsersAdapter;
    BlockedUsersAdapter.OnBlockedUsersListener onBlockedUsersListener = this;
    FloatingActionButton btnAddUnit;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blocked_users, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadRecyclerView();

        return view;
    }

    private void initializeViews() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvBlockedUsers = view.findViewById(R.id.rvBlockedUsers);
        btnAddUnit = view.findViewById(R.id.btnAddUnit);
    }

    private void loadRecyclerView() {
        arrBlockedUsers = new ArrayList<>();
        rvBlockedUsers = view.findViewById(R.id.rvBlockedUsers);
        rvBlockedUsers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvBlockedUsers.setLayoutManager(linearLayoutManager);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvBlockedUsers.setLayoutManager(linearLayoutManager);


        qryBlockedUsers = DB.collection("users")
                .whereArrayContains("blockedBy", AUTH.getUid());

        qryBlockedUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("DEBUG", "Listen failed.", error);
                    return;
                }

                arrBlockedUsers.clear();

                for (QueryDocumentSnapshot doc : value) {
                    arrBlockedUsers.add(doc.toObject(User.class));
                    blockedUsersAdapter.notifyDataSetChanged();
                }

                if (value.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvBlockedUsers.setVisibility(View.GONE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvBlockedUsers.setVisibility(View.VISIBLE);
                }
            }
        });

        blockedUsersAdapter = new BlockedUsersAdapter(requireContext(), arrBlockedUsers, onBlockedUsersListener);
        rvBlockedUsers.setAdapter(blockedUsersAdapter);
        blockedUsersAdapter.notifyDataSetChanged();
    }

    private void handleButtonClicks() {
    }

    @Override
    public void onBlockedUsersClick(int position) {
        User blockedUser = arrBlockedUsers.get(position);
        DB.collection("users").document(blockedUser.getUid())
                .update("blockedBy", FieldValue.arrayRemove(AUTH.getUid()));
    }
}