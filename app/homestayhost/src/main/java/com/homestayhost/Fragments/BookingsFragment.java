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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.homestayhost.Adapters.BookingAdapter;
import com.homestayhost.AuthenticationActivity;
import com.homestayhost.Dialogs.ProfileDialog;
import com.homestayhost.Objects.Booking;
import com.homestayhost.R;
import com.homestayhost.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BookingsFragment extends Fragment implements BookingAdapter.OnBookingListener {

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
    RecyclerView rvBookings;

    ArrayList<Booking> arrBooking;
    BookingAdapter bookingAdapter;
    BookingAdapter.OnBookingListener onBookingListener = this;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookings, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadRecyclerView();

        return view;
    }

    private void initializeViews() {
        clEmpty = view.findViewById(R.id.clEmpty);
        rvBookings = view.findViewById(R.id.rvBookings);

        Utils.Cache.setBoolean(requireContext(), "shop_item_dialog_is_visible", false);
    }

    private void loadRecyclerView() {
        arrBooking = new ArrayList<>();
        rvBookings = view.findViewById(R.id.rvBookings);
        rvBookings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvBookings.setLayoutManager(linearLayoutManager);


        qryShop = DB.collection("bookings")
                //.whereEqualTo("isBooked", false)
                .whereEqualTo("hostUid", AUTH.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        qryShop.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("DEBUG", "Listen failed.", error);
                    return;
                }

                arrBooking.clear();

                for (QueryDocumentSnapshot doc : value) {
                    arrBooking.add(doc.toObject(Booking.class));
                    bookingAdapter.notifyDataSetChanged();
                }

                if (value.isEmpty()) {
                    clEmpty.setVisibility(View.VISIBLE);
                    rvBookings.setVisibility(View.GONE);
                }
                else {
                    clEmpty.setVisibility(View.GONE);
                    rvBookings.setVisibility(View.VISIBLE);
                }
            }
        });

        bookingAdapter = new BookingAdapter(requireContext(), arrBooking, onBookingListener);
        rvBookings.setAdapter(bookingAdapter);
        bookingAdapter.notifyDataSetChanged();
    }

    private void handleButtonClicks() {
    }

    @Override
    public void onBookingClick(int position) {
    }
}