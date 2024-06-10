package com.homestayhost.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestayhost.Objects.User;
import com.homestayhost.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.blockedUsersViewHolder> {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    ArrayList<User> arrBlockedUsers;
    private OnBlockedUsersListener mOnBlockedUsersListener;

    public BlockedUsersAdapter(Context context, ArrayList<User> arrBlockedUsers, OnBlockedUsersListener onBlockedUsersListener) {
        this.context = context;
        this.arrBlockedUsers = arrBlockedUsers;
        this.mOnBlockedUsersListener = onBlockedUsersListener;
    }

    @NonNull
    @Override
    public blockedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_blocked_user, parent, false);
        return new blockedUsersViewHolder(view, mOnBlockedUsersListener);
    }

    @Override
    public void onBindViewHolder(@NonNull blockedUsersViewHolder holder, int position) {
        User blockedUser = arrBlockedUsers.get(position);

        holder.tvName.setText(blockedUser.getFirstName() + " " + blockedUser.getLastName());
        holder.tvMobile.setText(blockedUser.getMobile());
        holder.tvAddress.setText(blockedUser.getAddressPurok() + ", " + blockedUser.getAddressBarangay() + ", " + blockedUser.getAddresCity());
    }

    @Override
    public int getItemCount() {
        return arrBlockedUsers.size();
    }

    public class blockedUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnBlockedUsersListener onBlockedUsersListener;
        MaterialButton btnUnblock;
        TextView tvName, tvAddress, tvMobile;

        public blockedUsersViewHolder(@NonNull View itemView, OnBlockedUsersListener onBlockedUsersListener) {
            super(itemView);

            btnUnblock = itemView.findViewById(R.id.btnUnblock);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvMobile = itemView.findViewById(R.id.tvMobile);

            this.onBlockedUsersListener = onBlockedUsersListener;
            btnUnblock.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBlockedUsersListener.onBlockedUsersClick(getAdapterPosition());
        }
    }

    public interface OnBlockedUsersListener{
        void onBlockedUsersClick(int position);
    }
}
