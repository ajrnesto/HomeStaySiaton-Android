package com.homestaysiaton.Fragments;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.homestaysiaton.AuthenticationActivity;
import com.homestaysiaton.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestaysiaton.SplashActivity;
import com.homestaysiaton.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ShopItemFragment extends Fragment {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    GoogleMap googleMap;

    double price;
    int maxGuests;
    String id, hostUid, addressLine, unitDetails, unitName;
    double locationLatitude, locationLongitude;
    Long thumbnail;

    AppCompatImageView ivUnit;
    ConstraintLayout clGuests;
    TextView tvMaxGuestsMessage, tvAdultGuests, tvChildrenGuests, tvInfantsGuests, tvCleaningFee, tvTaxFee, tvTotal;
    MaterialButton btnClose, btnDecrementAdult, btnIncrementAdult, btnDecrementChildren, btnIncrementChildren, btnDecrementInfants, btnIncrementInfants, btnSaveGuests, btnCancel;
    TextView tvName, tvLocation, tvDetails, tvPrice, tvNumberOfNights, tvNightlyFee;
    TextInputEditText etStartDate, etEndDate, etGuests;
    MaterialButton btnBack, btnBook;
    MaterialDatePicker<Pair<Long, Long>> dateRangePicker;
    MaterialDatePicker<Long> dateStartPicker, dateEndPicker;

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
        view = inflater.inflate(R.layout.fragment_shop_item, container, false);

        initializeFirebase();
        initiate(view);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        loadItem();
        buttonHandler();

        return view;
    }

    private void initiate(View view) {
        clGuests = view.findViewById(R.id.clGuests);
        btnBack = view.findViewById(R.id.btnBack);
        ivUnit = view.findViewById(R.id.ivUnit);
        tvName = view.findViewById(R.id.tvName);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvDetails = view.findViewById(R.id.tvDetails);
        tvPrice = view.findViewById(R.id.tvPrice);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        tvNumberOfNights = view.findViewById(R.id.tvNumberOfNights);
        tvNightlyFee = view.findViewById(R.id.tvNightlyFee);
        btnBook = view.findViewById(R.id.btnBook);
        etGuests = view.findViewById(R.id.etGuests);
        tvCleaningFee = view.findViewById(R.id.tvCleaningFee);
        tvTaxFee = view.findViewById(R.id.tvTaxFee);
        tvTotal = view.findViewById(R.id.tvTotal);

        tvMaxGuestsMessage = view.findViewById(R.id.tvMaxGuestsMessage);
        tvAdultGuests = view.findViewById(R.id.tvAdultGuests);
        tvChildrenGuests = view.findViewById(R.id.tvChildrenGuests);
        tvInfantsGuests = view.findViewById(R.id.tvInfantsGuests);
        btnClose = view.findViewById(R.id.btnClose);
        btnDecrementAdult = view.findViewById(R.id.btnDecrementAdult);
        btnIncrementAdult = view.findViewById(R.id.btnIncrementAdult);
        btnDecrementChildren = view.findViewById(R.id.btnDecrementChildren);
        btnIncrementChildren = view.findViewById(R.id.btnIncrementChildren);
        btnDecrementInfants = view.findViewById(R.id.btnDecrementInfants);
        btnIncrementInfants = view.findViewById(R.id.btnIncrementInfants);
        btnSaveGuests = view.findViewById(R.id.btnSaveGuests);
        btnCancel = view.findViewById(R.id.btnCancel);

        Calendar calNow = Calendar.getInstance();
        calNow.set(Calendar.HOUR_OF_DAY, 0);
        calNow.set(Calendar.MINUTE, 0);
        calNow.set(Calendar.SECOND, 0);
        calNow.set(Calendar.MILLISECOND, 0);

        dateStartPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Check-in Date")
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(calNow.getTimeInMillis())).build())
                .setSelection(calNow.getTimeInMillis())
                .build();

        dateStartPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                etStartDate.setText(simpleDateFormat.format(selection));
                etEndDate.setText("");

                calculateNumberOfNightsAndTotal();

                Calendar calTomorrow = Calendar.getInstance();
                calTomorrow.setTimeInMillis(dateStartPicker.getSelection());
                calTomorrow.add(Calendar.DAY_OF_MONTH, 1);
                calTomorrow.set(Calendar.HOUR_OF_DAY, 0);
                calTomorrow.set(Calendar.MINUTE, 0);
                calTomorrow.set(Calendar.SECOND, 0);
                calTomorrow.set(Calendar.MILLISECOND, 0);

                dateEndPicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Checkout Date")
                        .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(calTomorrow.getTimeInMillis())).build())
                        .setSelection(calTomorrow.getTimeInMillis())
                        .build();

                dateEndPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                        etEndDate.setText(simpleDateFormat.format(dateEndPicker.getSelection()));

                        calculateNumberOfNightsAndTotal();
                    }
                });
            }
        });

        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.setTimeInMillis(dateStartPicker.getSelection());
        calTomorrow.add(Calendar.DAY_OF_MONTH, 1);
        calTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        calTomorrow.set(Calendar.MINUTE, 0);
        calTomorrow.set(Calendar.SECOND, 0);
        calTomorrow.set(Calendar.MILLISECOND, 0);

        dateEndPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Checkout Date")
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(calTomorrow.getTimeInMillis())).build())
                .setSelection(calTomorrow.getTimeInMillis())
                .build();

        dateEndPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                etEndDate.setText(simpleDateFormat.format(dateEndPicker.getSelection()));

                calculateNumberOfNightsAndTotal();
            }
        });

        /*dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Set Booking Duration")
                .setSelection(new Pair<>(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()))
                .setPositiveButtonText("Submit Booking")
                .build();
        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                // set unit's isBooked and bookingEndDate values
                DB.collection("units").document(id)
                        .update("isBooked", true, "bookingEndDate", selection.second)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // set new booking document
                                DocumentReference refNewBooking = DB.collection("bookings").document();
                                HashMap<String, Object> newBooking = new HashMap<>();
                                newBooking.put("id", refNewBooking.getId());
                                newBooking.put("userUid", AUTH.getCurrentUser().getUid());
                                newBooking.put("hostUid", hostUid);
                                newBooking.put("unitId", id);
                                newBooking.put("bookingStartDate", selection.first);
                                newBooking.put("bookingEndDate", selection.second);
                                newBooking.put("status", "pending");
                                newBooking.put("timestamp", System.currentTimeMillis());
                                refNewBooking.set(newBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        requireActivity().startActivity(new Intent(requireActivity(), SplashActivity.class));
                                        requireActivity().finish();
                                    }
                                });
                            }
                        });
                // set new bookingSchedules document (for
            }
        });*/
    }

    private void calculateNumberOfNightsAndTotal() {
        if (etStartDate.getText().toString().isEmpty() && etEndDate.getText().toString().isEmpty()) {
            return;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(dateStartPicker.getSelection());
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(dateEndPicker.getSelection());

        long msDiff = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        double nightlyFee = price * daysDiff;
        double cleaningFee = nightlyFee * 0.08;
        double subtotal = nightlyFee + cleaningFee;
        double taxFee = subtotal * 0.03;
        double total = subtotal + taxFee;

        DecimalFormat df = new DecimalFormat("0.00");
        tvNumberOfNights.setText("₱"+df.format(price) + " x " + daysDiff + " Nights");
        tvNightlyFee.setText("Total Nightly Fee: ₱" + df.format(nightlyFee));
        tvCleaningFee.setText("Cleaning Fee (8%): ₱" + df.format(cleaningFee));
        tvTaxFee.setText("Tax (3%): ₱" + df.format(taxFee));
        tvTotal.setText("Total: ₱" + df.format(total));
    }

    private void loadItem() {
        assert getArguments() != null;
        id = getArguments().getString("id");
        hostUid = getArguments().getString("hostUid");
        Log.d("DEBUG", "RECEIVED ID: "+id);
        price = getArguments().getDouble("price");
        maxGuests = getArguments().getInt("maxGuests");
        unitDetails = getArguments().getString("unitDetails");
        addressLine = getArguments().getString("addressLine");
        locationLatitude = getArguments().getDouble("locationLatitude");
        locationLongitude = getArguments().getDouble("locationLongitude");
        unitName = getArguments().getString("unitName");
        thumbnail = getArguments().getLong("thumbnail");

        tvName.setText(unitName);
        tvDetails.setText(unitDetails);
        tvLocation.setText(addressLine);
        tvMaxGuestsMessage.setText("This place has a maximum of " + maxGuests + " guests, not including infants. Pets are not allowed.");
        DecimalFormat df = new DecimalFormat("0.00");
        tvPrice.setText("₱"+df.format(price));
        storageRef.child("units/"+thumbnail).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(1000,0).centerCrop().into(ivUnit))
                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/1000?text=No+Image").fit().into(ivUnit));
    }

    private void buttonHandler() {
        etGuests.setOnClickListener(v -> clGuests.setVisibility(View.VISIBLE));

        btnCancel.setOnClickListener(v -> clGuests.setVisibility(View.GONE));

        btnSaveGuests.setOnClickListener(v -> {
            int adults = Integer.parseInt(tvAdultGuests.getText().toString());
            int children = Integer.parseInt(tvChildrenGuests.getText().toString());
            int infants = Integer.parseInt(tvInfantsGuests.getText().toString());

            StringBuilder strBuilderGuests = new StringBuilder();
            if (adults == 0) {
                Utils.simpleDialog(requireContext(), "Invalid Number of Guests", "Please ensure at least one adult is included in the number of guests.", "Okay");
                return;
            }

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

            if ((adults + children) > maxGuests) {
                Utils.simpleDialog(requireContext(), "Invalid Number of Guests", "The number of guests you've entered exceeds the maximum capacity of this unit. Please adjust the number of guests accordingly.", "Okay");
                return;
            }

            etGuests.setText(strBuilderGuests.toString());
            clGuests.setVisibility(View.GONE);
        });

        btnDecrementAdult.setOnClickListener(v -> {
            int adults = Integer.parseInt(tvAdultGuests.getText().toString());
            if (adults > 0) {
                adults--;
                tvAdultGuests.setText("" + adults);
            }
        });

        btnIncrementAdult.setOnClickListener(v -> {
            int adults = Integer.parseInt(tvAdultGuests.getText().toString());
            int children = Integer.parseInt(tvChildrenGuests.getText().toString());
            if (adults + children < maxGuests) {
                adults++;
                tvAdultGuests.setText("" + adults);
            }
        });

        btnDecrementChildren.setOnClickListener(v -> {
            int children = Integer.parseInt(tvChildrenGuests.getText().toString());
            if (children > 0) {
                children--;
                tvChildrenGuests.setText("" + children);
            }
        });

        btnIncrementChildren.setOnClickListener(v -> {
            int adults = Integer.parseInt(tvAdultGuests.getText().toString());
            int children = Integer.parseInt(tvChildrenGuests.getText().toString());
            if (adults + children < maxGuests) {
                children++;
                tvChildrenGuests.setText("" + children);
            }
        });

        btnDecrementInfants.setOnClickListener(v -> {
            int infants = Integer.parseInt(tvInfantsGuests.getText().toString());
            if (infants > 0) {
                infants--;
                tvInfantsGuests.setText("" + infants);
            }
        });

        btnIncrementInfants.setOnClickListener(v -> {
            int infants = Integer.parseInt(tvInfantsGuests.getText().toString());
            if (infants < 99) {
                infants++;
                tvInfantsGuests.setText("" + infants);
            }
        });

        etStartDate.setOnClickListener(view -> dateStartPicker.show(requireActivity().getSupportFragmentManager(), "DATE_START_PICKER"));

        etEndDate.setOnClickListener(view -> dateEndPicker.show(requireActivity().getSupportFragmentManager(), "DATE_END_PICKER"));

        btnBack.setOnClickListener(view -> requireActivity().onBackPressed());

        btnBook.setOnClickListener(view -> {
            if (AUTH.getCurrentUser() != null) {
                btnBook.setEnabled(false);
                if (etStartDate.getText().toString().isEmpty() || etEndDate.getText().toString().isEmpty() || etGuests.getText().toString().isEmpty()) {
                    Utils.simpleDialog(requireContext(), "Incomplete Booking Information", "Please fill in all required fields to proceed with your booking. Ensure that you've provided the check-in date, check-out date, and number of guests.", "Okay");
                    btnBook.setEnabled(true);
                    return;
                }

                DB.collection("units").document(id)
                        .update("isBooked", true, "bookingEndDate", dateEndPicker.getSelection())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // set new booking document
                                DocumentReference refNewBooking = DB.collection("bookings").document();
                                HashMap<String, Object> newBooking = new HashMap<>();
                                newBooking.put("id", refNewBooking.getId());
                                newBooking.put("userUid", AUTH.getCurrentUser().getUid());
                                newBooking.put("hostUid", hostUid);
                                newBooking.put("unitId", id);
                                newBooking.put("bookingStartDate", dateStartPicker.getSelection());
                                newBooking.put("bookingEndDate", dateEndPicker.getSelection());
                                newBooking.put("status", "pending");
                                newBooking.put("timestamp", System.currentTimeMillis());
                                newBooking.put("adult_guests", Integer.parseInt(tvAdultGuests.getText().toString()));
                                newBooking.put("children_guests", Integer.parseInt(tvChildrenGuests.getText().toString()));
                                newBooking.put("infant_guests", Integer.parseInt(tvInfantsGuests.getText().toString()));
                                refNewBooking.set(newBooking).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        buildOrdersNotification();

                                        requireActivity().startActivity(new Intent(requireActivity(), SplashActivity.class));
                                        requireActivity().finish();
                                    }
                                });
                            }
                        });
            }
            else {
                MaterialAlertDialogBuilder dialogLoginRequired = new MaterialAlertDialogBuilder(requireContext());
                dialogLoginRequired.setTitle("Sign in required");
                dialogLoginRequired.setMessage("You need to sign in to book units.");
                dialogLoginRequired.setPositiveButton("Log in", (dialogInterface, i) -> {
                    requireContext().startActivity(new Intent(requireContext(), AuthenticationActivity.class));
                    ((Activity)requireContext()).finish();
                });
                dialogLoginRequired.setNeutralButton("Back", (dialogInterface, i) -> { });
                dialogLoginRequired.show();
            }
        });
    }

    private void buildOrdersNotification() {
        String channelID = "Orders Notifications";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelID);
        builder.setContentTitle("HomeStay Siaton")
                .setSmallIcon(R.mipmap.ic_launcher_homestay)
                .setContentText("You have successfully booked the unit " + unitName)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent intent = new Intent(requireContext(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Orders Notifications", importance);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());
    }
}