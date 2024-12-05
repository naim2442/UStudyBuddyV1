package com.example.ustudybuddyv1.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ustudybuddyv1.R;
import com.example.ustudybuddyv1.Adapter.TaskAdapter;
import com.example.ustudybuddyv1.Database.Task;
import com.example.ustudybuddyv1.Database.TaskDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ToDoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskDatabase taskDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "default_user";

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize database and task list
        taskDatabase = TaskDatabase.getInstance(getContext());
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);

        recyclerView.setAdapter(taskAdapter);

        // Load tasks from database
        loadTasks();

        // Floating Action Button to add a new task
        FloatingActionButton fabAddTask = view.findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(v -> openAddTaskDialog());

        // Floating Action Button to sort tasks
        FloatingActionButton fabSort = view.findViewById(R.id.fab_sort);

        fabSort.setOnClickListener(v -> {
            // Create and show the PopupMenu
            PopupMenu popupMenu = new PopupMenu(getContext(), fabSort);
            getActivity().getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());

            // Handle item selection
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.action_sort_priority) {
                        sortTasksByPriority();
                        return true;
                    } else if (id == R.id.action_sort_deadline) {
                        sortTasksByDeadline();
                        return true;
                    } else if (id == R.id.action_sort_oldest) {
                        sortTasksByOldest();
                        return true;
                    } else if (id == R.id.action_sort_latest) {
                        sortTasksByLatest();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            // Show the menu
            popupMenu.show();
        });




        // Enable swipe-to-delete
        enableSwipeToDelete();

        return view;
    }

    private void loadTasks() {
        new Thread(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user != null ? user.getUid() : "default_user"; // Replace with the actual user ID

            taskList.clear();
            taskList.addAll(taskDatabase.taskDao().getTasksByUser(userId)); // Fetch tasks based on user ID
            getActivity().runOnUiThread(this::refreshRecyclerView);
        }).start();
    }

    private void openAddTaskDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);

        // Find the views from the dialog layout
        EditText editTitle = dialogView.findViewById(R.id.edit_task_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_task_description);
        Button buttonDueDate = dialogView.findViewById(R.id.button_due_date);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinner_priority);

        // Create an array of priority options (with colors)
        String[] priorities = {"High", "Medium", "Low"};
        int[] priorityColors = {Color.RED, Color.YELLOW, Color.GREEN};

        // Create an adapter to set the priority options with colors
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, priorities) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(priorityColors[position]); // Set color based on the priority
                return tv;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(priorityColors[position]); // Set color based on the priority
                return tv;
            }
        };

        // Set the adapter for the spinner
        spinnerPriority.setAdapter(adapter);

        // Initialize date and time picker variables
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Set up the DatePicker and TimePicker Dialog
        buttonDueDate.setOnClickListener(v -> {
            // Date Picker
            new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                // Time Picker
                new TimePickerDialog(getContext(), (timeView, hourOfDay, minuteOfHour) -> {
                    // Display selected date and time
                    String selectedDateTime = String.format("%02d-%02d-%04d %02d:%02d",
                            dayOfMonth, monthOfYear + 1, year1, hourOfDay, minuteOfHour);
                    buttonDueDate.setText(selectedDateTime);

                    // Save the selected date and time in milliseconds
                    calendar.set(year1, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0);
                    long dueDateMillis = calendar.getTimeInMillis();
                    // Do something with the selected due date
                }, hour, minute, true).show();
            }, year, month, day).show();
        });

        // Show the dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Task title cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get selected priority (High, Medium, Low)
                    String selectedPriority = (String) spinnerPriority.getSelectedItem();

                    String taskId = UUID.randomUUID().toString();  // Generate a unique task ID
                    // Get the current user's ID
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = user != null ? user.getUid() : "default_user";  // Replace with the actual user ID


                    // If due date is selected, use it; otherwise, create task without due date
                    long dueDateMillis = 0;  // Default value for no due date
                    if (!buttonDueDate.getText().toString().equals("Select Due Date")) {
                        // Use selected date and time
                        dueDateMillis = calendar.getTimeInMillis();
                    }

                    Task newTask = new Task(taskId, title, description, dueDateMillis, selectedPriority, false, false, userId); // Add userId

                    new Thread(() -> {
                        taskDatabase.taskDao().insert(newTask);
                        loadTasks(); // Refresh the task list
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = taskList.get(position);

                new Thread(() -> {
                    taskDatabase.taskDao().delete(taskToDelete);
                    taskList.remove(position);
                    getActivity().runOnUiThread(() -> taskAdapter.notifyItemRemoved(position));
                }).start();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void sortTasksByPriority() {
        Collections.sort(taskList, (task1, task2) -> {
            return task2.getPriority().compareTo(task1.getPriority()); // High to Low
        });
        refreshRecyclerView();
    }

    private void sortTasksByDeadline() {
        Collections.sort(taskList, (task1, task2) ->
                Long.compare(task1.getDueDate(), task2.getDueDate())); // Ascending (Oldest first)
        refreshRecyclerView();
    }

    private void sortTasksByLatest() {
        Collections.sort(taskList, (task1, task2) -> Long.compare(task2.getDueDate(), task1.getDueDate())); // Descending (Newest first)
        refreshRecyclerView();
    }

    private void sortTasksByOldest() {
        Collections.sort(taskList, (task1, task2) -> Long.compare(task1.getDueDate(), task2.getDueDate())); // Ascending (Oldest first)
        refreshRecyclerView();
    }


    // Refresh RecyclerView
    private void refreshRecyclerView() {
        taskAdapter.notifyDataSetChanged();
    }


}
