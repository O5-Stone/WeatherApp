package com.sreeginy.tairp_weather.Adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sreeginy.tairp_weather.Activity.CourseActivity;
import com.sreeginy.tairp_weather.Model.Course;
import com.sreeginy.tairp_weather.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private final CourseActivity courseActivity;
    private final List<Course> courses;
    private final PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f);
    private final PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f);
    private final PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f);
    private final PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f);

    public CourseAdapter(CourseActivity courseActivity, List<Course> courses) {
        this.courseActivity = courseActivity;
        this.courses = courses;
    }

    @NonNull
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress", "NotifyDataSetChanged"})
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitu_course_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            Course course = courses.get(position);
            // 创建对话框以显示课程详情并允许编辑
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("编辑课程");

            // 加载对话框的自定义布局
            View dialogView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_course_dialog, null);
            builder.setView(dialogView);

            // 初始化对话框布局中的视图
            EditText nameEditText = dialogView.findViewById(R.id.edit_course_name);
            EditText timeEditText = dialogView.findViewById(R.id.edit_course_time);
            EditText locationEditText = dialogView.findViewById(R.id.edit_course_location);

            // 使用当前课程详情填充对话框视图
            nameEditText.setText(course.getName());
            timeEditText.setText(course.getTime());
            locationEditText.setText(course.getLocation());

            // 设置确定按钮的操作
            builder.setPositiveButton("保存", (dialog, which) -> {
                // 从对话框视图中获取编辑后的值
                String editedName = nameEditText.getText().toString().trim();
                String editedTime = timeEditText.getText().toString().trim();
                String editedLocation = locationEditText.getText().toString().trim();

                // 使用编辑后的值更新课程对象
                course.setName(editedName);
                course.setTime(editedTime);
                course.setLocation(editedLocation);

                // 通知适配器数据集已更改
                notifyDataSetChanged();
                courseActivity.getCourseViewModel().updateCourse(course);

                // 关闭对话框
                dialog.dismiss();
            });

            // 设置取消按钮的操作
            builder.setNegativeButton("取消", (dialog, which) -> {
                // 关闭对话框
                dialog.dismiss();
            });

            // 创建并显示对话框
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        holder.itemView.setOnLongClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            Course course = courses.get(position);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXDown, scaleYDown);
            animator.setDuration(200);
            animator.start();
            new AlertDialog.Builder(parent.getContext())
                    .setTitle("删除事件")
                    .setMessage("是否删除事件" + course.getName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("是", (dialog, which) -> {
                        courseActivity.getCourseViewModel().deleteCourse(course);
                        dialog.dismiss();
                        v.setScaleX(1.0f);
                        v.setScaleY(1.0f);
                    })
                    .setNegativeButton("否", (dialog, which) -> {
                        dialog.dismiss();
                        ObjectAnimator deleteAnimator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXUp, scaleYUp);
                        deleteAnimator.setDuration(200);
                        deleteAnimator.start();
                    })
                    .show();
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course currentCourse = courses.get(position);

        holder.nameText.setText(currentCourse.getName());
        holder.timeText.setText(currentCourse.getTime());
        holder.locationText.setText(currentCourse.getLocation());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView timeText;
        TextView locationText;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.course_name);
            timeText = view.findViewById(R.id.course_time);
            locationText = view.findViewById(R.id.course_location);
        }
    }
}
