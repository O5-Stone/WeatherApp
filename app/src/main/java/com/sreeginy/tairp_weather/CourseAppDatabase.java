package com.sreeginy.tairp_weather;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sreeginy.tairp_weather.Model.Course;
import com.sreeginy.tairp_weather.Model.CourseDao;


@Database(entities = {Course.class}, version = 2, exportSchema = false)
public abstract class CourseAppDatabase extends RoomDatabase {
    public abstract CourseDao courseDao();

    private static volatile CourseAppDatabase INSTANCE;

    public static CourseAppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CourseAppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CourseAppDatabase.class, "course_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // 添加删除、添加、修改课程的方法
    public void deleteCourse(Course course) {
        new Thread(() -> courseDao().deleteCourse(course)).start();
    }

    public void insertCourse(Course course) {
        new Thread(() -> courseDao().insertCourse(course)).start();
    }

    public void updateCourse(Course course) {
        new Thread(() -> courseDao().updateCourse(course)).start();
    }
}
