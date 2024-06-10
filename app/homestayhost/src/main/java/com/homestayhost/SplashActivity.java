package com.homestayhost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;

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
import com.homestayhost.Utils.Utils;

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
        if (USER != null) {
            // check if user has been blocked by admin
            DB.collection("users").document(USER.getUid())
                    .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot user = task.getResult();

                                    boolean isVerified = false;
                                    if (user.getBoolean("isVerified") != null) {
                                        isVerified = user.getBoolean("isVerified");
                                    }

                                    if (isVerified) {
                                        if (user.contains("blocked")) {
                                            if (user.getBoolean("blocked")) {
                                                // go to blocked activity
                                                startActivity(new Intent(SplashActivity.this, BlockedActivity.class));
                                                finish();
                                            }
                                            else {
                                                // go to main activity
                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                        else {
                                            // go to main activity
                                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }
                                    else {
                                        startActivity(new Intent(SplashActivity.this, PendingVerificationActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });
        }
        else {
            startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
            finish();
        }
        // check if user has booking
        /*Query qryBookings = DB.collection("bookings")
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
                        Utils.Cache.setLong(SplashActivity.this, "booked_start_date", document.getLong("bookingStartDate"));
                        Utils.Cache.setLong(SplashActivity.this, "booked_end_date", document.getLong("bookingEndDate"));
                    }

                    startActivity(new Intent(SplashActivity.this, CurrentBookingActivity.class));
                    finish();
                }
            }
        });*/
    }
}