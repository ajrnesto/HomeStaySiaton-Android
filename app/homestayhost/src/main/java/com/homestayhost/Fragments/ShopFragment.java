package com.homestayhost.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homestayhost.Adapters.ShopItemAdapter;
import com.homestayhost.AuthenticationActivity;
import com.homestayhost.Dialogs.ProfileDialog;
import com.homestayhost.Objects.ShopItem;
import com.homestayhost.R;
import com.homestayhost.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ShopFragment extends Fragment implements ShopItemAdapter.OnShopItemListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    Query qryShop;
    ListenerRegistration velShop;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    ConstraintLayout clEmpty;
    RecyclerView rvShop;

    ArrayList<ShopItem> arrShopItem;
    ShopItemAdapter shopItemAdapter;
    ShopItemAdapter.OnShopItemListener onShopItemListener = this;
    FloatingActionButton btnAddUnit;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shop, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadRecyclerView();

        return view;
    }

    private void initializeViews() {
        clEmpty = view.findViewById(R.id.clEmpty);
        rvShop = view.findViewById(R.id.rvShop);
        btnAddUnit = view.findViewById(R.id.btnAddUnit);

        Utils.Cache.setBoolean(requireContext(), "shop_item_dialog_is_visible", false);
    }

    private void loadRecyclerView() {
        arrShopItem = new ArrayList<>();
        rvShop = view.findViewById(R.id.rvShop);
        rvShop.setHasFixedSize(true);
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvShop.setLayoutManager(linearLayoutManager);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvShop.setLayoutManager(gridLayoutManager);


        qryShop = DB.collection("units")
                //.whereEqualTo("isBooked", false)
                .whereEqualTo("hostUid", AUTH.getUid());

        qryShop.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("DEBUG", "Listen failed.", error);
                    return;
                }

                arrShopItem.clear();

                for (QueryDocumentSnapshot doc : value) {
                    arrShopItem.add(doc.toObject(ShopItem.class));
                    shopItemAdapter.notifyDataSetChanged();
                }

                if (value.isEmpty()) {
                    clEmpty.setVisibility(View.VISIBLE);
                    rvShop.setVisibility(View.GONE);
                }
                else {
                    clEmpty.setVisibility(View.GONE);
                    rvShop.setVisibility(View.VISIBLE);
                }
            }
        });

        shopItemAdapter = new ShopItemAdapter(requireContext(), arrShopItem, onShopItemListener);
        rvShop.setAdapter(shopItemAdapter);
        shopItemAdapter.notifyDataSetChanged();
    }

    private void handleButtonClicks() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment newUnitFragment = new NewUnitFragment();

                Bundle args = new Bundle();
                args.putString("mode", "add");

                newUnitFragment.setArguments(args);
                fragmentTransaction.replace(R.id.fragmentHolder, newUnitFragment, "NEW_UNIT_FRAGMENT");
                fragmentTransaction.addToBackStack("NEW_UNIT_FRAGMENT");
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onShopItemClick(int position) {
        ShopItem shopItem = arrShopItem.get(position);

        Bundle args = new Bundle();
        args.putString("id", shopItem.getId());
        args.putString("hostUid", shopItem.getHostUid());
        args.putDouble("price", shopItem.getPrice());
        args.putDouble("maxGuests", shopItem.getMaxGuests());
        args.putDouble("locationLatitude", shopItem.getLocation().getLatitude());
        args.putDouble("locationLongitude", shopItem.getLocation().getLongitude());
        args.putString("addressLine", shopItem.getAddressLine());
        args.putString("unitDetails", shopItem.getUnitDetails());
        args.putString("unitName", shopItem.getUnitName());
        if (shopItem.getThumbnail() != null) {
            args.putLong("thumbnail", shopItem.getThumbnail());
        }
        args.putString("mode", "edit");

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment newUnitFragment = new NewUnitFragment();

        newUnitFragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragmentHolder, newUnitFragment, "NEW_UNIT_FRAGMENT");
        fragmentTransaction.addToBackStack("NEW_UNIT_FRAGMENT");
        fragmentTransaction.commit();
    }
}