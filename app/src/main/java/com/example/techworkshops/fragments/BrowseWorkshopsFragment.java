package com.example.techworkshops.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techworkshops.R;
import com.example.techworkshops.adapters.BrowseWorkshopScreenAdapter;
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

public class BrowseWorkshopsFragment extends Fragment implements BrowseWorkshopScreenAdapter.OnAppyListener {

    private RecyclerView recyclerView;
    private View view;
    private NavController navController;
    private AlertDialog spotsBox;

    private static final String TAG = "BrowseWorkshopsFragment";
    private List<Workshop> workshops;
    private List<AppliedWorkshop> appliedWorkshopsList;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse_workshops, container, false);
        recyclerView = view.findViewById(R.id.all_workshops_recycler_view);
        recyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        loadData();
    }

    @Override
    public void setOnApplyListener(final Workshop workshop) {

        if (firebaseAuth.getCurrentUser() == null) {
            makeSnackbar("Login Required");
            navController.navigate(R.id.nav_log_in);
            return;
        }

        for (AppliedWorkshop appliedWorkshop : appliedWorkshopsList) {
            if (appliedWorkshop.getWorkshop_id().equals(workshop.getId())) {
                makeSnackbar("Already Applied to " + workshop.getCourseName());
                return;
            }
        }

        AppliedWorkshop appliedWorkshop = new AppliedWorkshop(
                firebaseAuth.getCurrentUser().getEmail(), workshop.getId()
        );
        spotsBox.show();
        db.collection("applied_workshops").document()
                .set(appliedWorkshop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeSnackbar(workshop.getCourseName() + " Added !");
                        appliedWorkshopsList.add(new AppliedWorkshop(
                                firebaseAuth.getCurrentUser().getEmail(),
                                workshop.getId()));
                        spotsBox.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spotsBox.cancel();
                        makeSnackbar("Workshop not added: " + e.getMessage());
                    }
                });
    }

    private void loadData() {

        spotsBox = new SpotsDialog(getContext());
        workshops = new ArrayList<>();
        spotsBox.show();
        db.collection("workshops").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: empty");
                            spotsBox.cancel();
                            setAdapter(false);
                            return;
                        }
                        workshops = queryDocumentSnapshots.toObjects(Workshop.class);
                        Log.d(TAG, "onSuccess: loaded, length " + String.valueOf(workshops.size()));
                        setAdapter(true);
                        spotsBox.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                        setAdapter(false);
                        spotsBox.cancel();
                    }
                });
        appliedWorkshopsList = new ArrayList<>();
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("applied_workshops")
                    .whereEqualTo("student_email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                appliedWorkshopsList = queryDocumentSnapshots.toObjects(AppliedWorkshop.class);
                            }
                        }
                    });
        }
    }

    private void setAdapter(boolean loaded) {
        if (loaded) {
            Log.d(TAG, "setAdapter: length " + workshops.size());
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new BrowseWorkshopScreenAdapter(BrowseWorkshopsFragment.this, workshops));
        } else {
            makeSnackbar("Failed to load Data");
        }

    }

    private void makeSnackbar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
