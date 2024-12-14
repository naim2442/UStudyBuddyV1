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

    // Fetch tasks for a specific user
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC")
    List<Task> getTasksByUser(String userId);

    @Query("SELECT COUNT(*) FROM tasks")
    int getTotalTodos();



}
