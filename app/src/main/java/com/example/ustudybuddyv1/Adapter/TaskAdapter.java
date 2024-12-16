package com.example.ustudybuddyv1.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Database.Task;
import com.example.ustudybuddyv1.Database.TaskDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;
    private final TaskDao taskDao;  // Assume TaskDao is injected for updating tasks

    public TaskAdapter(List<Task> tasks, TaskDao taskDao) {
        this.tasks = tasks;
        this.taskDao = taskDao;
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

        // Set progress for the SeekBar (task completion percentage)
        holder.taskCompletionSlider.setProgress(task.getCompletionPercentage());

        // Update the percentage text when the SeekBar value changes
        holder.completionPercentage.setText(task.getCompletionPercentage() + "%");

        // Add a listener to the SeekBar to update the task's completion percentage
        holder.taskCompletionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the task completion percentage in the model
                task.setCompletionPercentage(progress);

                // Update the percentage TextView
                holder.completionPercentage.setText(progress + "%");

                // Update the task in the database
                updateTaskInDatabase(task);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when tracking starts
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when tracking stops
            }
        });
    }

    private void updateTaskInDatabase(Task task) {
        // Update the task in the database
        new Thread(() -> taskDao.updateTask(task)).start();  // Assuming you have an update method in the DAO
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDueDate;
        View priorityIndicator;  // Add a View for the priority color
        SeekBar taskCompletionSlider;  // Add a SeekBar for task completion

        TextView completionPercentage;  // Add a TextView to display the percentage

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskDueDate = itemView.findViewById(R.id.task_due_date);
            priorityIndicator = itemView.findViewById(R.id.task_priority_indicator);  // Bind the priority indicator
            taskCompletionSlider = itemView.findViewById(R.id.task_completion_slider);  // Bind the SeekBar
            completionPercentage = itemView.findViewById(R.id.completion_percentage);  // Bind the percentage TextView
        }
    }
}
