package com.homestayhost.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.homestayhost.AuthenticationActivity;
import com.homestayhost.Fragments.BlockedUsersFragment;
import com.homestayhost.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileDialog extends AppCompatDialogFragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    Query qryUser;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    TextView tvFullname, tvEmail, tvUid, tvAddress, tvStaticPresentAddressHeading, tvPresentAddressHeading;
    TextInputLayout tilFirstName, tilLastName, tilMobile, tilAddressPurok, tilAddressBarangay, tilAddressCity;
    TextInputEditText etFirstName, etLastName, etMobile, etAddressPurok, etAddressBarangay, etAddressCity;
    MaterialButton btnEditProfile, btnUpdateProfile, btnLogout;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile, null);

        initializeFirebase();
        initiate(view);
        loadUserInformation();
        buttonHandler();

        builder.setView(view);
        return builder.create();
    }

    private void initiate(View view) {
        tvFullname = view.findViewById(R.id.tvFullname);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvStaticPresentAddressHeading = view.findViewById(R.id.tvStaticPresentAddressHeading);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnLogout = view.findViewById(R.id.btnLogout);
        tilFirstName = view.findViewById(R.id.tilFirstName);
        tilLastName = view.findViewById(R.id.tilLastName);
        tilMobile = view.findViewById(R.id.tilMobile);
        tilAddressPurok = view.findViewById(R.id.tilAddressPurok);
        tilAddressBarangay = view.findViewById(R.id.tilAddressBarangay);
        tilAddressCity = view.findViewById(R.id.tilAddressCity);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etMobile = view.findViewById(R.id.etMobile);
        tvPresentAddressHeading = view.findViewById(R.id.tvPresentAddressHeading);
        etAddressPurok = view.findViewById(R.id.etAddressPurok);
        etAddressBarangay = view.findViewById(R.id.etAddressBarangay);
        etAddressCity = view.findViewById(R.id.etAddressCity);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        tvUid = view.findViewById(R.id.tvUid);
        etAddressPurok = view.findViewById(R.id.etAddressPurok);
        etAddressBarangay = view.findViewById(R.id.etAddressBarangay);
    }

    private void loadUserInformation() {
        DB.collection("users").document(AUTH.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String mobile = USER.getPhoneNumber();
                        String firstName = "";
                        String lastName = "";
                        String addressPurok = "";
                        String addressBarangay = "";
                        String addressCity = "";
                        if (documentSnapshot.getData() != null){
                            firstName = documentSnapshot.getString("firstName");
                            lastName = documentSnapshot.getString("lastName");
                            addressPurok = documentSnapshot.getString("addressPurok");
                            addressBarangay = documentSnapshot.getString("addressBarangay");
                            addressCity = documentSnapshot.getString("addressCity");
                            String fullName = firstName+" "+lastName;
                            tvFullname.setText(fullName.trim());
                        }
                        else {
                            tvFullname.setText("No Name");
                        }

                        tvUid.setText("UID: " + AUTH.getCurrentUser().getUid());
                        tvEmail.setText(mobile);

                        if (addressPurok == null ||
                                addressPurok.isEmpty() ||
                                addressBarangay == null ||
                                addressBarangay.isEmpty() ||
                                addressCity == null ||
                                addressCity.isEmpty())
                        {
                            tvAddress.setText("Not set");
                        }
                        else {
                            tvAddress.setText(addressPurok + ", " + addressBarangay + ", " + addressCity);
                        }
                        etFirstName.setText(firstName);
                        etLastName.setText(lastName);
                        etAddressPurok.setText(addressPurok);
                        etAddressBarangay.setText(addressBarangay);
                        etAddressCity.setText(addressCity);
                        etMobile.setText(mobile);
                    }
                });
    }

    private void buttonHandler() {
        btnLogout.setOnClickListener(view -> {
            MaterialAlertDialogBuilder dialogLogout = new MaterialAlertDialogBuilder(requireActivity());
            dialogLogout.setTitle("Confirm logout")
                    .setNeutralButton("Cancel", (dialogInterface, i) -> {

                    })
                    .setNegativeButton("Log out", (dialogInterface, i) -> {
                        AUTH.signOut();
                        startActivity(new Intent(requireContext(), AuthenticationActivity.class));
                        requireActivity().finish();
                    }).show();
        });

        btnEditProfile.setOnClickListener(view -> {
            tvPresentAddressHeading.setVisibility(View.VISIBLE);
            tilFirstName.setVisibility(View.VISIBLE);
            tilLastName.setVisibility(View.VISIBLE);
            tilMobile.setVisibility(View.VISIBLE);
            tilAddressPurok.setVisibility(View.VISIBLE);
            tilAddressBarangay.setVisibility(View.VISIBLE);
            tilAddressCity.setVisibility(View.VISIBLE);
            btnUpdateProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);
        });

        btnUpdateProfile.setOnClickListener(view -> {
            btnUpdateProfile.setEnabled(false);

            Map<String, Object> user = new HashMap<>();
            user.put("firstName", Objects.requireNonNull(etFirstName.getText()).toString());
            user.put("lastName", Objects.requireNonNull(etLastName.getText()).toString());
            user.put("mobile", Objects.requireNonNull(etMobile.getText()).toString());
            user.put("addressPurok", Objects.requireNonNull(etAddressPurok.getText()).toString());
            user.put("addressBarangay", Objects.requireNonNull(etAddressBarangay.getText()).toString());
            user.put("addressCity", Objects.requireNonNull(etAddressCity.getText()).toString());
            DB.collection("users").document(AUTH.getCurrentUser().getUid())
                    .update(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                requireDialog().dismiss();
                                Toast.makeText(requireContext(), "Updated Profile", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        });
    }
}
