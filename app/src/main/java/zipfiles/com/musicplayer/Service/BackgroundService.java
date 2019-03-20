package zipfiles.com.musicplayer.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import zipfiles.com.musicplayer.Activity.MainActivity;
import zipfiles.com.musicplayer.Activity.NowPlaying;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;

public class BackgroundService extends Service
{
    private MusicPlayerControl musicPlayerControl;
    private String type;
    private int result;
    private AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange)
                {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
                    {
                        // Permanent loss of audio focus
                        //stop playback cleanup resources
                        musicPlayerControl.intentToService("stop");
                        mAudioManager.abandonAudioFocus(afChangeListener);
                    }

                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
                    {
                        musicPlayerControl.getMusicplayer().setVolume(0.2f, 0.2f);
                    }

                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
                    {
                        // Pause playback
                        musicPlayerControl.intentToService("pause");
                    }

                    else if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
                    {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        //resume playback

                        if(musicPlayerControl.getState().equalsIgnoreCase("stop"))
                        {
                            musicPlayerControl.intentToService("playFirst");
                        }
                        else
                        musicPlayerControl.intentToService("start");

                        musicPlayerControl.getMusicplayer().setVolume(80, 80);
                    }
                }
            };



    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        musicPlayerControl=MusicPlayerControl.getinstace(getApplicationContext());


        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        result = mAudioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);


        type=intent.getStringExtra("type");

        if(type.equalsIgnoreCase("playFirst"))
        {
            if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                musicPlayerControl.playForFirstTime();
                musicPlayerControl.setState("play");
            }

        }
        else if(type.equalsIgnoreCase("play"))
        {
            if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                musicPlayerControl.play();
                musicPlayerControl.setState("play");
            }
        }
        else if(type.equals("pause"))
        {
            musicPlayerControl.pause();
            musicPlayerControl.setState("pause");
        }
        else if(type.equalsIgnoreCase("stop"))
        {
            musicPlayerControl.stop();
            musicPlayerControl.setState("stop");
            stopForeground(true);
            stopSelf();
            mAudioManager.abandonAudioFocus(afChangeListener);
        }
        else if(type.equalsIgnoreCase("next"))
        {
            musicPlayerControl.playNext();
        }


        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                0);


        Notification notification;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? getNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.main_logo)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setContentText(musicPlayerControl.getSong().getArtist())
                .setContentTitle(musicPlayerControl.getSong().getTitle())
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);


        if(musicPlayerControl.getSong().getImage() != null)
        {
            notificationBuilder.setLargeIcon(musicPlayerControl.getSong().getImage());
        }
        else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            notificationBuilder.setLargeIcon(bitmap);
        }

        if(type.equalsIgnoreCase("play") || type.equalsIgnoreCase("playFirst"))
        {
            Intent pauseIntent = new Intent(this, BackgroundService.class);
            pauseIntent.putExtra("type","pause");
            PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder.addAction(R.drawable.pause_noti, "pause",pendingPrevIntent);
        }

        else if(type.equalsIgnoreCase("pause"))
        {
            Intent playIntent = new Intent(this, BackgroundService.class);
            playIntent.putExtra("type","play");
            PendingIntent pendingNextIntent = PendingIntent.getService(this, 1, playIntent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder.addAction(R.drawable.play_noti_icon, "play",pendingNextIntent);
        }

        Intent NextIntent = new Intent(this, BackgroundService.class);
        NextIntent.putExtra("type","next");
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 1, NextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.addAction(R.drawable.next_noti, "NEXT",pendingNextIntent);

         notification = notificationBuilder.build();

        startForeground(110, notification);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        musicPlayerControl.stop();
        stopForeground(true);
        stopSelf();
        mAudioManager.abandonAudioFocus(afChangeListener);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getNotificationChannel(NotificationManager notificationManager){
        String channelId = "channelid";
        String channelName = getResources().getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }


}

