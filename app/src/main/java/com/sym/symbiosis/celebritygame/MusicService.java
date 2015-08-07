package com.sym.symbiosis.celebritygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.sym.symbiosis.celebritygame.R;

public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MS", "onCreate()");
        mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.loopin);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MS", "onStartCommand()");
        mediaPlayer.start();
        return 1;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i("MS", "stopService()");
        mediaPlayer.stop();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i("MS", "onDestroy()");
        mediaPlayer.release();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
