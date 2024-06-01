package com.sreeginy.tairp_weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sreeginy.tairp_weather.Activity.MediaActivity;
import com.sreeginy.tairp_weather.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private final MediaActivity mediaActivity;
    private final List<String> mediaList;

    public MediaAdapter(MediaActivity mediaActivity, List<String> mediaList) {
        this.mediaActivity = mediaActivity;
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_media_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            String media = mediaList.get(position);
            mediaActivity.setCurrentSongIndex(position);
            mediaActivity.getDrawerLayout().closeDrawers();
            mediaActivity.mediaPlayer.reset();
            mediaActivity.showSong(media);
            mediaActivity.getSeekBar().setVisibility(View.VISIBLE);
            mediaActivity.getPlay_time().setVisibility(View.VISIBLE);
            mediaActivity.getSong_time().setVisibility(View.VISIBLE);
            mediaActivity.getBackbtn().setVisibility(View.VISIBLE);
            mediaActivity.getForwardbtn().setVisibility(View.VISIBLE);
            mediaActivity.getPlaybtn().setVisibility(View.VISIBLE);
            mediaActivity.getNextbtn().setVisibility(View.VISIBLE);
            mediaActivity.getPreviousbtn().setVisibility(View.VISIBLE);
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String media = mediaList.get(position);
        holder.SongName.setText(media);
    }
    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView SongName;

        public ViewHolder(View view) {
            super(view);
            SongName = view.findViewById(R.id.SongName);
        }
    }
}
