package com.taveiranet.slidescreen.k9;

import static com.larvalabs.slidescreen.PluginConstants.FIELDS_ARRAY;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_DATE;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_ID;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_INTENT;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_LABEL;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_PRIORITY;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_TEXT;
import static com.larvalabs.slidescreen.PluginConstants.FIELD_TITLE;

import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.larvalabs.slidescreen.PluginUtils;

/**
 * @author João Pedro Taveira
 */
public class SlideK9ContentProvider extends ContentProvider {

    private static final String TAG = SlideK9ContentProvider.class.getName();

    public static final Uri CONTENT_URI = Uri.parse("content://com.taveiranet.slidescreen.k9");
    public static final Uri K9CONTENT_INBOX_URI = Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages");
    public static final Uri K9CONTENT_ACCOUNTS_URI = Uri.parse("content://com.fsck.k9.messageprovider/accounts");

    public static interface MessageColumns extends BaseColumns {
        /**
         * The number of milliseconds since Jan. 1, 1970, midnight GMT.
         *
         * <P>Type: INTEGER (long)</P>
         */
        String SEND_DATE = "date";

        /**
         * <P>Type: TEXT</P>
         */
        String SENDER = "sender";

        /**
         * <P>Type: TEXT</P>
         */
        String SUBJECT = "subject";

        /**
         * <P>Type: TEXT</P>
         */
        String PREVIEW = "preview";

        String UNREAD = "unread";
        String ACCOUNT = "account";
        String URI = "uri";
        String DELETE_URI = "delUri";

        /**
         * @deprecated the field value is misnamed/misleading - present for compatibility purpose only. To be removed.
         */
        @Deprecated
        String INCREMENT = "id";
    }
    
    private static final String[] DEFAULT_MESSAGE_PROJECTION = new String[] {
        MessageColumns._ID,
        MessageColumns.SEND_DATE,
        MessageColumns.SENDER,
        MessageColumns.SUBJECT,
        MessageColumns.PREVIEW,
        MessageColumns.UNREAD,
        MessageColumns.ACCOUNT,
        MessageColumns.URI,
        MessageColumns.DELETE_URI
    };
    
    public boolean onCreate() {
        Log.d(TAG, "* CREATED.");
        
        return true;
    }

    public Cursor query(Uri uri, String[] fields, String s, String[] strings1, String s1) {
        if (fields == null || fields.length == 0) {
            fields = FIELDS_ARRAY;
        }
        Log.d(TAG, "* QUERY Called.");

		SharedPreferences mPrefs = getContext().getSharedPreferences("k9plugin.prefs", Context.MODE_PRIVATE);
		Map<String, ?> prefsKeys = mPrefs.getAll();
		for(String key: prefsKeys.keySet()){
			Log.d(TAG,"Key: " + key + " value: " + mPrefs.getBoolean(key, false));
		}

        Cursor mails = getContext().getContentResolver().query(SlideK9ContentProvider.K9CONTENT_INBOX_URI, DEFAULT_MESSAGE_PROJECTION, null, null, null);
        MatrixCursor cursor = new MatrixCursor(fields);
        
        for(int i = 0; i < mails.getCount(); i++){
        	mails.moveToPosition(i);
        	
        	long id = mails.getLong(mails.getColumnIndex(MessageColumns._ID));
        	//int count = mails.getInt(mails.getColumnIndex(MessageColumns._COUNT));
        	long sendDate = mails.getLong(mails.getColumnIndex(MessageColumns.SEND_DATE));
        	String sender = mails.getString(mails.getColumnIndex(MessageColumns.SENDER));
        	String subject = mails.getString(mails.getColumnIndex(MessageColumns.SUBJECT));
        	//String preview = mails.getString(mails.getColumnIndex(MessageColumns.PREVIEW));
        	String account = mails.getString(mails.getColumnIndex(MessageColumns.ACCOUNT));
        	String messageUri = mails.getString(mails.getColumnIndex(MessageColumns.URI));
        	//String delUri = mails.getString(mails.getColumnIndex(MessageColumns.DELETE_URI));
        	boolean unread = Boolean.parseBoolean(mails.getString(mails.getColumnIndex(MessageColumns.UNREAD)));

        	if(!unread) continue;
        	
        	MatrixCursor.RowBuilder builder = cursor.newRow();
        	long time = sendDate;
			for (String field : fields) {
				if (FIELD_ID.equals(field)) {
					builder.add(id);
				} else if (FIELD_TITLE.equals(field)) {
					builder.add(sender);
				} else if (FIELD_LABEL.equals(field)) {
					builder.add(account);
				} else if (FIELD_TEXT.equals(field)) {
					builder.add(subject);
				} else if (FIELD_DATE.equals(field)) {
					builder.add(time);
				} else if (FIELD_PRIORITY.equals(field)) {
					builder.add(time);
				} else if (FIELD_INTENT.equals(field)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(messageUri));
					builder.add(PluginUtils.encodeIntents(intent));
				} else {
					builder.add("");
				}
			}
        	
        }
        
        mails.close();		
		
        for (String string : fields) {
            Log.d(TAG, "  ARG: " + string);
        }
        
//        for (int i = 1; i <= 2; i++) {
//            MatrixCursor.RowBuilder builder = cursor.newRow();
//            long time = Calendar.getInstance().getTime().getTime();
//            for (String field : fields) {
//                if (FIELD_ID.equals(field)) {
//                    builder.add("" + i);
//                } else if (FIELD_TITLE.equals(field)) {
//                    builder.add("Title #" + i);
//                } else if (FIELD_LABEL.equals(field)) {
//                    builder.add("LABEL");
//                } else if (FIELD_TEXT.equals(field)) {
//                    builder.add("Hello and welcome to item #" + i + ".");
//                } else if (FIELD_DATE.equals(field)) {
//                    builder.add(time - i);
//                } else if (FIELD_PRIORITY.equals(field)) {
//                    builder.add(time - i);
//                } else if (FIELD_INTENT.equals(field)) {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.setType("vnd.android-dir/mms-sms");
//                    builder.add(PluginUtils.encodeIntents(intent));
//                } else {
//                    builder.add("");
//                }
//            }
//        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    

    @Override
    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public int delete(Uri uri, String s, String[] strings) {
        return -1;
    }

    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return -1;
    }

    public void sendUpdatedNotification() {
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
    }
    
}
