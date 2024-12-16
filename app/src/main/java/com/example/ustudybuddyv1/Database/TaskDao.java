package com.example.ustudybuddyv1.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Update
    void updateTask(Task task);  // Method to update a task

    // Fetch tasks for a specific user
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC")
    List<Task> getTasksByUser(String userId);

    // Fetch total tasks for a specific user
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    int getTotalTodos(String userId);
}
