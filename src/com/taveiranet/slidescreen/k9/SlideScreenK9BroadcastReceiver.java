package com.taveiranet.slidescreen.k9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SlideScreenK9BroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = SlideK9ContentProvider.class.getName();
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
        Log.d(TAG, "* onReceive called.");
		arg0.getContentResolver().notifyChange(SlideK9ContentProvider.CONTENT_URI, null);
	}
}
