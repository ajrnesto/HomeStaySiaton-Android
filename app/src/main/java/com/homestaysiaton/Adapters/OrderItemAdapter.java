package com.homestaysiaton.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.homestaysiaton.Objects.Unit;
import com.homestaysiaton.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.orderItemViewHolder> {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    ArrayList<Unit> arrOrderItems;

    public OrderItemAdapter(Context context, ArrayList<Unit> arrOrderItems) {
        this.context = context;
        this.arrOrderItems = arrOrderItems;
    }

    @NonNull
    @Override
    public orderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_order_item, parent, false);
        return new orderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull orderItemViewHolder holder, int position) {
        Unit orderItem = arrOrderItems.get(position);

        if (orderItem.getId() == "-1") {
            Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(holder.ivUnit);
            holder.tvName.setText("Deleted Item");
            holder.tvPrice.setText("₱--.--");
            holder.tvQuantity.setText("x--");
            holder.tvDetails.setText("Deleted Item");
            holder.tvSubtotal.setText("₱--.--");
            return;
        }

        String unitId = orderItem.getId();
        int quantity = orderItem.getQuantity();
        String unitName = orderItem.getUnitName();
        String unitDetails = orderItem.getUnitDetails();
        double price = orderItem.getPrice();
        Long thumbnail = orderItem.getThumbnail();

        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvPrice.setText("₱"+df.format(price));
        holder.tvQuantity.setText("x"+quantity);
        holder.tvName.setText(unitName);
        holder.tvDetails.setText(unitDetails);

        double subtotal = Float.parseFloat(String.valueOf(quantity)) * price;
        holder.tvSubtotal.setText("₱"+df.format(subtotal));

        storageRef.child("units/"+thumbnail).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(120,120).centerInside().into(holder.ivUnit))
                .addOnFailureListener(e -> Picasso.get().load("https://via.placeholder.com/150?text=No+Image").fit().into(holder.ivUnit));
    }

    @Override
    public int getItemCount() {
        return arrOrderItems.size();
    }

    public class orderItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView ivUnit;
        TextView tvName, tvDetails, tvPrice, tvQuantity, tvSubtotal;

        public orderItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUnit = itemView.findViewById(R.id.ivUnit);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
