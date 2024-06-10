package com.homestaysiaton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.homestaysiaton.Fragments.CurrentBookingFragment;
import com.homestaysiaton.Fragments.ShopFragment;
import com.homestaysiaton.Objects.Guests;
import com.homestaysiaton.Objects.ShopItem;
import com.homestaysiaton.Utils.CurrentBookingActivity;
import com.homestaysiaton.Utils.Utils;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    
    AppCompatImageView ivLogo;
    CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (USER == null) {
            // go to main activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();

            return;
        }

        // check if user has booking
        Query qryBookings = DB.collection("bookings")
                .whereEqualTo("userUid", AUTH.getCurrentUser().getUid())
                .whereEqualTo("status", "pending")
                .orderBy("bookingStartDate", Query.Direction.ASCENDING)
                .limit(1);
        qryBookings.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // if user has no booking
                if (task.getResult().isEmpty()) {
                    // go to main activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
                // if user has booking
                else {
                    // go to current booking activity
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Utils.Cache.setString(SplashActivity.this, "booking_id", document.getString("id"));
                        Utils.Cache.setString(SplashActivity.this, "booked_unit_id", document.getString("unitId"));
                        Utils.Cache.setLong(SplashActivity.this, "guest_count_adults", document.getLong("adult_guests"));
                        Utils.Cache.setLong(SplashActivity.this, "guest_count_children", document.getLong("children_guests"));
                        Utils.Cache.setLong(SplashActivity.this, "guest_count_infants", document.getLong("infant_guests"));
                        Utils.Cache.setLong(SplashActivity.this, "booked_start_date", document.getLong("bookingStartDate"));
                        Utils.Cache.setLong(SplashActivity.this, "booked_end_date", document.getLong("bookingEndDate"));
                    }

                    startActivity(new Intent(SplashActivity.this, CurrentBookingActivity.class));
                    finish();
                }
            }
        });
    }
}