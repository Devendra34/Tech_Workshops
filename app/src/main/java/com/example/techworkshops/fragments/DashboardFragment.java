package com.example.techworkshops.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techworkshops.R;
import com.example.techworkshops.adapters.DashboardScreenAdapter;
import com.example.techworkshops.model.AppliedWorkshop;
import com.example.techworkshops.model.Workshop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DashboardFragment extends Fragment {

    private TextView heading;
    private RecyclerView recyclerView;
    private View view;
    private AlertDialog spotsBox;

    private NavController navController;
    private String userId;
    private List<Workshop> appliedWorkshops;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        heading = view.findViewById(R.id.dashboard_heading);
        recyclerView = view.findViewById(R.id.dashboard_recycler_view);
        spotsBox = new SpotsDialog(getContext());
        recyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        if (firebaseAuth.getCurrentUser() == null) {
            navController.navigate(R.id.nav_log_in);
            makeSnackBar("Login Required");
        } else {
            loadAppliedWorkshops();
        }


    }

    private void loadAppliedWorkshops() {
        spotsBox.show();

        db.collection("applied_workshops")
                .whereEqualTo("student_email",firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            setApapter(false, "No Workshops are Selected");
                            spotsBox.cancel();
                        } else {
                            appliedWorkshops = new ArrayList<>();
                            final List<AppliedWorkshop> appliedList = queryDocumentSnapshots.toObjects(AppliedWorkshop.class);
                            AppliedWorkshop appliedWorkshop;
                            for(int i = 0; i<appliedList.size(); i++) {
                                final boolean []isLast = new boolean[1]; //by default false
                                if(i == appliedList.size() -1){
                                    isLast[0] = true;
                                }
                                appliedWorkshop = appliedList.get(i);
                                db.collection("workshops").whereEqualTo("id",appliedWorkshop.getWorkshop_id()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                appliedWorkshops.add(queryDocumentSnapshots.toObjects(Workshop.class).get(0));
                                                if(isLast[0]){
                                                    setApapter(true, "");
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                setApapter(false,"Failed: "+ e.getMessage());

                                            }
                                        });
                            }

                            spotsBox.cancel();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setApapter(false,e.toString());
                        spotsBox.cancel();
                    }
                });


    }

    private void setApapter(boolean isLoaded, String message) {

        if(isLoaded) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new DashboardScreenAdapter(appliedWorkshops));
        } else {
            makeSnackBar(message);
        }
    }

    private void makeSnackBar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
