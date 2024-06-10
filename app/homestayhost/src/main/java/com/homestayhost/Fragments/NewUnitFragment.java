 package com.homestayhost.Fragments;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homestayhost.AuthenticationActivity;
import com.homestayhost.MainActivity;
import com.homestayhost.R;
import com.homestayhost.SplashActivity;
import com.homestayhost.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

 public class NewUnitFragment extends Fragment {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    FirebaseStorage STORAGE;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
        STORAGE = FirebaseStorage.getInstance();
    }

    ActivityResultLauncher<Intent> activityResultLauncher;

    View view;
    GoogleMap googleMap;

     TextView tvFragmentTitle;
    RoundedImageView imgUnit;
    TextInputEditText etUnitName, etUnitDetails, etUnitAddress, etDailyRent, etMaxGuests;
    MaterialButton btnBack, btnUploadUnitThumbnail, btnAddUnit, btnDeleteUnit;
    Uri uriSelected = null;
    Uri uriRetrievedFromStorage = null;

     String id, hostUid, addressLine, unitDetails, unitName, mode;
     Double price, maxGuests, locationLatitude, locationLongitude;
     Long thumbnail;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            googleMap = map;
            // googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            LatLng siatonDefaultLocation = new LatLng(9.063723651244242, 123.03453869762073);

            /*MarkerOptions markerOptions = new MarkerOptions()
                    .position(siatonDefaultLocation)
                    .title(unitName);

            Marker unitMarker = googleMap.addMarker(markerOptions);*/

            if (Objects.equals(mode, "add")) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siatonDefaultLocation, 18));
            }
            else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLatitude, locationLongitude), 18));
            }

            //unitMarker.showInfoWindow();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_unit, container, false);

        initializeFirebase();
        initialize(view);
        initializeActivityResultLauncher();
        loadArgsData();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        buttonHandler();

        return view;
    }

     private void loadArgsData() {
         mode = getArguments().getString("mode");

         if (Objects.equals(mode, "edit")) {
             tvFragmentTitle.setText("Edit Unit");
             btnDeleteUnit.setVisibility(View.VISIBLE);

             id = getArguments().getString("id");
             hostUid = getArguments().getString("hostUid");
             price = getArguments().getDouble("price");
             maxGuests = getArguments().getDouble("maxGuests");
             locationLatitude = getArguments().getDouble("locationLatitude");
             locationLongitude = getArguments().getDouble("locationLongitude");
             addressLine = getArguments().getString("addressLine");
             unitDetails = getArguments().getString("unitDetails");
             unitName = getArguments().getString("unitName");
             thumbnail = getArguments().getLong("thumbnail");

             etUnitName.setText(unitName);
             etUnitDetails.setText(unitDetails);
             etUnitAddress.setText(addressLine);
             etDailyRent.setText("" + price);
             etMaxGuests.setText(String.format("%.0f", maxGuests));

             STORAGE.getReference("units/"+thumbnail).getDownloadUrl()
                     .addOnCompleteListener(new OnCompleteListener<Uri>() {
                         @Override
                         public void onComplete(@NonNull Task<Uri> task) {
                             if (task.isSuccessful()) {
                                 Uri uri = task.getResult();
                                 uriRetrievedFromStorage = uri;
                                 uriSelected = uri;
                                 Picasso.get().load(uri).resize(800,0).centerCrop().into(imgUnit);
                                 imgUnit.setVisibility(View.VISIBLE);
                             }
                             else {
                                 Toast.makeText(requireContext(), "Failed to load unit thumbnail", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
         }
         else {
             tvFragmentTitle.setText("Add New Unit");
             btnDeleteUnit.setVisibility(View.GONE);
         }
     }

     private void initialize(View view) {
        tvFragmentTitle = view.findViewById(R.id.tvFragmentTitle);
        btnBack = view.findViewById(R.id.btnBack);
        imgUnit = view.findViewById(R.id.imgUnit);
        btnUploadUnitThumbnail = view.findViewById(R.id.btnUploadUnitThumbnail);
        etUnitName = view.findViewById(R.id.etUnitName);
        etUnitDetails = view.findViewById(R.id.etUnitDetails);
        etUnitAddress = view.findViewById(R.id.etUnitAddress);
        etDailyRent = view.findViewById(R.id.etDailyRent);
        etMaxGuests = view.findViewById(R.id.etMaxGuests);
        btnAddUnit = view.findViewById(R.id.btnAddUnit);
        btnDeleteUnit = view.findViewById(R.id.btnDeleteUnit);
    }

    private void buttonHandler() {
        btnBack.setOnClickListener(view -> requireActivity().onBackPressed());

        btnUploadUnitThumbnail.setOnClickListener(view -> {
            selectImageFromDevice();
        });

        btnDeleteUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder dialogDeleteUnit = new MaterialAlertDialogBuilder(requireContext());
                dialogDeleteUnit.setTitle("Confirm Delete Unit");
                dialogDeleteUnit.setMessage("Are you sure you want to delete this unit?");
                dialogDeleteUnit.setPositiveButton("Delete Unit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DB.collection("units").document(id)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        DB.collection("bookings").whereEqualTo("id", id)
                                            .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().size() > 0) {
                                                                int counter = 0;
                                                                int numberOfBookings = task.getResult().getDocuments().size();

                                                                for (DocumentSnapshot booking : task.getResult().getDocuments()) {
                                                                    counter++;
                                                                    int finalCounter = counter;
                                                                    DB.collection("bookings").document(booking.getId()).delete()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (finalCounter == numberOfBookings - 1) {
                                                                                        Toast.makeText(requireContext(), "Unit was successfully deleted", Toast.LENGTH_SHORT).show();

                                                                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                                        Fragment shopFragment = new ShopFragment();
                                                                                        fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
                                                                                        fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
                                                                                        fragmentTransaction.commit();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                            else {
                                                                Toast.makeText(requireContext(), "Unit was successfully deleted", Toast.LENGTH_SHORT).show();

                                                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                Fragment shopFragment = new ShopFragment();
                                                                fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
                                                                fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
                                                                fragmentTransaction.commit();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
                dialogDeleteUnit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }});
                dialogDeleteUnit.show();
            }
        });

        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddUnit.setEnabled(false);

                if (Objects.equals(mode, "add")) {
                    addUnit();
                }
                else {
                    editUnit();
                }
            }

            private void editUnit() {
                String unitName = etUnitName.getText().toString().trim();
                String unitDetails = etUnitDetails.getText().toString().trim();
                String unitAddressLine = etUnitAddress.getText().toString().trim();
                String strDailyRent = etDailyRent.getText().toString().trim();
                String strMaxGuests = etMaxGuests.getText().toString().trim();

                if (uriSelected == null) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please upload a thumbnail for the unit", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitName)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide a unit name", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitDetails)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide unit details", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitAddressLine)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide the unit's address line", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(strDailyRent)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please specify the daily rental fee for the unit", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                ArrayList<String> rawKeywords = new ArrayList<>();
                List<String> keywords;
                rawKeywords.addAll(Arrays.asList(unitName.split("[, ]")));
                rawKeywords.addAll(Arrays.asList(unitDetails.split("[, ]")));
                rawKeywords.addAll(Arrays.asList(unitAddressLine.split("[, ]")));
                keywords = rawKeywords.stream().distinct().collect(Collectors.toList());
                Log.d("keywords", keywords.toString());
                double dailyRent = Double.parseDouble(strDailyRent);
                double maxGuests = Double.parseDouble(strMaxGuests);

                Long imageFileName = System.currentTimeMillis();

                Map<String, Object> unit = new HashMap<>();
                unit.put("unitName", unitName);
                unit.put("unitNameAllCaps", unitName.toUpperCase());
                unit.put("unitDetails", unitDetails);
                unit.put("keywords", keywords);
                unit.put("addressLine", unitAddressLine);
                unit.put("location", googleMap.getCameraPosition().target);
                unit.put("price", dailyRent);
                unit.put("maxGuests", maxGuests);
                unit.put("categoryId", "Uncategorized");
                unit.put("isBooked", false);
                unit.put("bookingEndDate", 0);

                if (uriSelected == uriRetrievedFromStorage) {
                    // if uriSelected value was not changed, then no need to reupload thumbnail
                    if (uriRetrievedFromStorage == null) {
                        Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please upload a thumbnail for the unit", "Okay");
                        btnAddUnit.setEnabled(true);
                        return;
                    }
                    updateData(unit, imageFileName);
                }
                else {
                    StorageReference bannerReference = STORAGE.getReference().child("units/"+imageFileName);
                    bannerReference.putFile(uriSelected).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Utils.simpleDialog(requireContext(), "Thumbnail Upload Failed", "" + task.getException().getLocalizedMessage(), "Try Again");
                                // Toast.makeText(requireContext(), "Failed to upload unit thumbnail. Please try again.", Toast.LENGTH_SHORT).show();
                                btnAddUnit.setEnabled(true);
                            }
                            else if (task.isSuccessful()) {
                                unit.put("thumbnail", imageFileName);
                                updateData(unit, imageFileName);
                            }
                        }
                    });
                }
            }

            private void updateData(Map<String, Object> unit, Long imageFileName) {
                DocumentReference thisUnit = DB.collection("units").document(id);
                thisUnit.update(unit)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Unit was successfully updated", Toast.LENGTH_SHORT).show();

                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    Fragment shopFragment = new ShopFragment();
                                    fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
                                    fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
                                    fragmentTransaction.commit();
                                }
                                else {
                                    Utils.simpleDialog(requireContext(), "Failed to add new unit", ""+task.getException().getLocalizedMessage(), "Try Again");
                                    btnAddUnit.setEnabled(true);
                                }
                            }
                        });
            }

            private void addUnit() {
                String unitName = etUnitName.getText().toString().trim();
                String unitDetails = etUnitDetails.getText().toString().trim();
                String unitAddressLine = etUnitAddress.getText().toString().trim();
                String strDailyRent = etDailyRent.getText().toString().trim();
                String strMaxGuests = etMaxGuests.getText().toString().trim();
                if (uriSelected == null) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please upload a thumbnail for the unit", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitName)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide a unit name", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitDetails)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide unit details", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(unitAddressLine)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please provide the unit's address line", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(strDailyRent)) {
                    Utils.simpleDialog(requireContext(), "Failed to Add Unit", "Please specify the daily rental fee for the unit", "Okay");
                    btnAddUnit.setEnabled(true);
                    return;
                }

                ArrayList<String> rawKeywords = new ArrayList<>();
                List<String> keywords;
                rawKeywords.addAll(Arrays.asList(unitName.split("[, ]")));
                rawKeywords.addAll(Arrays.asList(unitDetails.split("[, ]")));
                rawKeywords.addAll(Arrays.asList(unitAddressLine.split("[, ]")));
                keywords = rawKeywords.stream().distinct().collect(Collectors.toList());
                Log.d("keywords", keywords.toString());
                double dailyRent = Double.parseDouble(strDailyRent);
                double maxGuests = Double.parseDouble(strMaxGuests);

                DocumentReference newUnit = DB.collection("units").document();
                String unitId = newUnit.getId();
                Long imageFileName = System.currentTimeMillis();

                Map<String, Object> unit = new HashMap<>();
                unit.put("id", unitId);
                unit.put("hostUid", AUTH.getCurrentUser().getUid());
                unit.put("unitName", unitName);
                unit.put("unitNameAllCaps", unitName.toUpperCase());
                unit.put("unitDetails", unitDetails);
                unit.put("keywords", keywords);
                unit.put("addressLine", unitAddressLine);
                unit.put("location", googleMap.getCameraPosition().target);
                unit.put("price", dailyRent);
                unit.put("categoryId", "Uncategorized");
                unit.put("isBooked", false);
                unit.put("bookingEndDate", 0);
                unit.put("maxGuests", maxGuests);

                Log.d("DEBUG", "DocRef ID: " + newUnit.getId());
                Log.d("DEBUG", "UNIT ID: " + unitId);

                StorageReference bannerReference = STORAGE.getReference().child("units/"+imageFileName);
                bannerReference.putFile(uriSelected).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Utils.simpleDialog(requireContext(), "Thumbnail Upload Failed", "" + task.getException().getLocalizedMessage(), "Try Again");
                            // Toast.makeText(requireContext(), "Failed to upload unit thumbnail. Please try again.", Toast.LENGTH_SHORT).show();
                            btnAddUnit.setEnabled(true);
                        }
                        else if (task.isSuccessful()) {
                            unit.put("thumbnail", imageFileName);
                            newUnit.set(unit)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(requireContext(), "Unit was successfully added", Toast.LENGTH_SHORT).show();

                                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                Fragment shopFragment = new ShopFragment();
                                                fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "SHOP_FRAGMENT");
                                                fragmentTransaction.addToBackStack("SHOP_FRAGMENT");
                                                fragmentTransaction.commit();
                                            }
                                            else {
                                                Utils.simpleDialog(requireContext(), "Failed to add new unit", ""+task.getException().getLocalizedMessage(), "Try Again");
                                                btnAddUnit.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    private void selectImageFromDevice() {
        Intent iImageSelect = new Intent();
        iImageSelect.setType("image/*");
        iImageSelect.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(Intent.createChooser(iImageSelect, "Select ID"));
    }

    private void initializeActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uriRetrieved = Objects.requireNonNull(data).getData();
                        uriSelected = uriRetrieved;

                        // display selected image
                        Picasso.get().load(uriRetrieved).resize(800,0).centerCrop().into(imgUnit);
                        imgUnit.setVisibility(View.VISIBLE);

                    }
                }
        );
    }
}