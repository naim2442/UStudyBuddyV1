package com.example.ustudybuddyv1.Adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Database.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getDescription());
        // Format the due date from long (timestamp) to a readable string
        long dueDateMillis = task.getDueDate();
        if (dueDateMillis != 0) {  // Check if there's a valid due date (non-zero)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());  // Change format if needed
            String formattedDueDate = dateFormat.format(new Date(dueDateMillis));
            holder.taskDueDate.setText(formattedDueDate);  // Set formatted date in the dueDate TextView
        } else {
            holder.taskDueDate.setText("No due date");  // If no due date, show a fallback message
        }
        // Set priority color
        switch (task.getPriority()) {
            case "High":
                holder.priorityIndicator.setBackgroundColor(Color.RED); // Red for High priority
                break;
            case "Medium":
                holder.priorityIndicator.setBackgroundColor(Color.YELLOW); // Yellow for Medium priority
                break;
            case "Low":
                holder.priorityIndicator.setBackgroundColor(Color.GREEN); // Green for Low priority
                break;
            default:
                holder.priorityIndicator.setBackgroundColor(Color.GRAY); // Default to Gray if no priority
                break;
        }
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDueDate;
        View priorityIndicator;  // Add a View for the priority color

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskDueDate = itemView.findViewById(R.id.task_due_date);
            priorityIndicator = itemView.findViewById(R.id.task_priority_indicator);  // Bind the priority indicator
        }
    }
}
