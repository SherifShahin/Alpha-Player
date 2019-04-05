package zipfiles.com.musicplayer.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import zipfiles.com.musicplayer.Adapter.AllSongsAdapter;
import zipfiles.com.musicplayer.Model.Folder;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Model.Song;

public class AllSongsFragment extends Fragment
{

    private MusicPlayerControl musicPlayerControl;
    private static String state;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private ProgressBar progressBar;

    private AllSongsAdapter allSongsAdapter;
    private RecyclerView recyclerView;

    private static String currentFile="";

    private static ArrayList<Song> currentFileSongs=new ArrayList<>();

    private ArrayList<Folder> folders;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.allsongs_fragment, container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar=view.findViewById(R.id.progressbar);
        recyclerView=view.findViewById(R.id.main_recycleview);

        progressBar.setVisibility(View.VISIBLE);

        if(progressBar.getVisibility() == View.VISIBLE)
        {
            mediaMetadataRetriever =new MediaMetadataRetriever();
            musicPlayerControl=MusicPlayerControl.getinstace(getContext());

            File file=new File(Environment.getExternalStorageDirectory().getPath());

            ArrayList<Song> songs=new ArrayList<>();


            folders=new ArrayList<>();

            if(musicPlayerControl.isHaveList())
            {
                if(!musicPlayerControl.getList().isEmpty()) {
                    songs = musicPlayerControl.getList();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            else {
                songs = findSongs(file);
                musicPlayerControl.setList(songs);
                musicPlayerControl.setFolders(folders);
            }


            musicPlayerControl.releaseFav();


            allSongsAdapter=new AllSongsAdapter(songs,getContext());
            recyclerView.setAdapter(allSongsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        }
    }


    public ArrayList<Song> findSongs(File root)
    {
        ArrayList<Song> songs = new ArrayList<>();
        File[] files = root.listFiles();


        for (File singlefile : files)
        {
            if (singlefile.isDirectory() && !singlefile.isHidden())
            {
                songs.addAll(findSongs(singlefile));
            }
            else
            {
                if(!currentFile.equals(singlefile.getParentFile().getName()))
                {
                    if(!currentFile.isEmpty() && !currentFileSongs.isEmpty()){
                        Folder folder=new Folder();
                        folder.setName(currentFile);
                        folder.setSongs(currentFileSongs);

                        folders.add(folder);

                        currentFile=singlefile.getParentFile().getName();
                        currentFileSongs=new ArrayList<>();
                    }
                    else
                    {
                        currentFile=singlefile.getParentFile().getName();
                    }
                }


                if ((singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")
                        || singlefile.getName().endsWith(".MP3"))
                        && !singlefile.getName().startsWith("Call"))
                {
                    mediaMetadataRetriever.setDataSource(singlefile.getPath());
                    Song song=new Song();

                    if(getSongTitle().equalsIgnoreCase("No Title"))
                    {
                        song.setTitle(singlefile.getName());
                    }
                    else
                        song.setTitle(getSongTitle());

                    song.setImage(getSongImage());
                    song.setArtist(getSongArtist());
                    song.setPath(singlefile.getPath());
                    songs.add(song);
                    currentFileSongs.add(song);
                }
            }
        }

        progressBar.setVisibility(View.INVISIBLE);

        return songs;
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

    public String getSongArtist()
    {
        String artist=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if(artist != null)
            return artist;

        return "no artist";
    }

    public String getSongTitle()
    {
        String Title=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if(Title != null)
            return Title;

        return "No Title";
    }

    @Override
    public void onStart() {
        super.onStart();
 }

}
