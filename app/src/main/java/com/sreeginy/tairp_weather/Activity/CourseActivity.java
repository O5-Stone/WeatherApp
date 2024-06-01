package com.sreeginy.tairp_weather.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sreeginy.tairp_weather.Adapter.CourseAdapter;
import com.sreeginy.tairp_weather.Model.Course;
import com.sreeginy.tairp_weather.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView scheduleRecyclerView;
    private CourseAdapter courseAdapter;
    private CourseViewActivity courseViewActivity;
    private Button addBtn;
    private String selectedDate;

    public CourseViewActivity getCourseViewModel() {
        return courseViewActivity;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    Comparator<Course> timeComparator = Comparator.comparing(Course::getTime);

    private List<Course> filteredCourses = new ArrayList<>(); // 用于存储按日期过滤后的课程

    @SuppressLint({"MissingInflatedId", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        calendarView = findViewById(R.id.calendarView);
        scheduleRecyclerView = findViewById(R.id.schedule_recycler_view);
        courseViewActivity = new ViewModelProvider(this).get(CourseViewActivity.class);
        addBtn = findViewById(R.id.addCourseBtn);

//        // 设置当前日期为日历视图的默认日期
//        Calendar calendar = Calendar.getInstance();
//        calendarView.setDate(calendar.getTimeInMillis());

        // 设置 RecyclerView
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredCourses.sort(timeComparator);
        courseAdapter = new CourseAdapter(this, filteredCourses); // 使用按日期过滤后的课程
        scheduleRecyclerView.setAdapter(courseAdapter);

        // 设置日历日期选择监听器
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadSchedulesForDate(selectedDate);
        });

        // 加载当日课程数据
        loadSchedulesForToday();

        addBtn.setOnClickListener(v -> {
            // 创建一个弹窗窗口来添加课程信息
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.edit_course_dialog, null);
            builder.setView(dialogView);

            EditText editCourseName = dialogView.findViewById(R.id.edit_course_name);
            EditText editCourseTime = dialogView.findViewById(R.id.edit_course_time);
            EditText editCourseLocation = dialogView.findViewById(R.id.edit_course_location);

            builder.setPositiveButton("确定", (dialog, which) -> {
                String courseName = editCourseName.getText().toString();
                String courseTime = editCourseTime.getText().toString();
                String courseLocation = editCourseLocation.getText().toString();

                // 创建课程对象
//                Log.d("CourseList", selectedDate);
                Course newCourse = new Course(selectedDate,courseName, courseTime, courseLocation);
                // 将课程添加到数据库
                courseViewActivity.insertCourse(newCourse);
                loadSchedulesForDate(selectedDate);
            });

            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            builder.show();

        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadSchedulesForDate(String date) {
        courseViewActivity.getCoursesByDate(date).observe(this, courses -> {
            filteredCourses.clear();
            filteredCourses.addAll(courses);
            courseAdapter.notifyDataSetChanged();
            scheduleRecyclerView.setVisibility(filteredCourses.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void loadSchedulesForToday() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("DefaultLocale") String todayDate = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        loadSchedulesForDate(todayDate);
    }

}
