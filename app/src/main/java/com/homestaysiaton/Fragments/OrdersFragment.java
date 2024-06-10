package com.homestaysiaton.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.homestaysiaton.Adapters.OrderAdapter;
import com.homestaysiaton.Objects.Order;
import com.homestaysiaton.Objects.Unit;
import com.homestaysiaton.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;

    TabLayout tabOrders;
    RecyclerView rvOrders;

    ArrayList<Order> arrOrders;
    ArrayList<Unit> arrOrderItems;
    OrderAdapter orderAdapter;
    OrderAdapter.OnOrderListener onOrderListener = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_orders, container, false);

        initializeFirebase();
        initializeViews();
        loadRecyclerView(tabOrders.getSelectedTabPosition());

        tabOrders.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRecyclerView(tabOrders.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void loadRecyclerView(int tabIndex) {
        arrOrders = new ArrayList<>();
        arrOrderItems = new ArrayList<>();
        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setHasFixedSize(true);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        String statusFilter = "";
        /*if (tabIndex == 0) {
            statusFilter = "Pending";
        }
        else if (tabIndex == 1) {
            statusFilter = "Preparing";
        }
        else if (tabIndex == 2) {
            statusFilter = "Ready for Pick-up";
        }
        else if (tabIndex == 3) {
            statusFilter = "In Transit";
        }
        else if (tabIndex == 4) {
            statusFilter = "Delivered/Picked-up";
        }
        else if (tabIndex == 5) {
            statusFilter = "Failed Delivery";
        }*/
        if (tabIndex == 0) {
            statusFilter = "Pending";
        }
        else if (tabIndex == 1) {
            statusFilter = "Preparing";
        }
        else if (tabIndex == 2) {
            statusFilter = "Ready for Pick-up";
        }
        else if (tabIndex == 3) {
            statusFilter = "Delivered/Picked-up";
        }

        Query qryMyOrders = DB.collection("orders")
                        .whereEqualTo("customer", Objects.requireNonNull(AUTH.getCurrentUser()).getUid())
                        .whereEqualTo("status", statusFilter)
                        .orderBy("timestamp", Query.Direction.DESCENDING);

        qryMyOrders.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        arrOrders.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            String id = (String) doc.getData().get("id");
                            String customer = (String) doc.getData().get("customer");
                            ArrayList<String> deliveryAddress = (ArrayList<String>) doc.getData().get("deliveryAddress");
                            String deliveryOption = (String) doc.getData().get("deliveryOption");
                            String status = (String) doc.getData().get("status");
                            long timestamp = Long.parseLong(doc.getData().get("timestamp").toString());
                            double total = Double.parseDouble(doc.getData().get("total").toString());
                            ArrayList<Unit> arrOrderItems = new ArrayList<>();

                            arrOrders.add(new Order(
                                    id,
                                    customer,
                                    deliveryAddress,
                                    deliveryOption,
                                    status,
                                    timestamp,
                                    total,
                                    arrOrderItems
                            ));

                            DB.collection("orders").document(id)
                                    .collection("orderItems")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot item : task.getResult()) {

                                                String unitId = (String) item.getData().get("unitId");
                                                int quantity = Integer.parseInt(item.getData().get("quantity").toString());

                                                DB.collection("units").document(unitId)
                                                        .get()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                DocumentSnapshot unit = task1.getResult();
                                                                
                                                                /*if (unit == null) {
                                                                    return;
                                                                }*/

                                                                if (unit.getData() == null) {
                                                                    arrOrderItems.add(new Unit(
                                                                            "-1",
                                                                            0,
                                                                            "Deleted Item",
                                                                            "",
                                                                            0,
                                                                            null
                                                                    ));
                                                                    orderAdapter.notifyDataSetChanged();

                                                                    return;
                                                                }

                                                                String unitName = (String) unit.getData().get("unitName");
                                                                String unitDetails = (String) unit.getData().get("unitDetails");
                                                                double price = Double.parseDouble(unit.getData().get("price").toString());

                                                                Long thumbnail = null;
                                                                if (unit.getData().get("thumbnail") != null) {
                                                                    thumbnail = Long.parseLong(unit.getData().get("thumbnail").toString());
                                                                }

                                                                arrOrderItems.add(new Unit(
                                                                        unitId,
                                                                        quantity,
                                                                        unitName,
                                                                        unitDetails,
                                                                        price,
                                                                        thumbnail
                                                                ));

                                                                orderAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }

                        if (arrOrders.isEmpty()) {
                            rvOrders.setVisibility(View.INVISIBLE);
                        }
                        else {
                            rvOrders.setVisibility(View.VISIBLE);
                        }
                    }
                });

        orderAdapter = new OrderAdapter(requireContext(), arrOrders, onOrderListener);
        rvOrders.setAdapter(orderAdapter);
    }

    private void initializeViews() {
        tabOrders = view.findViewById(R.id.tabOrders);
        rvOrders = view.findViewById(R.id.rvOrders);
    }

    @Override
    public void onOrderClick(int position) {
        
    }
}