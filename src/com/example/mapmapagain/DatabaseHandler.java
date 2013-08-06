package com.example.mapmapagain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.AsyncTask;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	private static DatabaseHandler mInstance = null;
	 private static final int DATABASE_VERSION = 7;
	 
	    // Database Name
	    private static final String DATABASE_NAME = "Bathrooms";
	 
	    // Contacts table name
	    private static final String TABLE_NAME = "Bathroom_Locations";
	 
	    // Contacts Table Columns names
	    private static final String KEY_ID = "_id";
	    private static final String KEY_LAT = "lat";
	    private static final String KEY_LONG = "longitude";
	    private static final String KEY_NAME = "name";
	    private static final String KEY_ADDR = "address";
	    private static final String KEY_HANDICAPPED = "handicapped";
	    private static final String KEY_PUB = "public";
	    private static final String KEY_CLEAN = "clean";
	    private static final String KEY_REVIEW = "review";
	SQLiteDatabase db;
	        
		   private Context mCtx;

		    public static DatabaseHandler getInstance(Context ctx) {
		        /** 
		         * use the application context as suggested by CommonsWare.
		         * this will ensure that you dont accidentally leak an Activitys
		         * context (see this article for more information: 
		         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
		         */
		        if (mInstance == null) {
		        	Log.i("mInstance", "new");
		            mInstance = new DatabaseHandler(ctx.getApplicationContext());
		       
		        }
		        return mInstance;
		    }

		    /**
		     * constructor should be private to prevent direct instantiation.
		     * make call to static factory method "getInstance()" instead.
		     */
		    DatabaseHandler(Context ctx) {
		        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		        Log.i("Context", "ctx");
		        this.mCtx = ctx;
		    }
	    
	 public void read(){
		 SQLiteDatabase db = this.getReadableDatabase();
	 }
	 
	 public void write(){
		 SQLiteDatabase db = this.getWritableDatabase();
	 }
	
	public void onCreate(SQLiteDatabase db) {
	    String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT NOT NULL, " //not null?
                + KEY_ADDR + " TEXT NOT NULL, " + KEY_LAT + " REAL NOT NULL, " + KEY_LONG + " REAL NOT NULL, " +
                KEY_HANDICAPPED + " INTEGER NOT NULL, " +
                KEY_PUB + " INTEGER NOT NULL, " +
                KEY_CLEAN + " REAL NOT NULL, "	+
                KEY_REVIEW + " TEXT NOT NULL"
                + ")";
    
	    String Create_Latitude_Index = "Create Index latitude_idx on " + TABLE_NAME + "(" + KEY_LAT + ")";
	    String Create_Longitude_Index = "Create Index longitude_idx on " + TABLE_NAME + "(" + KEY_LONG + ")";
	    		Log.i("onCreate", CREATE_LOCATION_TABLE);
		
	    db.execSQL(CREATE_LOCATION_TABLE);
	    db.execSQL(Create_Latitude_Index);
	    db.execSQL(Create_Longitude_Index);
		Log.i("onCreate", "finished");
    }
//change arg0 to db and get rid of global SQLiteDatabase db

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}
	
    // Adding new location
    void addLocation(Locations locations) {
     SQLiteDatabase db = this.getWritableDatabase();
        Log.i("addlocation", locations.toString());
        ContentValues values = new ContentValues();
        Log.i("addlocation", "2");
        values.put(KEY_NAME, locations.getName()); // Location Name
        values.put(KEY_ADDR, locations.getAddress());
        values.put(KEY_LAT, locations.getLat()); // Location Lat
        values.put(KEY_LONG, locations.getLong());
        values.put(KEY_HANDICAPPED, locations.getHandicapped());
        values.put(KEY_PUB, locations.getPub());
        values.put(KEY_CLEAN, locations.getClean());
        values.put(KEY_REVIEW, locations.getReview());
        Log.i("addlocation", values.toString());
        
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
        Log.i("addlocation", "final");
    }
    
/*
    // Getting single location
public Locations getLocations(int id) {
	SQLiteDatabase db = this.getReadableDatabase();
 Cursor cursor = 
db.query(TABLE_NAME, new String[] { KEY_ID, KEY_NAME,  KEY_ADDR, KEY_LAT, KEY_LONG, KEY_HANDICAPPED, KEY_PUB, KEY_CLEAN},
		KEY_ID + "=?", new String[] { String.valueOf(id) },
		null, null, null, null);
           
    if (cursor != null)
        cursor.moveToFirst();
 
    Locations locations = new Locations(Integer.parseInt(cursor.getString(0)),
            cursor.getString(1), cursor.getString(2), 
            cursor.getDouble(3), cursor.getDouble(4),
           cursor.getBoolean(5),  cursor.getBoolean(6),
            cursor.getFloat(7)
    		);
    // return contact
    return locations;
}

*/
public List<Locations> getAddrLocation(String addr) {
	SQLiteDatabase db = this.getWritableDatabase();
	 Log.i("getlatlnglocation", "start");
	 List<Locations> locationList = new ArrayList<Locations>();
	
 Cursor cursor = 
db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ADDR + " = '" + addr + "'", null);
 Log.i("cursor", Integer.toString(cursor.getCount()));
 cursor.moveToPosition(0);
 Log.i("cursor", Integer.toString(cursor.getCount()));
    		if(cursor.getCount() > 0){
    		do{
    			Log.i("cursor", cursor.getString(0));
    			Locations location = new Locations();
	            location.setID(Integer.parseInt(cursor.getString(0)));
	            location.setName(cursor.getString(1));
	             location.setAddress(cursor.getString(2));
	            location.setLat(cursor.getDouble(3));
	            location.setLongitude(cursor.getDouble(4));
	            
	         location.setClean(cursor.getFloat(7));
         location.setReview(cursor.getString(8));
            locationList.add(location);
    		}while(cursor.moveToNext());
    
    		}
    		cursor.close();
    return locationList;

    		}


//Getting All Locations
public List<Locations> getAllLocations() {
    List<Locations> locationList = new ArrayList<Locations>();
    // Select All Query
    String selectQuery = "SELECT * FROM " + TABLE_NAME;

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
        do {
        	
            Locations location = new Locations();
            location.setID(Integer.parseInt(cursor.getString(0)));
            location.setName(cursor.getString(1));
             location.setAddress(cursor.getString(2));
            location.setLat(cursor.getDouble(3));
            location.setLongitude(cursor.getDouble(4));
           
            // Adding location to list
            locationList.add(location);
        } while (cursor.moveToNext());
    }
    cursor.close();
    // return location list
    return locationList;
}

// Deleting single location???????????????????????????????????????
public void deleteLocation(Locations location) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_NAME, KEY_ID + " = ?",
            new String[] { String.valueOf(location.getID()) });
    db.close();
}

// Getting contacts Count
public int getLocCount() {//(*)
  	int count = 0;
    String selectQuery = "SELECT  * FROM " + TABLE_NAME;

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows
    if (cursor.moveToFirst()) {
        do {
          count += 1;
        } while (cursor.moveToNext());
    }
Log.i("cursor", Integer.toString(count));
    cursor.close();
    //closed cursor???
    //Need more efficient way of getting count!!!!!!
    // return count
    return count;
}

public List<Locations> findNearBy(double curr_lat, double curr_longitude){
	SQLiteDatabase db = this.getWritableDatabase();
	Log.i("findNearBy", "start");
	List<Locations> locationList = new ArrayList<Locations>();
	double high_lat = curr_lat + 1.0;
	double high_long = curr_longitude + 1.0;
	double low_lat = curr_lat - 1.0;
	double low_long = curr_longitude - 1.0;
	Log.i("values", "clat " + curr_lat + " curr_longitude " + curr_longitude + " high_lat " + high_lat + " high_long " + high_long);
	//Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_ID, KEY_NAME, KEY_ADDR, KEY_LAT, KEY_LONG}, KEY_LAT + " AND " + KEY_LONG,
		//	new String[]
			//		{KEY_LAT + " BETWEEN CAST(" + low_lat + " AS REAL) AND CAST(" 
				//	+ high_lat + " AS REAL) AND " + KEY_LONG + " BETWEEN CAST(" + low_long + " AS REAL) AND CAST(" 
			//		+ high_long + " AS REAL)"},
			//	null, null, null);
	
	
	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
			KEY_LAT +  " BETWEEN " + low_lat + " AND " 
				+ high_lat + " AND " + KEY_LONG + " BETWEEN " + low_long + " AND " 
			+ high_long, null);
			
			
	  if (cursor.moveToFirst()) {
	        do {
	        	   Log.i("findNearBy", "loop");
	            Locations location = new Locations();
	            location.setID(Integer.parseInt(cursor.getString(0)));
	            location.setName(cursor.getString(1));
	             location.setAddress(cursor.getString(2));
	            location.setLat(cursor.getDouble(3));
	            location.setLongitude(cursor.getDouble(4));
	            Log.i("findNearBy", location.toString());
	            locationList.add(location);
	   	        } while (cursor.moveToNext());
	  }
	        cursor.close();
	        Log.i("findNearBy", locationList.toString());
	        
	        return locationList;
	        
	

}

public void latlngorder(){
	Log.i("latlngorder", "start");
	SQLiteDatabase db = this.getWritableDatabase();
	Cursor cursorlat = db.query(TABLE_NAME, new String[] {KEY_LAT}, null, null, null, null, KEY_LAT);
	Cursor cursorlong = db.query(TABLE_NAME, new String[] {KEY_LONG}, null, null, null, null, KEY_LONG);
	  if (cursorlat.moveToFirst() && cursorlong.moveToFirst()) {
	        do {
	        	double lat = cursorlat.getDouble(0);
	        	double longitude = cursorlong.getDouble(0);
	            Log.i("latlngorder", Double.toString(lat) +  Double.toString(longitude));
	   	        } while (cursorlat.moveToNext() && cursorlong.moveToNext());
	
}



}

public List<Locations> getAll(){
	SQLiteDatabase db = this.getWritableDatabase();
	 Log.i("getlatlnglocation", "start");
	 List<Locations> locationList = new ArrayList<Locations>();
	
Cursor cursor = 
db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
Log.i("cursor", Integer.toString(cursor.getCount()));
cursor.moveToPosition(0);
Log.i("cursor", Integer.toString(cursor.getCount()));
   		if(cursor.getCount() > 0){
   		do{
   			Locations location = new Locations();
	            location.setID(Integer.parseInt(cursor.getString(0)));
	            location.setName(cursor.getString(1));
	             location.setAddress(cursor.getString(2));
	            location.setLat(cursor.getDouble(3));
	            location.setLongitude(cursor.getDouble(4));
	            
	         location.setClean(cursor.getFloat(7));
	 location.setReview(cursor.getString(8));
           locationList.add(location);
   		}while(cursor.moveToNext());
   
   		}
   		cursor.close();
   return locationList;
	
	
	
	
}







public Cursor getCursor(String addr){
	SQLiteDatabase db = this.getWritableDatabase();
	 Log.i("getCursor", "start");
Cursor cursor = 
db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ADDR + " = '" + addr + "'", null);
cursor.moveToPosition(0);
Log.i("what's in cursor", cursor.getString(2));
   return cursor;
}



	
	
	
	
	
}










