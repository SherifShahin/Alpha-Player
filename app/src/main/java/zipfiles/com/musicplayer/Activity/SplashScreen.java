package zipfiles.com.musicplayer.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.Model.Folder;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.R;

public class SplashScreen extends AppCompatActivity {

    private MusicPlayerControl musicPlayerControl;
    private MediaMetadataRetriever mediaMetadataRetriever;

    private static String currentFile="";
    private static String currentFilePath="";
    private static int currentFileIndex= -1;

    private static ArrayList<Song> currentFileSongs=new ArrayList<>();
    private ArrayList<Folder> folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mediaMetadataRetriever =new MediaMetadataRetriever();
        musicPlayerControl= MusicPlayerControl.getinstace(this);

        final File file=new File(Environment.getExternalStorageDirectory().getPath());

        final ArrayList<Song>[] songs = new ArrayList[]{new ArrayList<>()};

        folders = new ArrayList<>();

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                songs[0] = findSongs(file);
                musicPlayerControl.setList(songs[0]);
                musicPlayerControl.setFolders(folders);

                if(musicPlayerControl.isHaveList()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        };

        thread.start();

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
                        folder.setUrl(currentFilePath);
                        folder.setIndex(++currentFileIndex);

                        folders.add(folder);

                        currentFile=singlefile.getParentFile().getName();
                        currentFileSongs=new ArrayList<>();
                    }
                    else
                    {
                        currentFile=singlefile.getParentFile().getName();
                        currentFilePath=singlefile.getParentFile().getPath().toString();
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


}
