package com.example.techworkshops.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techworkshops.R;
import com.example.techworkshops.model.Workshop;

import java.util.List;

public class DashboardScreenAdapter extends
        RecyclerView.Adapter<DashboardScreenAdapter.WorkshopsViewHolder> {

    private List<Workshop> workshops;

    public DashboardScreenAdapter(List<Workshop> workshops) {
        this.workshops = workshops;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.workshop_details;
    }

    @NonNull
    @Override
    public DashboardScreenAdapter.WorkshopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DashboardScreenAdapter.WorkshopsViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardScreenAdapter.WorkshopsViewHolder holder, int position) {
        holder.courseNameTxt.setText(workshops.get(position).getCourseName());
        holder.instructorNameTxt.setText("Instructor :" + workshops.get(position).getInstructorName());
        holder.descriptionTxt.setText(workshops.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return workshops.size();
    }

    class WorkshopsViewHolder extends RecyclerView.ViewHolder {

        TextView courseNameTxt, instructorNameTxt, descriptionTxt;
        Button applyButton;

        WorkshopsViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTxt = itemView.findViewById(R.id.course_name);
            instructorNameTxt = itemView.findViewById(R.id.course_instructor);
            descriptionTxt = itemView.findViewById(R.id.course_description);
            applyButton = itemView.findViewById(R.id.apply_button);
            applyButton.setVisibility(View.GONE);
        }
    }
}
