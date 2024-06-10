package com.homestayhost.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestayhost.Dialogs.IdDialog;
import com.homestayhost.Dialogs.ProfileDialog;
import com.homestayhost.Objects.Booking;
import com.homestayhost.R;
import com.homestayhost.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.bookingViewHolder> {

    private final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final StorageReference STORAGE = FirebaseStorage.getInstance().getReference();

    Context context;
    ArrayList<Booking> arrBooking;
    private OnBookingListener mOnBookingListener;

    public BookingAdapter(Context context, ArrayList<Booking> arrBooking, OnBookingListener onBookingListener) {
        this.context = context;
        this.arrBooking = arrBooking;
        this.mOnBookingListener = onBookingListener;
    }

    @NonNull
    @Override
    public bookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_booking, parent, false);
        return new bookingViewHolder(view, mOnBookingListener);
    }

    @Override
    public void onBindViewHolder(@NonNull bookingViewHolder holder, int position) {
        Booking booking = arrBooking.get(position);

        String id = booking.getId();
        String unitId = booking.getUnitId();
        String userUid = booking.getUserUid();
        String hostUid = booking.getHostUid();
        long bookingEndDate = booking.getBookingEndDate();
        long bookingStartDate = booking.getBookingStartDate();
        String status = booking.getStatus();
        long timestamp = booking.getTimestamp();

        holder.tvStatus.setText(status);
        loadCustomerInfo(holder, userUid);
        loadUnitInfo(holder, unitId, bookingEndDate, bookingStartDate);

        SimpleDateFormat sdfTimestamp = new SimpleDateFormat("MMM d, yyyy");
        holder.tvTimestamp.setText(sdfTimestamp.format(timestamp));

        if (Objects.equals(status, "pending")) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnComplete.setVisibility(View.GONE);
        }
        else if (Objects.equals(status, "accepted")) {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnComplete.setVisibility(View.VISIBLE);
        }
        else if (Objects.equals(status, "completed")) {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnComplete.setVisibility(View.GONE);
        }
    }

    private void loadUnitInfo(bookingViewHolder holder, String unitId, long bookingEndDate, long bookingStartDate) {
        DB.collection("units").document(unitId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot unit = task.getResult();

                        holder.tvUnitName.setText(unit.getString("unitName"));

                        SimpleDateFormat sdfSchedule = new SimpleDateFormat("MMM d, yyyy");
                        holder.tvSchedule.setText(sdfSchedule.format(bookingStartDate) + " - " + sdfSchedule.format(bookingEndDate));

                        long diff = bookingEndDate - bookingStartDate;
                        DecimalFormat df = new DecimalFormat("0.00");
                        holder.tvTotal.setText("₱"+df.format(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) * unit.getDouble("price")));

                        STORAGE.child("units/"+unit.getLong("thumbnail")).getDownloadUrl()
                                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(500,0).centerInside().into(holder.ivUnit))
                                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(holder.ivUnit));
                    }
                });
    }

    private void loadCustomerInfo(bookingViewHolder holder, String userUid) {
        DB.collection("users").document(userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot user = task.getResult();

                            holder.tvName.setText(user.getString("firstName") + " " + user.getString("lastName"));
                            holder.tvEmail.setText(user.getString("email"));
                            holder.tvMobile.setText(user.getString("mobile"));

                            STORAGE.child("images/"+user.getString("idFileName")).getDownloadUrl()
                                    .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(500,0).centerInside().into(holder.ivId))
                                    .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(holder.ivId));
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrBooking.size();
    }

    public class bookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnBookingListener onBookingListener;
        RoundedImageView ivId, ivUnit;
        TextView tvStatus, tvName, tvEmail, tvMobile, tvUnitName, tvSchedule, tvTimestamp, tvTotal;
        MaterialButton btnBlock, btnAccept, btnCancel, btnComplete;

        public bookingViewHolder(@NonNull View itemView, OnBookingListener onBookingListener) {
            super(itemView);

            ivId = itemView.findViewById(R.id.ivId);
            ivUnit = itemView.findViewById(R.id.ivUnit);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvUnitName = itemView.findViewById(R.id.tvUnitName);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnBlock = itemView.findViewById(R.id.btnBlock);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnComplete = itemView.findViewById(R.id.btnComplete);

            this.onBookingListener = onBookingListener;
            itemView.setOnClickListener(this);

            ivId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userUid = arrBooking.get(getAdapterPosition()).getUserUid();
                    DB.collection("users").document(userUid)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot user = task.getResult();

                                    IdDialog idDialog = new IdDialog();
                                    Bundle args = new Bundle();
                                    args.putString("idFileName", user.getString("idFileName"));
                                    idDialog.setArguments(args);
                                    idDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "PROFILE_DIALOG");
                                }
                            });
                }
            });

            btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder dialogBlock = new MaterialAlertDialogBuilder((AppCompatActivity)context);
                    dialogBlock.setTitle("Block User");
                    dialogBlock.setMessage("Are you sure you want to block this guest? Once blocked, this guest will not be able to book your units.");
                    dialogBlock.setPositiveButton("Block User", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DB.collection("users").document(arrBooking.get(getAdapterPosition()).getUserUid())
                                    .update("blockedBy", FieldValue.arrayUnion(AUTH.getUid()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                DB.collection("units").document(arrBooking.get(getAdapterPosition()).getUnitId())
                                                        .update("isBooked", false, "bookingEndDate", 0)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        DB.collection("bookings").document(arrBooking.get(getAdapterPosition()).getId())
                                                                                .delete();
                                                                    }
                                                                });
                                            }
                                        }
                                    });
                        }
                    });
                    dialogBlock.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    });
                    dialogBlock.show();
                }
            });

            btnCancel.setOnClickListener(v -> DB.collection("bookings").document(arrBooking.get(getAdapterPosition()).getId())
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DB.collection("units").document(arrBooking.get(getAdapterPosition()).getUnitId())
                                        .update("isBooked", false, "bookingEndDate", 0);
                            }
                        }
                    }));

            btnAccept.setOnClickListener(v -> DB.collection("bookings").document(arrBooking.get(getAdapterPosition()).getId())
                    .update("status", "accepted"));

            btnComplete.setOnClickListener(v -> DB.collection("bookings").document(arrBooking.get(getAdapterPosition()).getId())
                    .update("status", "completed"));
        }

        @Override
        public void onClick(View view) {
            onBookingListener.onBookingClick(getAdapterPosition());
        }
    }

    public interface OnBookingListener{
        void onBookingClick(int position);
    }
}
