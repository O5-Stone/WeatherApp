package com.sreeginy.tairp_weather.Activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.sreeginy.tairp_weather.Adapter.MediaAdapter;
import com.sreeginy.tairp_weather.Model.DefaultLrcBuilder;
import com.sreeginy.tairp_weather.Model.ILrcBuilder;
import com.sreeginy.tairp_weather.Model.ILrcView;
import com.sreeginy.tairp_weather.Model.LrcRow;
import com.sreeginy.tairp_weather.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MediaActivity extends AppCompatActivity {
    public final MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentSongIndex = -1;
    private EditText sdedit;
    private TextView textView;
    private DrawerLayout drawerLayout;
    private TextView play_time;
    private SeekBar seekBar;
    private TextView song_time;
    private Button playbtn, previousbtn, nextbtn, backbtn, forwardbtn;
    private RecyclerView recyclerView;
    private final MediaViewActivity mediaViewActivity = new MediaViewActivity();
    private MediaAdapter mediaAdapter;
    private ArrayList<String> mediaList;
    private boolean isSeekbarChaning;
    //自定义LrcView，用来展示歌词
    ILrcView mLrcView;
    //更新歌词的频率，每秒更新一次
    private final int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    public TextView getPlay_time() {
        return play_time;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public TextView getSong_time() {
        return song_time;
    }

    public Button getPlaybtn() {
        return playbtn;
    }

    public Button getPreviousbtn() {
        return previousbtn;
    }

    public Button getNextbtn() {
        return nextbtn;
    }

    public Button getBackbtn() {
        return backbtn;
    }

    public Button getForwardbtn() {
        return forwardbtn;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        sdedit = findViewById(R.id.sd_edit);
        Button searchBtn = findViewById(R.id.searchBtn);
        drawerLayout = findViewById(R.id.song_drawerlayout);
        play_time = findViewById(R.id.play_time);
        seekBar = findViewById(R.id.seekBar);
        song_time = findViewById(R.id.song_time);
        previousbtn = findViewById(R.id.previousBtn);
        backbtn = findViewById(R.id.backBtn);
        playbtn = findViewById(R.id.playBtn);
        forwardbtn = findViewById(R.id.forwardBtn);
        nextbtn = findViewById(R.id.nextBtn);
        recyclerView = findViewById(R.id.mediaManageRecyclerView);
        mLrcView = findViewById(R.id.lrcView);
        textView = findViewById(R.id.SongName);

        if (mediaPlayer.isPlaying()){
            playbtn.setBackground(getDrawable(R.drawable.pause_song));
        }else {
            playbtn.setBackground(getDrawable(R.drawable.play_song));
        }

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mediaPlayer.getCurrentPosition();//获取当前播放的位置
                play_time.setText(mediaViewActivity.calculateTime(position / 1000));//开始时间
                song_time.setText(mediaViewActivity.calculateTime(duration2));//总时长
                // 如果当前播放位置超过了歌曲总时长，说明当前歌曲已经快进完毕
                if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                    stopLrcPlay();
                    mediaPlayer.reset();
                    PlayNextSong();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mediaPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                play_time.setText(mediaViewActivity.calculateTime(mediaPlayer.getCurrentPosition() / 1000));
                if(mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()){
                    PlayNextSong();
                }
            }
        });

        searchBtn.setOnClickListener(v -> {
            drawerLayout.open();
            // 刷新SongManage
            mediaList = mediaViewActivity.updateSongManage(sdedit.getText().toString());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            mediaAdapter = new MediaAdapter(this, mediaList);
            recyclerView.setAdapter(mediaAdapter);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        // 播放、暂停功能
        playbtn.setOnClickListener( v -> {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                playbtn.setBackground(getDrawable(R.drawable.play_song));
            }else {
                mediaPlayer.start();
                playbtn.setBackground(getDrawable(R.drawable.pause_song));
            }
        });
        // 快进、快退功能
        backbtn.setOnClickListener(v -> {
            int current = mediaPlayer.getCurrentPosition();
            int newPosition = current - 5000;
            if (newPosition < 0){
                newPosition = 0;
            }
            mediaPlayer.seekTo(newPosition);
        });
        forwardbtn.setOnClickListener(v -> {
            int current = mediaPlayer.getCurrentPosition();
            int newPosition = current + 5000;
            if (newPosition > mediaPlayer.getDuration()) {
                newPosition = mediaPlayer.getDuration();
            }
            mediaPlayer.seekTo(newPosition);

            // 如果当前播放位置超过了歌曲总时长，说明当前歌曲已经快进完毕
            if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                stopLrcPlay();
                mediaPlayer.reset();
                PlayNextSong();
            }
        });
        // 上一首、下一首功能
        previousbtn.setOnClickListener(v -> PlayPreviousSong());
        nextbtn.setOnClickListener(v -> PlayNextSong());
        //设置自定义的LrcView上下拖动歌词时监听
        //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
        mLrcView.setListener((newPosition, row) -> mediaPlayer.seekTo((int) row.time));

    }

    public void showSong(String SongName){
        String lrc = getSongList(SongName.substring(0, SongName.length() - 4) + ".lrc");
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);
        mLrcView.setLrc(rows);

        beginLrcPlay(SongName);
    }
    public String getSongList(String fileName){
        try {
            @SuppressLint({"NewApi", "LocalSuppress"}) InputStreamReader inputReader = new InputStreamReader(Files.newInputStream(Paths.get("sdcard/Music/Musiclrc/" + fileName)));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder result= new StringBuilder();
            while((line = bufReader.readLine()) != null){
                if(line.trim().isEmpty())
                    continue;
                result.append(line).append("\r\n");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * 开始播放歌曲并同步展示歌词
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void beginLrcPlay(String SongName){
        try {
            File file = new File(sdedit.getText().toString() + SongName);
            textView.setText(SongName);
            String filePath = file.getAbsolutePath();
            mediaPlayer.setDataSource(filePath);
            //准备播放歌曲监听
            //准备完毕
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if(mTimer == null){
                    mTimer = new Timer();
                    mTask = new LrcTask();
                    mTimer.schedule(mTask, 0, mPalyTimerDuration);
                }
            });
            //歌曲播放完毕监听
            mediaPlayer.setOnCompletionListener(mp -> {
                if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()){
                    stopLrcPlay();
                    PlayNextSong();
                }
            });
            //准备播放歌曲
            mediaPlayer.prepare();
            int duration2 = mediaPlayer.getDuration() / 1000;
            int position = mediaPlayer.getCurrentPosition();
            play_time.setText(mediaViewActivity.calculateTime(position / 1000));
            song_time.setText(mediaViewActivity.calculateTime(duration2));
            int duration = mediaPlayer.getDuration();//获取音乐总时间
            seekBar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSeekbarChaning){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            },0,50);
            //开始播放歌曲
            mediaPlayer.start();
            playbtn.setBackground(getDrawable(R.drawable.pause_song));
        } catch (IllegalArgumentException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止展示歌曲
     */
    public void stopLrcPlay(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }
    //计算播放时间
    private void PlayNextSong(){
        if(!mediaList.isEmpty()){
            if (currentSongIndex == -1 || currentSongIndex == mediaList.size() - 1){
                currentSongIndex = 0;
            }else {
                currentSongIndex++;
            }
            playSelectedSong(mediaList.get(currentSongIndex));
        }
    }
    private void PlayPreviousSong(){
        if(!mediaList.isEmpty()){
            if (currentSongIndex == -1 || currentSongIndex == 0){
                currentSongIndex = mediaList.size() - 1;
            }else {
                currentSongIndex--;
            }
        }
        playSelectedSong(mediaList.get(currentSongIndex));
    }

    private void playSelectedSong(String songName){
        mediaPlayer.stop();
        mediaPlayer.reset();
        showSong(songName);
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask{
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mediaPlayer.getCurrentPosition();
            MediaActivity.this.runOnUiThread(() -> {
                //滚动歌词
                mLrcView.seekLrcToTime(timePassed);
            });

        }
    }
}