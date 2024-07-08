package com.example.task1.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundPlayer {
    private ExecutorService executorService;
    private Map<Integer, MediaPlayer> soundPool;
    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(2);
        this.soundPool = new HashMap<>();
    }

    public void playSound(int resourceId) {
        if (executorService.isShutdown()) {
            return;
        }
        executorService.execute(() -> {
            try {
                if (soundPool.containsKey(resourceId)) {
                    MediaPlayer mediaPlayer = soundPool.get(resourceId);
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(context.getResources().openRawResourceFd(resourceId));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, resourceId);
                    soundPool.put(resourceId, mediaPlayer);
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mp.reset();
                    });
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                Log.e("SoundPlayer", "Error playing sound", e);
            }
        });
    }

    public void stopSound(int resourceId) {
        if (executorService.isShutdown()) {
            return;
        }
        executorService.execute(() -> {
            MediaPlayer mediaPlayer = soundPool.get(resourceId);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Log.e("SoundPlayer", "Error preparing media player", e);
                }
            }
        });
    }

    public void release() {
        for (MediaPlayer mediaPlayer : soundPool.values()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
        soundPool.clear();
        executorService.shutdownNow();
    }
}