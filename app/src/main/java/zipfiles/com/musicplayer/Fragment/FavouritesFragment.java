package zipfiles.com.musicplayer.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zipfiles.com.musicplayer.Adapter.AllSongsAdapter;
import zipfiles.com.musicplayer.Adapter.FavouritesAdapter;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Model.Song;

public class FavouritesFragment extends Fragment
{

    private FavouritesAdapter favouritesAdapter;
    private RecyclerView recyclerView;

    private MusicPlayerControl control;
    private ArrayList<Song> fav_frag_songs;
    private MediaMetadataRetriever mediaMetadataRetriever;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourites_fragment, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView=view.findViewById(R.id.Favourites_recycleview);

        mediaMetadataRetriever =new MediaMetadataRetriever();

        control=MusicPlayerControl.getinstace(getContext());

        control.releaseFav();

        fav_frag_songs=new ArrayList<>();

        if(control.getList() != null) {
            fav_frag_songs = control.getFav_list();

            Log.e("Favourite frag"," "+fav_frag_songs.size());

            for(int i=0;i<fav_frag_songs.size();i++)
            {
                mediaMetadataRetriever.setDataSource(fav_frag_songs.get(i).getPath());
                fav_frag_songs.get(i).setImage(getSongImage());
            }
        }


        if(!fav_frag_songs.isEmpty()) {
            favouritesAdapter = new FavouritesAdapter(fav_frag_songs,getContext());
            recyclerView.setAdapter(favouritesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }



    public Bitmap getSongImage()
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


    @Override
    public void onResume() {
        super.onResume();
    }
}
