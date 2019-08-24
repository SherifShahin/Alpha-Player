package zipfiles.com.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import zipfiles.com.musicplayer.Control.MusicPlayerControl;

public  class HardButtonReceiver extends BroadcastReceiver
{
    private MusicPlayerControl musicPlayerControl;

    public HardButtonReceiver ()
    {
        super ();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        musicPlayerControl = MusicPlayerControl.getinstace(context);

        KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if(key.getAction() == KeyEvent.ACTION_DOWN)
        {
            int keycode = key.getKeyCode();
            if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
            {
                musicPlayerControl.intentToService("next");
            }
            else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            {
                musicPlayerControl.intentToService("prev");
            }
            else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK) {

                if(musicPlayerControl.getState().equals("start"))
                {
                    musicPlayerControl.intentToService("pause");
                }
                else if(musicPlayerControl.getState().equals("pause"))
                {
                    musicPlayerControl.intentToService("start");
                }
                else if(musicPlayerControl.getState().equals("stop"))
                {
                    musicPlayerControl.intentToService("playFirst");
                }

            }
        }
        abortBroadcast();
    }
}