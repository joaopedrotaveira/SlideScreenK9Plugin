package com.taveiranet.slidescreen.k9;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.larvalabs.slidescreen.PluginReceiver;
import com.taveiranet.slidescreen.k9.R;

/**
 * @author João Pedro Taveira
 */
public class SlidescreenReceiver extends PluginReceiver {

    public static final String TAG = SlidescreenReceiver.class.getName();

    @Override
    public int getColor() {
        return Color.rgb(255, 0, 0);
    }

    @Override
    public Uri getContentProviderURI() {
        return SlideK9ContentProvider.CONTENT_URI;
    }

    
    @Override
    public String getName() {
        return "K-9 Plugin";
    }

    @Override
    public int getIconResourceId() {
        return R.raw.mail;
    }

    @Override
    public Intent[] getSingleTapShortcutIntents() {
        Intent groupIntent = new Intent(Intent.ACTION_MAIN);
        //groupIntent.setType("vnd.android-dir/mms-sms");
        groupIntent.setComponent(new ComponentName("com.fsck.k9","com.fsck.k9.activity.Accounts"));
        return new Intent[]{groupIntent};
    }

    @Override
    public Intent[] getLongpressShortcutIntents() {
        Intent longIntent = new Intent(Intent.ACTION_VIEW);
        //longIntent.setComponent(new ComponentName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity"));
        longIntent.setComponent(new ComponentName("com.fsck.k9","com.fsck.k9.activity.FolderList"));
        return new Intent[]{longIntent};
    }

    @Override
    public Intent getPreferenceActivityIntent() {
    	Intent prefs = new Intent(Intent.ACTION_MAIN);
        prefs.setComponent(new ComponentName("com.taveiranet.slidescreen.k9",
                "com.taveiranet.slidescreen.k9.preference.SlideK9PluginPreferences"));
        return prefs;
    }

    @Override
    public void markedAsRead(String itemId) {
        Log.d(TAG, "Received item marked as read: " + itemId);
    }
}
