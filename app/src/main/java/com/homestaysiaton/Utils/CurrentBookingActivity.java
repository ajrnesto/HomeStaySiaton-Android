package com.homestaysiaton.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homestaysiaton.AuthenticationActivity;
import com.homestaysiaton.Dialogs.ProfileDialog;
import com.homestaysiaton.Fragments.CurrentBookingFragment;
import com.homestaysiaton.Fragments.ShopFragment;
import com.homestaysiaton.MainActivity;
import com.homestaysiaton.Objects.ShopItem;
import com.homestaysiaton.R;
import com.homestaysiaton.SplashActivity;

public class CurrentBookingActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_current_booking);

        initializeFirebase();
        initializeViews();
        backstackListener();

        btnProfile.setOnClickListener(view -> {
            if (USER != null) {
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.show(getSupportFragmentManager(), "PROFILE_DIALOG");
            }
            else {
                startActivity(new Intent(CurrentBookingActivity.this, AuthenticationActivity.class));
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

        String unitId = Utils.Cache.getString(CurrentBookingActivity.this, "booked_unit_id");
        long bookingStartDate = Utils.Cache.getLong(CurrentBookingActivity.this, "booked_start_date");
        long bookingEndDate = Utils.Cache.getLong(CurrentBookingActivity.this, "booked_end_date");

        DB.collection("units").document(unitId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ShopItem unit = task.getResult().toObject(ShopItem.class);

                            Bundle bundle = new Bundle();
                            bundle.putString("id", unit.getId());
                            bundle.putString("hostUid", unit.getHostUid());
                            bundle.putString("unitName", unit.getUnitName());
                            bundle.putString("unitDetails", unit.getUnitDetails());
                            bundle.putDouble("locationLatitude", unit.getLocation().getLatitude());
                            bundle.putDouble("locationLongitude", unit.getLocation().getLongitude());
                            bundle.putDouble("price", unit.getPrice());
                            bundle.putLong("thumbnail", unit.getThumbnail());
                            bundle.putLong("bookingStartDate", bookingStartDate);
                            bundle.putLong("bookingEndDate", bookingEndDate);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                            Fragment currentBookingFragment = new CurrentBookingFragment();
                            currentBookingFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.fragmentHolder, currentBookingFragment, "CURRENT_BOOKING_FRAGMENT");
                            fragmentTransaction.addToBackStack("CURRENT_BOOKING_FRAGMENT");
                            fragmentTransaction.commit();
                        }
                    }
                });
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