package com.homestaysiaton.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestaysiaton.AuthenticationActivity;
import com.homestaysiaton.Dialogs.ProfileDialog;
import com.homestaysiaton.R;
import com.homestaysiaton.SplashActivity;
import com.homestaysiaton.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CurrentBookingFragment extends Fragment {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    MaterialButton btnProfile;

    View view;
    GoogleMap googleMap;

    AppCompatImageView ivUnit;
    TextView tvBookingStart, tvGuests, tvBookingEnd, tvNumberOfNights, tvNightlyFee, tvCleaningFee, tvTaxFee, tvTotal, tvName, tvLocation, tvDetails, tvPrice;
    MaterialButton btnCancel;

    double price;
    String id, hostUid, unitDetails, unitName;
    double locationLatitude, locationLongitude;
    Long thumbnail, bookingStartDate, bookingEndDate;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            googleMap = map;
            // googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            LatLng unitLocation = new LatLng(locationLatitude, locationLongitude);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(unitLocation)
                    .title(unitName);

            Marker unitMarker = googleMap.addMarker(markerOptions);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unitLocation, 18));

            unitMarker.showInfoWindow();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_current_booking, container, false);

        initializeFirebase();
        initializeViews();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        loadItem();
        listenForBookingCompletion();
        handleUserInteraction();

        return view;
    }

    private void listenForBookingCompletion() {
        DB.collection("bookings").document(Utils.Cache.getString(requireContext(), "booking_id"))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot bookingSnap, @Nullable FirebaseFirestoreException error) {
                        long bookingEndDate = 0;
                        if (bookingSnap != null && bookingSnap.exists()) {
                            bookingEndDate = bookingSnap.getLong("bookingEndDate");
                        }

                        String status = bookingSnap.getString("status");

                        if (!Objects.equals(status, "pending")) {
                            btnCancel.setVisibility(View.GONE);
                        }

                        if ((Objects.equals(status, "completed")) ||
                                (System.currentTimeMillis() > bookingEndDate) ||
                                (!bookingSnap.exists()))
                        {
                            if (System.currentTimeMillis() > bookingEndDate) {
                                DB.collection("bookings").document(Utils.Cache.getString(requireContext(), "booking_id"))
                                        .update("status", "completed")
                                        .addOnCompleteListener(task -> resetFieldsAndCache());
                            }
                            else {
                                resetFieldsAndCache();
                            }
                        }
                    }
                });
    }

    private void resetFieldsAndCache() {
        DB.collection("units").document(Utils.Cache.getString(requireContext(), "booked_unit_id"))
                .update("isBooked", false, "bookingEndDate", 0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utils.Cache.removeKey(requireContext(), "booking_id");
                        Utils.Cache.removeKey(requireContext(), "booked_unit_id");
                        Utils.Cache.removeKey(requireContext(), "booked_start_date");
                        Utils.Cache.removeKey(requireContext(), "booked_end_date");

                        startActivity(new Intent(requireActivity(), SplashActivity.class));
                        requireActivity().finish();
                    }
                });
    }

    private void initializeViews() {
        tvBookingStart = view.findViewById(R.id.tvBookingStart);
        tvBookingEnd = view.findViewById(R.id.tvBookingEnd);
        tvGuests = view.findViewById(R.id.tvGuests);
        tvNumberOfNights = view.findViewById(R.id.tvNumberOfNights);
        tvNightlyFee = view.findViewById(R.id.tvNightlyFee);
        tvCleaningFee = view.findViewById(R.id.tvCleaningFee);
        tvTaxFee = view.findViewById(R.id.tvTaxFee);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnProfile = view.findViewById(R.id.btnProfile);
        ivUnit = view.findViewById(R.id.ivUnit);
        tvName = view.findViewById(R.id.tvName);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDetails = view.findViewById(R.id.tvDetails);
        tvPrice = view.findViewById(R.id.tvPrice);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void loadItem() {
        id = getArguments().getString("id");
        hostUid = getArguments().getString("hostUid");
        Log.d("DEBUG", "RECEIVED ID: "+id);
        price = getArguments().getDouble("price");
        unitDetails = getArguments().getString("unitDetails");
        locationLatitude = getArguments().getDouble("locationLatitude");
        locationLongitude = getArguments().getDouble("locationLongitude");
        unitName = getArguments().getString("unitName");
        thumbnail = getArguments().getLong("thumbnail");
        bookingStartDate = getArguments().getLong("bookingStartDate");
        bookingEndDate = getArguments().getLong("bookingEndDate");
        long adults = Utils.Cache.getLong(requireContext(), "guest_count_adults");
        long children = Utils.Cache.getLong(requireContext(), "guest_count_children");
        long infants = Utils.Cache.getLong(requireContext(), "guest_count_infants");

        SimpleDateFormat sdfSchedule = new SimpleDateFormat("MMM d, yyyy");
        tvBookingStart.setText("Check-in: " + sdfSchedule.format(bookingStartDate));
        tvBookingEnd.setText("Checkout: " + sdfSchedule.format(bookingEndDate));

        StringBuilder strBuilderGuests = new StringBuilder();

        strBuilderGuests.append("Guests: ");
        strBuilderGuests.append(adults);
        strBuilderGuests.append(" adults");
        if (children > 0) {
            strBuilderGuests.append(", ");
            strBuilderGuests.append(children);
            strBuilderGuests.append(" children");
        }
        if (infants > 0) {
            strBuilderGuests.append(", ");
            strBuilderGuests.append(infants);
            strBuilderGuests.append(" infants");
        }

        tvGuests.setText(strBuilderGuests.toString());

        long msDiff = bookingEndDate - bookingStartDate;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        DecimalFormat df = new DecimalFormat("0.00");
        double nightlyFee = price * daysDiff;
        double cleaningFee = nightlyFee * 0.08;
        double subtotal = nightlyFee + cleaningFee;
        double taxFee = subtotal * 0.03;
        double total = subtotal + taxFee;

        tvNumberOfNights.setText("₱"+df.format(price) + " x " + daysDiff + " Nights");

        tvNightlyFee.setText("Total Nightly Fee: ₱" + df.format(nightlyFee));
        tvCleaningFee.setText("Cleaning Fee (8%): ₱" + df.format(cleaningFee));
        tvTaxFee.setText("Tax (3%): ₱" + df.format(taxFee));
        tvTotal.setText("Total: ₱" + df.format(total));

        tvName.setText(unitName);
        tvDetails.setText(unitDetails);
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        tvPrice.setText("₱"+df.format(price));
        storageRef.child("units/"+thumbnail).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(1000,0).centerCrop().into(ivUnit))
                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/1000?text=No+Image").fit().into(ivUnit));
        // addressline
        try {
            List<Address> addresses = geocoder.getFromLocation(locationLatitude, locationLongitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);

                tvLocation.setText(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUserInteraction() {
        btnProfile.setOnClickListener(view -> {
            if (USER != null) {
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.show(requireActivity().getSupportFragmentManager(), "PROFILE_DIALOG");
            }
            else {
                startActivity(new Intent(requireContext(), AuthenticationActivity.class));
            }
        });

        btnCancel.setOnClickListener(view -> {
            DB.collection("bookings")
                    .document(Utils.Cache.getString(requireContext(), "booking_id"))
                    .delete().addOnCompleteListener(task -> {  });
        });
    }
}