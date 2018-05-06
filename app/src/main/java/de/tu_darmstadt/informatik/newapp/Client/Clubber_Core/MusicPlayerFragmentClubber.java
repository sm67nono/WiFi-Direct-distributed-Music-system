package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import de.tu_darmstadt.informatik.newapp.R;

/**
 * Created by manna on 26-01-2017.
 */

public class MusicPlayerFragmentClubber {

    private View mContentView = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContentView = inflater.inflate(R.layout.music_player_clubber, null);
        mediaPlayer = new MediaPlayer();

        return mContentView;

    }*/
    public void stopPlaying(){

        try {

            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void startPlaying(String url, long startingtime, int startingPosition){

        try {

                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                //mediaPlayer.setDataSource("http://www.stephaniequinn.com/Music/Canon.mp3");
                //mediaPlayer.setDataSource("http://192.168.49.1:1512/1012.mp3");

                mediaPlayer.setDataSource(url);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.prepare();
                mediaPlayer.start();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
