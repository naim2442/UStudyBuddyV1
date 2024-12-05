//package com.example.ustudybuddyv1.Fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Observer;
//import com.example.ustudybuddyv1.Database.Task;
//import com.example.ustudybuddyv1.Database.TaskDao;
//import com.example.ustudybuddyv1.Database.TaskDatabase;
//import com.example.ustudybuddyv1.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import java.util.List;
//
//public class DashboardUserFragment extends Fragment {
//
//    private TextView totalGroupsJoinedText;
//    private TextView upcomingGroupSessionsText;
//    private TextView totalToDoListText;
//    private TaskDao taskDao;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_dashboard_user, container, false);
//
//        // Initialize TextViews
//        totalGroupsJoinedText = rootView.findViewById(R.id.totalGroupsJoined);
//        upcomingGroupSessionsText = rootView.findViewById(R.id.upcomingGroupSessions);
//        totalToDoListText = rootView.findViewById(R.id.totalToDoList);
//
//        // Initialize TaskDao (for fetching to-do list data)
//        taskDao = TaskDatabase.getInstance(getContext()).taskDao();
//
//        // Example userId (use actual current user's ID)
//        String currentUserId = "050p4fJUoPWpiLw73qdSM2WVqIs1";
//
//        // Fetch group data
//        fetchGroupData(currentUserId);
//
//        // Fetch to-do list data
//        fetchToDoListData(currentUserId);
//
//        return rootView;
//    }
//
//    // Fetch the number of groups joined and upcoming sessions
//    private void fetchGroupData(String userId) {
//        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
//
//        groupsRef.orderByChild("members").equalTo(userId) // Filtering groups where user is a member
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        int totalGroupsJoined = 0;
//                        int upcomingSessions = 0;
//                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
//                            String groupId = snapshot.getKey();
//                            String groupDate = snapshot.child("dateTime").getValue(String.class);
//
//                            // Example of parsing the date (you may need to adjust the date format)
//                            long currentTime = System.currentTimeMillis();
//                            long groupTime = parseDateToMillis(groupDate);
//
//                            if (groupTime > currentTime) {
//                                upcomingSessions++;
//                            }
//
//                            totalGroupsJoined++;
//                        }
//
//                        // Update UI
//                        totalGroupsJoinedText.setText("Total Groups Joined: " + totalGroupsJoined);
//                        upcomingGroupSessionsText.setText("Upcoming Group Sessions: " + upcomingSessions);
//                    }
//                });
//    }
//
//    // Fetch the number of tasks in the to-do list
//    private void fetchToDoListData(String userId) {
//        // Get LiveData for the tasks list
//        LiveData<List<Task>> tasks = taskDao.getTasksByUser(userId);
//
//        // Observe the LiveData to get updates when the data changes
//        tasks.observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
//            @Override
//            public void onChanged(List<Task> taskList) {
//                // Check if taskList is not null
//                if (taskList != null) {
//                    int totalTasks = taskList.size();
//                    totalToDoListText.setText("Total To-Do List Tasks: " + totalTasks);
//                } else {
//                    // Handle the case where the taskList is null
//                    totalToDoListText.setText("Total To-Do List Tasks: 0");
//                }
//            }
//        });
//    }
//
//
//    // Example of parsing date to milliseconds
//    private long parseDateToMillis(String date) {
//        // You'll need to parse the "date" string to actual Date object
//        // For now, return a dummy value (replace with actual parsing)
//        return System.currentTimeMillis(); // Replace with actual date parsing
//    }
//}
