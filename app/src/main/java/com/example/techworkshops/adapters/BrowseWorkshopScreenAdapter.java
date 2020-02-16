package com.example.techworkshops.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techworkshops.R;
import com.example.techworkshops.fragments.BrowseWorkshopsFragment;
import com.example.techworkshops.model.Workshop;

import java.util.List;

public class BrowseWorkshopScreenAdapter extends RecyclerView.Adapter<BrowseWorkshopScreenAdapter.WorkshopsViewHolder> {

    private List<Workshop> workshops;
    private OnAppyListener appyListener;

    public BrowseWorkshopScreenAdapter(BrowseWorkshopsFragment fragment, List<Workshop> workshops) {
        this.workshops = workshops;
        appyListener = fragment;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.workshop_details;
    }

    @NonNull
    @Override
    public WorkshopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkshopsViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkshopsViewHolder holder, int position) {
        holder.courseNameTxt.setText(workshops.get(position).getCourseName());
        holder.instructorNameTxt.setText("Instructor :" + workshops.get(position).getInstructorName());
        holder.descriptionTxt.setText(workshops.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return workshops.size();
    }

    class WorkshopsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView courseNameTxt, instructorNameTxt, descriptionTxt;
        Button applyButton;

        WorkshopsViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTxt = itemView.findViewById(R.id.course_name);
            instructorNameTxt = itemView.findViewById(R.id.course_instructor);
            descriptionTxt = itemView.findViewById(R.id.course_description);
            applyButton = itemView.findViewById(R.id.apply_button);
            applyButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            appyListener.setOnApplyListener(workshops.get(getAdapterPosition()));
        }
    }

    public interface OnAppyListener {
        void setOnApplyListener(Workshop workshop);
    }
}
