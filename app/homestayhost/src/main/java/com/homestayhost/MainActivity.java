package com.homestayhost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.homestayhost.Dialogs.ProfileDialog;
import com.homestayhost.Fragments.BlockedUsersFragment;
import com.homestayhost.Fragments.BookingsFragment;
import com.homestayhost.Fragments.ShopFragment;

import java.util.List;

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
    ChipGroup cgNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initializeViews();
        backstackListener();
        handleUserInteraction();

        btnProfile.setOnClickListener(view -> {
            if (USER != null) {
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.show(getSupportFragmentManager(), "PROFILE_DIALOG");
            }
            else {
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
        });

        // listen for block
        if (USER != null) {
            DB.collection("users").document(AUTH.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snap, @Nullable FirebaseFirestoreException error) {
                            if (snap != null) {
                                if (snap.contains("blocked")) {
                                    if (snap.getBoolean("blocked")) {
                                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                        finish();
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void handleUserInteraction() {
        cgNav.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup chipGroup, @NonNull List<Integer> list) {
                if (chipGroup.getCheckedChipId() == R.id.chipMyUnits) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment shopFragment = new ShopFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
                    fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
                    fragmentTransaction.commit();
                }
                else if (chipGroup.getCheckedChipId() == R.id.chipBookings) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment bookingsFragment = new BookingsFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, bookingsFragment, "BOOKINGS_FRAGMENT");
                    fragmentTransaction.addToBackStack("BOOKINGS_FRAGMENT");
                    fragmentTransaction.commit();
                }
                else if (chipGroup.getCheckedChipId() == R.id.chipBlockedGuests) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment blockedUsersFragment = new BlockedUsersFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, blockedUsersFragment, "BLOCKED_GUESTS_FRAGMENT");
                    fragmentTransaction.addToBackStack("BLOCKED_GUESTS_FRAGMENT");
                    fragmentTransaction.commit();
                }
            }
        });
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
        cgNav = findViewById(R.id.cgNav);

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