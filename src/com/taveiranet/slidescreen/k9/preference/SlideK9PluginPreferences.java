package com.taveiranet.slidescreen.k9.preference;

import java.util.Hashtable;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.taveiranet.slidescreen.k9.R;
import com.taveiranet.slidescreen.k9.SlideK9ContentProvider;

public class SlideK9PluginPreferences extends PreferenceActivity {
    private static final String TAG = SlideK9PluginPreferences.class.getName();

	
	private static final int DIALOG_OAUTH = 0x1;

	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"* onCreate");

		PreferenceScreen prefsScreen = getPreferenceManager().createPreferenceScreen(this);
		prefsScreen.getPreferenceManager().setSharedPreferencesName("k9plugin.prefs");
		prefsScreen.getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
		setPreferenceScreen(prefsScreen);
		
		mPrefs = getSharedPreferences("k9plugin.prefs", MODE_PRIVATE);
		Map<String, ?> prefsKeys = mPrefs.getAll();
		for(String key: prefsKeys.keySet()){
			Log.d(TAG,"Key: " + key);
		}

        String[] projection = new String[] { "accountNumber", "accountName" };
        Cursor accounts = getContentResolver().query(SlideK9ContentProvider.K9CONTENT_ACCOUNTS_URI, projection, null, null, null);
        Hashtable<String, String> accountsMap = new Hashtable<String, String>(); 
        for(int i = 0 ; i < accounts.getCount(); i++){
        	accounts.moveToPosition(i);
        	int accountNumber = accounts.getInt(accounts.getColumnIndex("accountNumber"));
        	String accountName = accounts.getString(accounts.getColumnIndex("accountName"));
        	Log.d(TAG,"Account: #" + accountNumber + " " + accountName);
        	accountsMap.put("account"+accountNumber, accountName);
        }
        accounts.close();

        for(String key: prefsKeys.keySet()){
        	if(key.matches("account.*") && !accountsMap.containsKey(key)){
        		Log.d(TAG,"* onCreate: delete account");
        		mPrefs.edit().remove(key);
        	}
        }
        
        for(String key: accountsMap.keySet()){
        	Preference account = new CheckBoxPreference(this);
    		account.setTitle(accountsMap.get(key));
    		account.setKey(key);
    		getPreferenceScreen().addPreference(account);
        }
		
		PreferenceScreen about = getPreferenceManager().createPreferenceScreen(this);
		
		about.setKey("k9.about");
		about.setTitle(R.string.pref_about_title);
		about.setSummary(R.string.pref_about_title);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setComponent(new ComponentName("com.taveiranet.slidescreen.k9", "com.taveiranet.slidescreen.k9.preference.AboutActivity"));
		about.setIntent(intent);
		getPreferenceScreen().addPreference(about);
		
		//String token = mPrefs.getString(Constants.PREF_TOKEN, "");

		//mOAuthPref = findPreference("oauth.login");
	}

	@Override
	protected void onPause() {
		Log.d(TAG,"* onPause");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG,"* onStop");
		Map<String, ?> prefsKeys = getPreferenceScreen().getSharedPreferences().getAll();
		for(String key: prefsKeys.keySet()){
			Log.d(TAG,"Key: " + key);
		}
		super.onStop();
	}
}
