package com.homestayhost.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestayhost.AuthenticationActivity;
import com.homestayhost.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IdDialog extends AppCompatDialogFragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    StorageReference STORAGE;
    Query qryUser;

    private void initializeFirebase() {
        STORAGE = FirebaseStorage.getInstance().getReference();
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    ImageView ivId;
    MaterialButton btnClose;
    String idFileName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_id, null);

        initializeFirebase();
        initiate(view);
        buttonHandler();
        loadId();

        builder.setView(view);
        return builder.create();
    }

    private void loadId() {
        STORAGE.child("images/"+idFileName).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(500,0).centerInside().into(ivId))
                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(ivId));
    }

    private void initiate(View view) {
        ivId = view.findViewById(R.id.ivId);
        btnClose = view.findViewById(R.id.btnClose);

        idFileName = getArguments().getString("idFileName");
    }

    private void buttonHandler() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
