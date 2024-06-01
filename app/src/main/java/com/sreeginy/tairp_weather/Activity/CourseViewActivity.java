package com.sreeginy.tairp_weather.Activity;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sreeginy.tairp_weather.CourseAppDatabase;
import com.sreeginy.tairp_weather.Model.Course;

import java.util.List;

public class CourseViewActivity extends AndroidViewModel {
    private final CourseAppDatabase database;

    public CourseViewActivity(Application application) {
        super(application);
        database = CourseAppDatabase.getDatabase(application);
    }

    // 添加课程
    public void insertCourse(Course course) {
        database.insertCourse(course);
    }

    // 删除课程
    public void deleteCourse(Course course) {
        database.deleteCourse(course);
    }

    // 更新课程
    public void updateCourse(Course course) {
        database.updateCourse(course);
    }

    // 获取特定日期的所有课程
    public LiveData<List<Course>> getCoursesByDate(String date) {
        return database.courseDao().getCoursesByDate(date);
    }
}
