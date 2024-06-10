package com.homestaysiaton.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.homestaysiaton.Objects.Order;
import com.homestaysiaton.Objects.Unit;
import com.homestaysiaton.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.orderViewHolder> {

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    ArrayList<Order> arrOrder;
    OrderItemAdapter orderItemsAdapter;
    private OnOrderListener mOnOrderListener;

    public OrderAdapter(Context context, ArrayList<Order> arrOrder, OnOrderListener onOrderListener) {
        this.context = context;
        this.arrOrder = arrOrder;
        this.mOnOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_order, parent, false);
        return new orderViewHolder(view, mOnOrderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull orderViewHolder holder, int position) {
        Order order = arrOrder.get(position);

        String id = order.getId();
        ArrayList<String> deliveryAddress = order.getDeliveryAddress();
        String deliveryOption = order.getDeliveryOption();
        String status = order.getStatus();
        long timestamp = order.getTimestamp();
        double total = order.getTotal();
        ArrayList<Unit> arrOrderItems = order.getArrOrderItems();

        holder.tvDeliveryAddress.setText("Delivery Address: "+String.join(", ", deliveryAddress));
        holder.tvDeliveryOption.setText("Delivery Option: "+deliveryOption);
        holder.tvStatus.setText(status);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, hh:mm aa");
        holder.tvTimestamp.setText(sdf.format(timestamp));

        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvTotal.setText("₱"+df.format(total));

        holder.rvOrderItems.setHasFixedSize(true);
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        orderItemsAdapter = new OrderItemAdapter(holder.itemView.getContext(), arrOrderItems);
        holder.rvOrderItems.setAdapter(orderItemsAdapter);
    }

    @Override
    public int getItemCount() {
        return arrOrder.size();
    }

    public class orderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnOrderListener onOrderListener;
        AppCompatImageView ivUnit;
        TextView tvTimestamp, tvStatus, tvDeliveryOption, tvDeliveryAddress, tvTotal;
        RecyclerView rvOrderItems;

        public orderViewHolder(@NonNull View itemView, OnOrderListener onOrderListener) {
            super(itemView);

            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeliveryOption = itemView.findViewById(R.id.tvDeliveryOption);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
            tvTotal = itemView.findViewById(R.id.tvNightlyFee);

            this.onOrderListener = onOrderListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onOrderListener.onOrderClick(getAdapterPosition());
        }
    }

    public interface OnOrderListener{
        void onOrderClick(int position);
    }
}
