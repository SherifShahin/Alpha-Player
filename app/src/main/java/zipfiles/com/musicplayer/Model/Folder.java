package zipfiles.com.musicplayer.Model;

import java.util.ArrayList;

public class Folder
{
    private String name;
    private ArrayList<Song> songs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

}
