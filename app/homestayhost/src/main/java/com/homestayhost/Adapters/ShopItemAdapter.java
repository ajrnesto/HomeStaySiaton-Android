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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homestayhost.Objects.ShopItem;
import com.homestayhost.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.shopItemViewHolder> {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    ArrayList<ShopItem> arrShopItem;
    private OnShopItemListener mOnShopItemListener;

    public ShopItemAdapter(Context context, ArrayList<ShopItem> arrShopItem, OnShopItemListener onShopItemListener) {
        this.context = context;
        this.arrShopItem = arrShopItem;
        this.mOnShopItemListener = onShopItemListener;
    }

    @NonNull
    @Override
    public shopItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_shop_item, parent, false);
        return new shopItemViewHolder(view, mOnShopItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull shopItemViewHolder holder, int position) {
        ShopItem shopItem = arrShopItem.get(position);

        String unitId = shopItem.getId();
        double price = shopItem.getPrice();
        String unitDetails = shopItem.getUnitDetails();
        String unitName = shopItem.getUnitName();
        String address = shopItem.getAddressLine();
        int stock = shopItem.getStock();
        Long thumbnail = shopItem.getThumbnail();
        boolean isBooked = shopItem.isIsBooked();
        long bookingEndDate = shopItem.getBookingEndDate();

        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvPrice.setText("₱"+df.format(price));
        holder.tvName.setText(unitName);
        holder.tvAddress.setText(address);
        holder.tvDetails.setText(unitDetails);

        Log.d("DEBUG", unitName + ": " + isBooked);
        if (isBooked) {
            if (bookingEndDate == 0 || bookingEndDate < System.currentTimeMillis()) {
                // if booking end date has passed already
                FirebaseFirestore.getInstance().collection("units").document(unitId)
                        .update("isBooked", false,
                                "bookingEndDate", 0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                            }
                        });
            }
            else {
                holder.cvStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#DE6E4B")));
                holder.tvStatus.setText("Booked until " + new SimpleDateFormat("MMM dd").format(bookingEndDate));
            }
        }
        else {
            holder.cvStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#2FBF71")));
            holder.tvStatus.setText("Available");
        }

        storageRef.child("units/"+thumbnail).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(500,0).centerInside().into(holder.ivUnit))
                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(holder.ivUnit));
    }

    @Override
    public int getItemCount() {
        return arrShopItem.size();
    }

    public class shopItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnShopItemListener onShopItemListener;
        MaterialCardView cvStatus;
        RoundedImageView ivUnit;
        TextView tvStatus, tvName, tvAddress, tvDetails, tvPrice;

        public shopItemViewHolder(@NonNull View itemView, OnShopItemListener onShopItemListener) {
            super(itemView);

            cvStatus = itemView.findViewById(R.id.cvStatus);
            ivUnit = itemView.findViewById(R.id.ivUnit);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            this.onShopItemListener = onShopItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onShopItemListener.onShopItemClick(getAdapterPosition());
        }
    }

    public interface OnShopItemListener{
        void onShopItemClick(int position);
    }
}
