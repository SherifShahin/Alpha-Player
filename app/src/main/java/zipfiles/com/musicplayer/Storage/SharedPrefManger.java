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





    public void setHavePosts()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putBoolean("haveposts",true);

        editor.apply();
        editor.commit();

    }


    public boolean havePosts()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(Shared_name, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean("haveposts",false);
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
