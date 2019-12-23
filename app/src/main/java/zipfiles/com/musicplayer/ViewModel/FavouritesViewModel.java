package zipfiles.com.musicplayer.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.Model.Song;

public class FavouritesViewModel extends ViewModel
{
    private MutableLiveData<List<Song>> songs;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private MusicPlayerControl control;
    private ArrayList<Song> list;
    private boolean booleanthread=true;

    public LiveData<List<Song>> getSongs(Context context)
    {
        if (songs == null)
        {
            control = MusicPlayerControl.getinstace(context);
            songs = new MutableLiveData<>();
            loadSongs();
        }
        return songs;
    }



    private void loadSongs()
    {
        if(control.isHaveFavList())
        {
            ArrayList<Song> favsongs = control.getFavList();
            if(favsongs != null) {
                Collections.reverse(favsongs);
                songs.setValue(favsongs);
            }
        }
        else
            getFavSongs();

    }

    private Bitmap getSongImage()
    {
        byte[] art = mediaMetadataRetriever.getEmbeddedPicture();

        Bitmap Songimg = null;

        if (art != null) {
            Songimg = BitmapFactory.decodeByteArray(art, 0, art.length);
        }

        if (Songimg != null)
            return Songimg;

        return null;
    }

    private void getFavSongs()
    {
        final Thread thread = new Thread(){
            public void run(){

                while(booleanthread)
                {
                    mediaMetadataRetriever= new MediaMetadataRetriever();
                    control.releaseFav();
                    list = control.getFav_list();

                    if(list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                mediaMetadataRetriever.setDataSource(list.get(i).getPath());
                                list.get(i).setImage(getSongImage());
                            }
                        control.setFav_list(list);
                        Collections.reverse(list);
                    }
                    setSongs(list);
                    booleanthread=false;
                }
            }
        };
        thread.start();
    }

    private void setSongs(List<Song> list)
    {
        songs.postValue(list);
    }
}
