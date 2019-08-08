package zipfiles.com.musicplayer.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import zipfiles.com.musicplayer.Model.Song;


public class SharedPrefManger
{
    private static String Shared_name="AlphaPlayer_Data";
    private static SharedPrefManger mInstance;
    private Context context;

    public SharedPrefManger(Context context)
    {
        this.context = context;
    }

    public static synchronized SharedPrefManger getInstance(Context context)
    {
        if(mInstance == null )
        {
            mInstance=new SharedPrefManger(context);
        }
      return mInstance;
    }


    public void set_Favourites(ArrayList<Song> list)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("fav_list", json);
        editor.apply();
        editor.commit();
    }


    public ArrayList<Song> getFavourites()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("fav_list", null);

        Type type=new TypeToken<ArrayList<Song>>(){}.getType();
        ArrayList<Song> list=gson.fromJson(json,type);

        return list;
    }

    public void saveLastSong(Song song)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString("songTitle",song.getTitle());
        editor.putString("songPath",song.getPath());
        editor.putString("songArtist",song.getArtist());

        editor.commit();

    }

    public Song getLastSong()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);

        Song song= new Song();

        song.setTitle(sharedPreferences.getString("songTitle",""));
        song.setPath(sharedPreferences.getString("songPath",""));
        song.setArtist(sharedPreferences.getString("songArtist",""));

        if(song.getTitle().isEmpty())
            return null;
        else
        return song;
    }



    public void clear()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.clear();
        editor.apply();
        editor.commit();
    }


}
