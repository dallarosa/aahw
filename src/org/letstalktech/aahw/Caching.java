package org.letstalktech.aahw;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class Caching extends SQLiteOpenHelper {
    static final String APP_BASE_DIR = "/Android/data/me.fivetalk/";
    String externalStorageState;
	   private static final int DATABASE_VERSION = 2;
	    private static final String CACHING_TABLE_NAME = "network_caching_data";
	    private static final String CACHING_TABLE_CREATE =
	       "CREATE TABLE "+CACHING_TABLE_NAME+" (" +
	       "ID INTEGER PRIMARY KEY, " +
	       "URL TEXT, " +
	       "DATA_TYPE INTEGER, " +
	       "DATA TEXT);";
	    SQLiteDatabase rdb;
	    SQLiteDatabase wdb;
	    private static final int IMAGE = 0;
	    private static final int JSON = 1;
	    private static final int HTML = 2;
	    private static final int BINARY = 3;
	public Caching(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "caching", null, DATABASE_VERSION);
		rdb = getReadableDatabase();
		wdb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CACHING_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public boolean isInCache(String url){

		if(rdb.query(CACHING_TABLE_NAME,new String[]{"id"}, "URL = ? ", new String[]{url}, null, null, null).getCount() > 0)
			return true;
		return false;
	}
	
	public Object getCacheData(String url){
		Cursor result = rdb.query(CACHING_TABLE_NAME,new String[]{"ID","DATA_TYPE","DATA"}, "URL = ? ", new String[]{url}, null, null, null);
		switch(result.getInt(1)){
		case JSON:
//			try {
//				JSONObject json = new JSONObject(result.getString(2));
//				return json;
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			break;
		case IMAGE:
			if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
				Bitmap image= getImageFromFile(result.getString(2));
				return image;
			}
			break;
		case HTML:
			break;
		case BINARY:
			break;	
		}
		return null;
	}

	private Bitmap getImageFromFile(String filename) {
		File baseDir = Environment.getExternalStorageDirectory();
		File iconDir = new File(baseDir, APP_BASE_DIR + "files/user_profiles");
		if (!iconDir.exists())
			iconDir.mkdirs();
		File iconFile = new File(iconDir,filename);
		FileInputStream fis;
		try {
			fis = new FileInputStream(iconFile);
	        Bitmap bi = BitmapFactory.decodeStream(fis);
	        fis.close();
	        return bi;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
