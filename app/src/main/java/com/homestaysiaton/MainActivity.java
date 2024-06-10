package com.homestaysiaton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homestaysiaton.Dialogs.ProfileDialog;
import com.homestaysiaton.Fragments.CurrentBookingFragment;
import com.homestaysiaton.Fragments.ShopFragment;
import com.homestaysiaton.Objects.ShopItem;
import com.homestaysiaton.Objects.Unit;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    MaterialButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initializeViews();
        backstackListener();

        btnProfile.setOnClickListener(view -> {
            if (USER != null) {
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.show(getSupportFragmentManager(), "PROFILE_DIALOG");
            }
            else {
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) { // if navigation is at first backstack entry
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initializeViews() {
        btnProfile = findViewById(R.id.btnProfile);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment shopFragment = new ShopFragment();
        fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
        fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
        fragmentTransaction.commit();
    }

    private void backstackListener() {
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            ShopFragment shopFragment = (ShopFragment) getSupportFragmentManager().findFragmentByTag("SHOP_FRAGMENT");
            CartFragment cartFragment = (CartFragment) getSupportFragmentManager().findFragmentByTag("CART_FRAGMENT");
            OrdersFragment ordersFragment = (OrdersFragment) getSupportFragmentManager().findFragmentByTag("ORDERS_FRAGMENT");
            ShopItemFragment shopItemFragment = (ShopItemFragment) getSupportFragmentManager().findFragmentByTag("SHOP_ITEM_FRAGMENT");

            if (shopFragment != null && shopFragment.isVisible() ||
                    shopItemFragment != null && shopItemFragment.isVisible() ) {
                bottom_navbar.getMenu().getItem(0).setChecked(true);
                softKeyboardListener();
            }
            else if (cartFragment != null && cartFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(1).setChecked(true);
            }
            else if (ordersFragment != null && ordersFragment.isVisible()) {
                bottom_navbar.getMenu().getItem(2).setChecked(true);
            }
        });*/
    }

    private void softKeyboardListener() {
    }
}