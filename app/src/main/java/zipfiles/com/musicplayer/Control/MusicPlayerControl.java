package zipfiles.com.musicplayer.Control;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import zipfiles.com.musicplayer.Model.Folder;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.Service.BackgroundService;
import zipfiles.com.musicplayer.Storage.SharedPrefManger;
import zipfiles.com.musicplayer.Interface.Subscriber;

public class MusicPlayerControl implements MediaPlayer.OnPreparedListener
{
    private static MediaPlayer  musicplayer;
    private static MusicPlayerControl instance;
    private Context context;
    private static Song song;
    private static String state;
    private static int currentSongPosition=-1;

    private ArrayList<Song> list;
    private ArrayList<Song> fav_list;
    private ArrayList<Folder> Folders;

    private ArrayList<Song> current_list;

    private Folder currentFolder;

    private MediaMetadataRetriever mediaMetadataRetriever;

    private int positionInFav;

    private static String repeatType="none";

    private static boolean in_Now_Playing=false;

    private boolean haveList=false;

    private static String shuffleState="";

    private ArrayList<Subscriber> subscribers;


    public MusicPlayerControl(Context context)
    {
        /**
         * Function:
         *      DIC --
         *
         * Arguments:
         *      context -->
         *
         * Raise:
         *
         * Return:
         *
         *
         * */
        this.context=context;

        musicplayer=new MediaPlayer();
        musicplayer.setVolume(100,100);
        musicplayer.setLooping(false);
        mediaMetadataRetriever=new MediaMetadataRetriever();
        list=new ArrayList<>();
        subscribers=new ArrayList<>();
    }

    public static MusicPlayerControl getinstace(Context context)
    {
        if(instance == null)
        {
            synchronized (MusicPlayerControl.class) {
                if(instance == null)
                    instance =new MusicPlayerControl(context);
            }
        }
        return instance;
    }


    public void setSong(Song song,int currentSongPosition)
    {
        this.song=song;
        this.currentSongPosition=currentSongPosition;

        try {
            musicplayer=new MediaPlayer();
            musicplayer.reset();
            musicplayer.setDataSource(getSong().getPath());
            mediaMetadataRetriever.setDataSource(getSong().getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Song getSong() {
        return song;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        notifySubscribers();
    }

    public ArrayList<Song> getList() {
        return list;
    }

    public void setList(ArrayList<Song> list) {
        this.list = list;
        setHaveList(true);
    }

    public int getCurrentSongPosition()
    {
        return currentSongPosition;
    }


    public void playForFirstTime()
    {
        musicplayer.prepareAsync();
        musicplayer.setOnPreparedListener(this);
        setState("playFirst");
    }


    public void play()
    {
        musicplayer.start();
    }


    public void pause()
    {
        if(musicplayer.isPlaying()) {
            musicplayer.pause();
        }
    }


    public void stop()
    {
        musicplayer.stop();
    }

    public void release()
    {
        musicplayer.release();
    }


    public void intentToService(String type)
    {
        Intent intent=new Intent(context,BackgroundService.class);
        intent.putExtra("root",getSong().getPath());
        intent.putExtra("type",type);
        context.startService(intent);
    }


    public int getCurrentDuration() { return musicplayer.getDuration(); }

    public int getCurrentPosition() { return musicplayer.getCurrentPosition(); }


    public String getCurrentDurationInString()
    {
        int songduration=getCurrentDuration();

        String currentduration=String.format("%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(songduration)
                , TimeUnit.MILLISECONDS.toSeconds(songduration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songduration))
        );

        return currentduration;
    }


    public String getCurrentPositionInString()
    {
        int songposition=getCurrentPosition();

        String currentposition=String.format("%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(songposition)
                , TimeUnit.MILLISECONDS.toSeconds(songposition)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songposition))
        );

        return currentposition;
    }

    public void seekTo(int msec) { musicplayer.seekTo(msec); }


    public void playNext()
    {
        if(getCurrentSongPosition() < getCurrent_list().size()-1)
        {
            setSong(getCurrent_list().get(getCurrentSongPosition() + 1), getCurrentSongPosition() + 1);
        }
        else
            setSong(getCurrent_list().get(0),0);

        intentToService("playFirst");
    }


    public void playNextWithShuffle()
    {
        setSong(getCurrent_list().get(generaterandom()),generaterandom());

        intentToService("playFirst");
    }



    public void pvPlay()
    {
        if(getCurrentSongPosition() != 0) {
            setSong(getCurrent_list().get(getCurrentSongPosition() - 1), getCurrentSongPosition() - 1);
        }
        else
            setSong(getCurrent_list().get(getCurrent_list().size()-1),getCurrent_list().size()-1);

        intentToService("playFirst");
    }


    public void releaseFav()
    {
        SharedPrefManger sharedPrefManger=SharedPrefManger.getInstance(context);

        if(sharedPrefManger.getFavourites() != null)
        {
            fav_list=sharedPrefManger.getFavourites();
        }
        else
            fav_list=new ArrayList<>();

    }


    public void setinFavourites()
    {
        releaseFav();

        fav_list.add(song);

        SharedPrefManger.getInstance(context).set_Favourites(fav_list);
    }


    public void setSongInFavourites(Song song)
    {
        releaseFav();

        fav_list.add(song);

        SharedPrefManger.getInstance(context).set_Favourites(fav_list);
    }


    public void removeFromFavourites()
    {
        releaseFav();

       if(!fav_list.isEmpty())
       {
           fav_list.remove(positionInFav);
       }

       SharedPrefManger.getInstance(context).set_Favourites(fav_list);
    }


    public boolean inFavourites()
    {
        releaseFav();

        if(fav_list != null)
        {
            if (check())
            {
                return true;
            }

        }



        return false;
    }


    public ArrayList<Song> getFav_list()
    {
        return SharedPrefManger.getInstance(context).getFavourites();
    }


    public boolean check()
    {
        positionInFav=-1;

        for(int i=0 ;i<fav_list.size();i++)
        {
            if(fav_list.get(i).getTitle().equals(song.getTitle()))
            {
                positionInFav=i;
                break;
            }
        }

        if(positionInFav != -1)
            return true;
        else
        return false;
    }


    public int getPositionInFav(String title)
    {
        int positionInFav=-1;

        for(int i=0 ;i<fav_list.size();i++)
        {
            if(fav_list.get(i).getTitle().equals(title))
            {
                positionInFav=i;
                break;
            }
        }

            return positionInFav;
    }





    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType,Context context)
    {
        this.repeatType = repeatType;
        this.context=context;
    }


    public MediaPlayer getMusicplayer()
    {
        return musicplayer;
    }


    public static boolean isIn_Now_Playing() {
        return in_Now_Playing;
    }

    public static void setIn_Now_Playing(boolean in_Now_Playing) {
        MusicPlayerControl.in_Now_Playing = in_Now_Playing;
    }


    public boolean isHaveList() {
        return haveList;
    }

    public void setHaveList(boolean haveList) {
        this.haveList = haveList;
    }

    public ArrayList<Folder> getFolders() {
        return Folders;
    }

    public void setFolders(ArrayList<Folder> folders) {
        Folders = folders;
    }


    public Folder getCurrentFolder()
    {
        return currentFolder;
    }

    public void setCurrentFolder(Folder currentFolder)
    {
        this.currentFolder = currentFolder;
    }


    public void setCurrent_list(ArrayList<Song> current_list) {
        this.current_list = current_list;
    }

    public ArrayList<Song> getCurrent_list() {
        return current_list;
    }

    public void setFav_list(ArrayList<Song> fav_list) {
        this.fav_list = fav_list;
    }

    public int generaterandom()
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(current_list.size());
        return randomInt;
    }


    public String getShuffleState() {
        return shuffleState;
    }

    public void setShuffleState(String shuffleState) {
        this.shuffleState = shuffleState;
    }



    public void subscribe(Subscriber subscriber)
    {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber)
    {
        int index=subscribers.indexOf(subscriber);
        subscribers.remove(index);
    }

    public void notifySubscribers()
    {
        for(Subscriber subscriber:subscribers)
        {
            subscriber.update(state);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
