package com.homestaysiaton.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.homestaysiaton.Objects.ShopItem;
import com.homestaysiaton.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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

        String categoryId = shopItem.getCategoryId();
        double price = shopItem.getPrice();
        double maxGuests = shopItem.getMaxGuests();
        String unitDetails = shopItem.getUnitDetails();
        String unitName = shopItem.getUnitName();
        String address = shopItem.getAddressLine();
        int stock = shopItem.getStock();
        Long thumbnail = shopItem.getThumbnail();

        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvPrice.setText("₱"+df.format(price));
        holder.tvName.setText(unitName);
        holder.tvAddress.setText(address);
        holder.tvDetails.setText(unitDetails);
        holder.tvMaxGuests.setText("Max Guests: " + String.format("%.0f", maxGuests));

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
        RoundedImageView ivUnit;
        TextView tvName, tvAddress, tvDetails, tvMaxGuests, tvPrice;

        public shopItemViewHolder(@NonNull View itemView, OnShopItemListener onShopItemListener) {
            super(itemView);

            ivUnit = itemView.findViewById(R.id.ivUnit);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvMaxGuests = itemView.findViewById(R.id.tvMaxGuests);
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
