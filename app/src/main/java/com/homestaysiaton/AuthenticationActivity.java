package com.homestaysiaton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homestaysiaton.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthenticationActivity extends AppCompatActivity {

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

    // global
    ConstraintLayout clLogo;

    // authentication
    ConstraintLayout clLogin;
    TextInputLayout tilLoginMobile;
    TextInputEditText etLoginMobile;
    MaterialButton btnGotoSignup, btnLogin, btnSkip;

    // registration
    ConstraintLayout clSignup;
    TextInputEditText etSignupFirstName, etSignupLastName, etSignupMobile, etAddressPurok, etAddressBarangay, etAddressCity;
    RoundedImageView imgId;
    MaterialButton btnUploadId, btnGotoLogin, btnSignup;

    // verification
    ConstraintLayout clVerification;
    TextInputLayout tilVerificationCode;
    TextInputEditText etVerificationCode;
    MaterialButton btnVerify;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks loginCallback, signupCallback;

    Uri uriSelected = null;
    Uri uriLoaded = null;
    boolean EDIT_MODE = false;
    int SELECT_IMAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initializeFirebase();
        initializeViews();
        initializeActivityResultLauncher();
        initializeCallbacks();
        handleClickListeners();
    }

    private void initializeViews() {
        // global
        clLogo = findViewById(R.id.clLogo);

        // authentication
        clLogin = findViewById(R.id.clLogin);
        tilLoginMobile = findViewById(R.id.tilLoginMobile);
        etLoginMobile = findViewById(R.id.etLoginMobile);
        btnGotoSignup = findViewById(R.id.btnGotoSignup);
        btnLogin = findViewById(R.id.btnLogin);
        btnSkip = findViewById(R.id.btnSkip);

        // registration
        clSignup = findViewById(R.id.clSignup);
        etSignupFirstName = findViewById(R.id.etSignupFirstName);
        etSignupLastName = findViewById(R.id.etSignupLastName);
        etSignupMobile = findViewById(R.id.etSignupMobile);
        etAddressPurok = findViewById(R.id.etAddressPurok);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etAddressCity = findViewById(R.id.etAddressCity);
        imgId = findViewById(R.id.imgId);
        btnUploadId = findViewById(R.id.btnUploadId);
        btnGotoLogin = findViewById(R.id.btnGotoLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // verification
        clVerification = findViewById(R.id.clVerification);
        tilVerificationCode = findViewById(R.id.tilVerificationCode);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);
    }

    private void initializeCallbacks() {
        loginCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                clLogin.setVisibility(View.VISIBLE);
                clSignup.setVisibility(View.GONE);
                clVerification.setVisibility(View.GONE);

                btnLogin.setEnabled(true);
                btnSignup.setEnabled(true);

                Utils.simpleDialog(AuthenticationActivity.this, "Mobile Verification Failed", e.getLocalizedMessage(), "Try Again");
                Log.d("DEBUG", "Verification Failed: "+e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                clLogin.setVisibility(View.GONE);
                clSignup.setVisibility(View.GONE);
                clVerification.setVisibility(View.VISIBLE);

                btnVerify.setEnabled(true);

                btnVerify.setOnClickListener(view -> {
                    btnVerify.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, etVerificationCode.getText().toString());

                    AUTH.signInWithCredential(credential).addOnSuccessListener(authResult -> {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("DEBUG", "signInWithCredential:success");
                        // Update UI
                        tilVerificationCode.setError(null);

                        DB.collection("users").document(AUTH.getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot user = task.getResult();

                                            if (user.getLong("userType") == 0) {
                                                Toast.makeText(AuthenticationActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                                finish();
                                            }
                                            else {
                                                AUTH.signOut();
                                                Utils.simpleDialog(AuthenticationActivity.this, "Login Failed", "The account you are trying to log in is not a regular user account. Please use the Homestay Hosts app for host users.", "I Understand");
                                            }
                                        }
                                    }
                                });
                    }).addOnFailureListener(e -> {
                        btnVerify.setEnabled(true);

                        // Sign in failed, display a message and update the UI
                        Log.d("DEBUG", "signInWithCredential:failure", e);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid

                            tilVerificationCode.setError("The verification code entered was invalid");
                        }
                    });
                });
            }
        };

        signupCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                clLogin.setVisibility(View.GONE);
                clSignup.setVisibility(View.VISIBLE);
                clVerification.setVisibility(View.GONE);

                btnLogin.setEnabled(true);
                btnSignup.setEnabled(true);

                Utils.simpleDialog(AuthenticationActivity.this, "Mobile Verification Failed", e.getLocalizedMessage(), "Try Again");
                Log.d("DEBUG", "Verification Failed: "+e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                clLogin.setVisibility(View.GONE);
                clSignup.setVisibility(View.GONE);
                clVerification.setVisibility(View.VISIBLE);

                btnVerify.setEnabled(true);

                btnVerify.setOnClickListener(view -> {
                    btnVerify.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, etVerificationCode.getText().toString());

                    AUTH.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                tilVerificationCode.setError(null);
                                Toast.makeText(AuthenticationActivity.this, "Creating new account", Toast.LENGTH_SHORT).show();

                                String uid = task.getResult().getUser().getUid();
                                String idFileName = String.valueOf(System.currentTimeMillis());

                                Map<String, Object> user = new HashMap<>();
                                user.put("uid", uid);
                                user.put("firstName", Objects.requireNonNull(etSignupFirstName.getText()).toString());
                                user.put("lastName", Objects.requireNonNull(etSignupLastName.getText()).toString());
                                user.put("addressPurok", Objects.requireNonNull(etAddressPurok.getText()).toString());
                                user.put("addressBarangay", Objects.requireNonNull(etAddressBarangay.getText()).toString());
                                user.put("addresCity", Objects.requireNonNull(etAddressCity.getText()).toString());
                                user.put("idFileName", Objects.requireNonNull(idFileName));
                                user.put("mobile", "+63" + Objects.requireNonNull(etSignupMobile.getText()).toString());
                                user.put("userType", 0);

                                StorageReference bannerReference = STORAGE.getReference().child("images/"+idFileName);
                                bannerReference.putFile(uriSelected).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Utils.simpleDialog(AuthenticationActivity.this, "Failed to Upload ID", task.getException().getLocalizedMessage(), "Try Again");
                                            clVerification.setVisibility(View.GONE);
                                            clSignup.setVisibility(View.VISIBLE);
                                        }
                                        else if (task.isSuccessful()) {
                                            DB.collection("users").document(uid)
                                                    .set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(AuthenticationActivity.this, "Creating account...", Toast.LENGTH_SHORT).show();

                                                                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                                                finish();
                                                            } else {
                                                                Utils.simpleDialog(AuthenticationActivity.this, "Authentication Failed", task.getException().getLocalizedMessage(), "Try Again");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                            else {
                                btnVerify.setEnabled(true);

                                // Sign in failed, display a message and update the UI
                                Log.d("DEBUG", "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid

                                    tilVerificationCode.setError("The verification code entered was invalid");
                                }
                            }
                        });
                });
            }
        };
    }

    private void handleClickListeners() {
        btnUploadId.setOnClickListener(view -> {
            selectImageFromDevice();
        });

        btnGotoSignup.setOnClickListener(view -> {
            clLogin.setVisibility(View.GONE);
            clSignup.setVisibility(View.VISIBLE);
        });

        btnGotoLogin.setOnClickListener(view -> {
            clLogin.setVisibility(View.VISIBLE);
            clSignup.setVisibility(View.GONE);
        });

        btnSkip.setOnClickListener(view -> {
            Toast.makeText(AuthenticationActivity.this, "Signed in as Guest", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            Utils.hideKeyboard(this);

            btnLogin.setEnabled(false);
            String phone = "+63" + Objects.requireNonNull(etLoginMobile.getText());

            /*String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
            String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

            if (TextUtils.isEmpty(email)) {
                Utils.simpleDialog(this, "Login Failed","Please enter your email.", "Okay");
                btnLogin.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Utils.simpleDialog(this, "Login Failed","Please enter a password.", "Okay");
                btnLogin.setEnabled(true);
                return;
            }*/

            DB.collection("users").whereEqualTo("mobile", phone)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.isEmpty()) {
                            tilLoginMobile.setError("The number entered is not connected to an account.");
                            btnLogin.setEnabled(true);
                            return;
                        }
                        tilLoginMobile.setError(null);

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(AUTH)
                                        .setPhoneNumber(phone)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(this)                 // Activity (for callback binding)
                                        .setCallbacks(loginCallback)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    });

            /*AUTH.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, "Signed in as "+email, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            Utils.simpleDialog(this, "Login Failed", "Invalid email or password.", "Back");
                            btnLogin.setEnabled(true);
                        }
                    });*/
        });

        btnSignup.setOnClickListener(view -> {
            Utils.hideKeyboard(this);

            btnSignup.setEnabled(false);

            if (uriSelected == null) {
                Utils.simpleDialog(this, "ID Required", "Please upload a government-issued identification card.", "Okay");
                btnSignup.setEnabled(true);
                return;
            }

            String firstname = Objects.requireNonNull(etSignupFirstName.getText()).toString();
            String lastname = Objects.requireNonNull(etSignupLastName.getText()).toString();
            String phone = "+63" + Objects.requireNonNull(etSignupMobile.getText());

            if (TextUtils.isEmpty(firstname) ||
                    TextUtils.isEmpty(lastname)
            ) {
                Utils.simpleDialog(this, "Missing Fields", "All fields are required for registration.", "Okay");
                btnSignup.setEnabled(true);
                return;
            }

            if (etSignupMobile.getText().toString().length() <= 9) {
                Utils.simpleDialog(this, "Invalid Phone Number", "Please enter a valid phone number", "Okay");
                btnSignup.setEnabled(true);
                return;
            }

            DB.collection("users").whereEqualTo("mobile", "+63"+etSignupMobile.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                            if (!task.getResult().isEmpty()) {
                                btnSignup.setEnabled(true);
                                Utils.simpleDialog(AuthenticationActivity.this, "Mobile Number Exists", "This mobile number is already in use. Please try registering using a different mobile number.", "Okay");
                            }
                            else {
                                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(AUTH)
                                        .setPhoneNumber(phone)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(getParent())                 // Activity (for callback binding)
                                        .setCallbacks(signupCallback)          // OnVerificationStateChangedCallbacks
                                        .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                            }
                        }
                    });
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
                        Picasso.get().load(uriRetrieved).resize(800,0).centerCrop().into(imgId);
                    }
                }
        );
    }
}