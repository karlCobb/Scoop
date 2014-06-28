package com.example.mapmapagain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Inflater;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;




import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.util.Key;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.textservice.TextInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;


public class MainActivity extends Activity{
	/*
	 * Variables for storing data when activity is killed
	 */
	
	@SuppressWarnings("deprecation")
	
	static final String CHECKED_ADDRESS = "address";
	static final String CHECKED_GPS = "checked_gps";
	static final String CHECKED_LOCATION = "checked_location";
    String INTERNET = Variables.get_internet_address();
    String GPS_OFF = "GPS";
    SharedPreferences prefs;
    /*
	 * Variables for methods
	 */
	final Context c = this;
	MarkerPlaces mp;
	protected GoogleMap map;
	LatlngDbConversion latlngdb = new LatlngDbConversion();
	
	//used to prevent adding the same location twice by same user
	boolean checked_address;
	boolean checked_gps;
	boolean checked_location;
	
	//for address and gps
	boolean isNetworkEnabled = false;
	boolean isGPSEnabled = false;
	boolean internetConnectivity = false;
	
	//to stop async task from starting two instances

	
	@Override
    public void onCreate(Bundle savedInstanceState)
		{    		


		super.onCreate(savedInstanceState);
			 
			setContentView(R.layout.activity_main);
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			Log.i("mapFrag", "mapfrag");
			Log.i("Enter onCreate", "onCreate");

			
			if(savedInstanceState != null)
			  {
						/* 
						 * checked_address -- The 'would you like to add this location' does not appear during onRestart
						 * checked_gps -- The 'turn on location services' message does not appear during onRestart
						 * FindNearby displays bathrooms within preference distances
						 * map finds current location and sets frame
						 */
				Log.i("savedInstanceState", "SavedInstanceState");
				
				    	checked_address = savedInstanceState.getBoolean(CHECKED_ADDRESS, false);
				    	checked_gps = savedInstanceState.getBoolean(CHECKED_GPS, false);
				    	LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
						CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 14);
						map.animateCamera(update);
			  }
			Log.i("FindLocationGPS", "FindLocationGPS");
			FindLocationGPS();
	
			
	
 
		
map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	
	@Override
	public void onInfoWindowClick(Marker arg0) {	
		Log.i("mapInfoWindow", "InfoWindow");	
		if(arg0.getTitle().toString().trim().compareTo("You are here") != 0)
		{
			Intent i = new Intent(getApplicationContext(), ListReview.class);
			i.putExtra("address", arg0.getTitle());
			startActivity(i);}
		}
});



/*
 * Sets data in infowindow
 */

map.setInfoWindowAdapter(new InfoWindowAdapter(){

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		
		
		Locations l;
		View v = null;
		if(arg0.getTitle().toString().trim().compareTo("You are here") != 0)
		{
			v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
			l = new Locations(MarkerPlaces.places.get(arg0.getId()));

		
		TextView name = (TextView) v.findViewById(R.id.getName);
		TextView address = (TextView) v.findViewById(R.id.getAddress);
		RatingBar clean = (RatingBar) v.findViewById(R.id.getClean);
		ImageView handicapped = (ImageView) v.findViewById(R.id.handiImage);
		ImageView free = (ImageView) v.findViewById(R.id.freeImage);
		clean.setRating(l.clean);
		
		name.setText(l.getName().toString());
		String text = l.getAddress().toString();
		if(text.length() < 30)
			address.setText(text);
			else
			{
			int last = text.length();
			int comma = text.indexOf(",");

			String text1 = text.substring(0, comma+2);
			text1 += "\n"; 
			text1 += text.substring(comma+2, last);
			address.setText(text1);
			}
		
		
	/*
		if(l.getHandicapped() == true)
			handicapped.setId(R.drawable.handicapped);
			else
				handicapped.setId(R.drawable.not_handicapped);
			
		if(l.getPub() == true)
			free.setId(R.drawable.money);
			else
				free.setId(R.drawable.nomoney);
		*/
		}else{
			/*
			TextView title = (TextView) v.findViewById(R.id.getName);
			TextView address = (TextView) v.findViewById(R.id.getAddress);
			RatingBar clean = (RatingBar) v.findViewById(R.id.getClean);
			ImageView handicapped = (ImageView) v.findViewById(R.id.handiImage);
			ImageView free = (ImageView) v.findViewById(R.id.freeImage);
			title.setText(arg0.getTitle().toString());
			title.setGravity(Gravity.CENTER_HORIZONTAL);
			address.setText("");
			handicapped.clearAnimation();
			free.clearAnimation();
			return v;
		*/
		}
		return v;
	
		
	}
	
});

}
	
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo ni = cm.getActiveNetworkInfo();
	    if (ni!=null && ni.isAvailable() && ni.isConnected()) {
	        return true;
	    } else {
	    	return false; 
	    }
	}
	
	private void FindLocationGPS(){
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	    LocationListener loclist = new myLocationListener();
	    
		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);	
    if (isNetworkEnabled) {
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    6000,
                    100, loclist);
            Log.d("Network", "Network");}
        else if (isGPSEnabled) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    6000,
                    100, loclist);
            Log.d("GPS", "GPS");}			
		else if(checked_gps == false){
				gpsSettings();
				checked_gps = true;
		}	
	}
	
	public void FindLocationGPS(View v){
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	    LocationListener loclist = new myLocationListener();
	    
		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);	
    if (isNetworkEnabled) {
    	lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, loclist);

            Log.d("Network", "Network");}
        else if (isGPSEnabled) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0, loclist);

            Log.d("GPS", "GPS");}			
		else{
			gpsSettings();
		}
			
    }
	

	private void gpsSettings(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Press Settings to enable GPS.");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        

        alertDialog.setNeutralButton("Manually Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GPSOffCurrLocation();			
			}
		});
        
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.create();
        alertDialog.show();
    }
     
	
	
	
	
/*
*finds and updates the current location of the user	 
*/
   
public class myLocationListener implements LocationListener{
 double loclist_lat;
 double loclist_lng;
	
	
    	
    	@Override
    	public void onLocationChanged(Location location) {
	    	Log.i("Enter Location Listener", Double.toString(loclist_lat) + " " + Double.toString(loclist_lng));
    	Log.i("Enter Location Listener", "Enter");
    		if(location != null)
    		{
    	    	Log.i("Enter Location Listener", "not null");
    		if((location.getLatitude() <= loclist_lat - .0001 || location.getLatitude() >= loclist_lat + .0001) &&
    				(location.getLongitude() <= loclist_lng - .0001 || location.getLongitude() >= loclist_lng + .0001)){
    		Variables.curr_lat = location.getLatitude();
    		Variables.curr_lng = location.getLongitude();
    		LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
    		map.addMarker(new MarkerOptions().position(position).title("You are here")
    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 14);
    		map.animateCamera(update);
    		loclist_lat = location.getLatitude();
    		loclist_lng = location.getLongitude();
    		Log.i("Variables", Variables.curr_lat + " " + Variables.curr_lng);
    		new GeoCodeLatlng().execute(Variables.curr_lat, Variables.curr_lng);
    		
    		}
    		//also check if address changed significantly???
    		//This will not get called because checked_address tells if the question was asked not if it were added.
    		/*
    		 * MARK FOR DELETION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    		 
    		if(checked_address == false)
    		{
    			new GeoCodeLatlng().execute(Variables.curr_lat, Variables.curr_lng);
    		}//end if checked_address
    		*/

    	}
    	}

    	@Override
    	public void onProviderDisabled(String provider) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void onProviderEnabled(String provider) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		// TODO Auto-generated method stub
    		
    	}
    	
    }


public void GPS(View v){
	 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
     startActivity(intent);
}


//displays db locations on map and updates camera to point at curr location
private void map_display() throws IOException{
	
	
	List<Locations> l = new ArrayList<Locations>();
	double lat;
	double longitude;
	String addr;
		    LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 14);
			map.animateCamera(update);
			
		FindNearby findnearby = new FindNearby();
		findnearby.execute(Variables.curr_lat, Variables.curr_lng);			
	}
	


private void nosuchaddress(String original_addr){
AlertDialog.Builder nosuchaddress = new AlertDialog.Builder(this);
nosuchaddress.setMessage("Sorry we cannot find your address");
nosuchaddress.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
		
	}
});
nosuchaddress.create();
nosuchaddress.show();
}

/*
 * Automatically asks if the current location should be added
 */

private void add_location_question() throws IOException{
AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	// set title1`	
	alertDialogBuilder.setTitle("Confirm");
alertDialogBuilder
.setMessage("Would you like to add your current location: " + Variables.curr_addr	+ "\n")
.setCancelable(false)
.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

	public void onClick(DialogInterface dialog,int id) {
	//if clicked send lat, lng, and addr to get name of place and more specific info
     if(Variables.curr_addr != null){
		CheckAddressExist checkaddressexist = new CheckAddressExist();
        checkaddressexist.execute(Variables.curr_addr);
     }else{
 		GPSOffCurrLocation();
     }
	}
  })  
  .setNegativeButton("No",new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
			// if this button is clicked, just close
			// the dialog box and do nothing
			
			dialog.cancel();
		}
		
	}
	);
alertDialogBuilder.create();
alertDialogBuilder.show();
}



@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
    //Save the user's current state
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean(CHECKED_ADDRESS, checked_address);
    savedInstanceState.putBoolean(CHECKED_GPS, checked_gps);
    savedInstanceState.putBoolean(CHECKED_LOCATION, checked_location);
}




@Override
public void onResume(){
super.onResume();
Log.i("Enter onResume", "onResume");

Log.i("onResume", "onResume");
if(Variables.LOCKED == false){
	Variables.LOCKED = true;
FindNearby findnearby = new FindNearby();
findnearby.execute(Variables.curr_lat, Variables.curr_lng);
}
}
@Override
public void onRestart(){
	super.onRestart();
}

@Override
public void onPause(){
super.onPause();
}

@Override
public void onStop(){
super.onStop();
}




/*
 *Menu builder and menu click settings 
 */
public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.mainmenu, menu);
	  return true;
}


	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		  // We have only one menu option
		  case R.id.prefs:
		    // Launch Preference activity
		    Intent i = new Intent(this, Preferences.class);
		    startActivity(i);
		    // Some feedback to the user
		    Toast.makeText(this, "Enter search parameters.",
		      Toast.LENGTH_LONG).show();
		    break;
		  case R.id.exit:
			  finish();
		}
		return super.onOptionsItemSelected(item);
	}



/*
 * Manually add the current location
 * GodCodes address and then checks address before showing review page to user
 */
	
public void AddCurrentLocation(View v){
//	if(Variables.curr_addr != ""){
CheckAddressExist checkaddressexist = new CheckAddressExist();
checkaddressexist.execute(Variables.curr_addr);
//	}
//	else{
//		GPSOffCurrLocation();
//	}

	}

/*
 * Manually add a location from anywhere
 * Follows similar route as AddCurrentLocation
 * Goes to GeocodeAddress before adding it
 */

public void addAnotherLocation(View v){
	
	AlertDialog.Builder addlocationform = new AlertDialog.Builder(this);
	LayoutInflater inflater = getLayoutInflater();
	addlocationform.setMessage("Please enter a location to search for: ");
	addlocationform.setView(inflater.inflate(R.layout.custom_alert_dialog, null)).setPositiveButton("Set", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Dialog dial = (Dialog) dialog;
			
			EditText putaddress = (EditText) dial.findViewById(R.id.searchAddress);
			String search_address = putaddress.getText().toString();
			if(search_address.isEmpty() || search_address == null)
				{dialog.cancel();}
			GeoCodeAddress geocode = new GeoCodeAddress();
			geocode.execute(search_address, GPS_OFF);

		}
	})
	.setNegativeButton("Cancel", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
			
		}
	});
	addlocationform.create();
	addlocationform.show();	
	
}

public void connectToNetwork(){
	
 Log.i("connectToNetwork", "connectToNetwork");
	AlertDialog.Builder connectToNetwork = new AlertDialog.Builder(this);
	
 Log.i("connectToNetwork", "connectToNetwork");
    connectToNetwork.setTitle("No Network Connectivity");
	
 Log.i("connectToNetwork", "connectToNetwork");
 
	connectToNetwork.setMessage("This application does not work without internet.  Please connect to the internet or quit.");
	
 Log.i("connectToNetwork", "connectToNetwork");
	connectToNetwork.setPositiveButton("go to network settings", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			 Log.i("connectToNetwork", "connectToNetwork");
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			startActivity(intent);
		}
	}).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			 Log.i("connectToNetwork", "connectToNetwork");
			dialog.cancel();
		}
	});
	 Log.i("connectToNetwork", "connectToNetwork");
	connectToNetwork.create();
	 Log.i("connectToNetwork", "connectToNetwork");
	connectToNetwork.show();
	 Log.i("connectToNetwork", "connectToNetwork");
	
	}

public void Refresh(View v){
	FindNearby findnearby = new FindNearby();
	findnearby.execute(Variables.curr_lat, Variables.curr_lng);
}


public void GPSOffCurrLocation(){
	
	AlertDialog.Builder addlocationmanually = new AlertDialog.Builder(this);
	addlocationmanually.setMessage("Enter your location or click Settings to turn on your GPS");
	LayoutInflater inflater = getLayoutInflater();
	addlocationmanually.setView(inflater.inflate(R.layout.custom_alert_dialog, null));
	
	addlocationmanually.setPositiveButton("Enter", new OnClickListener() {
	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			  
		
	
			
			Dialog dial = (Dialog) dialog;
			EditText putaddress = (EditText) dial.findViewById(R.id.searchAddress);
			String search_address = putaddress.getText().toString();
			if(search_address.isEmpty() || search_address == null)
				{dialog.cancel();}
			GeoCodeAddress geocode = new GeoCodeAddress();
			geocode.execute(search_address, GPS_OFF);
			
		
		}
	})
	.setNegativeButton("Cancel", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
			
		}
	});
	addlocationmanually.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	               startActivity(intent);
			}
		});

	addlocationmanually.create();
	addlocationmanually.show();	
	
}



/*
 * Grabs the shared preferences before updating markers on screen
 */

class FindNearby extends AsyncTask<Double, Void, JSONArray>
{


	@Override
	protected JSONArray doInBackground(Double...params) {
		Log.i("Enter findnearby", "findnearby");
	//	StringBuilder builder = new StringBuilder();
		final String uri = INTERNET + "/find_nearbypref.php?";
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
		

	//	String location_pref = prefs.getString("LOCATION", "0");

		String dist_pref = prefs.getString("RADIUS", "100");
		double dist;
		
		if(dist_pref.compareTo("") == 0)
			dist = .25;
		else{
		int dist_int = Integer.parseInt(dist_pref);
		dist = dist_int/100.0;
		}
		
		int rating_int = 0;
		String rating_string = prefs.getString("RATING", "0");
		if(rating_string.compareTo("") == 0){
			rating_int = 0;
		}else{
		rating_int = Integer.parseInt(rating_string);
		}
		
		
		/*
		 * Send currlatlng and dist to find minlatlng maxlatlng
		 */
		
		HashMap bound_latlng = new HashMap<String, Double>();
		bound_latlng = boundingCoordinates(dist, params[0], params[1]);
		double minLat = (Double) bound_latlng.get("minLat");
		double minLng = (Double) bound_latlng.get("minLng");
		double maxLat = (Double) bound_latlng.get("maxLat");
		double maxLng = (Double) bound_latlng.get("maxLng");
		
		String minlat = latlngdb.latDb(minLat);
		String minlng = latlngdb.lngDb(minLng);
		String maxlat = latlngdb.latDb(maxLat);
		String maxlng = latlngdb.lngDb(maxLng);
		
		//Grab locations which fit inside radius and rating parameters
		
        HttpClient client = new DefaultHttpClient();
        String url = uri + "&min_lat=" + minlat + "&max_lat=" + maxlat + 
        		"&min_lng=" + minlng + "&max_lng=" + maxlng + "&rating=" + rating_int;
		HttpGet httpGet =  new HttpGet(url);
        StringBuilder builder = new StringBuilder();
        org.apache.http.HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
      
        try {
                
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) 
                    {
                      builder.append(line);
                    }//end while
   
                       JSONArray jsonarray = new JSONArray(builder.toString());
                     return jsonarray;
                     
                  
                }//end if
        
	else {
                      
               }}catch (Exception e){
                   Log.e("log_tag", "Error in http connection" +e.getMessage());
               }//end try catch
               return null;
	
	}//end function

	@Override
	protected void onPostExecute(JSONArray jsonarray) {
		
		//put locations on map after clearing map.  Also add markers
		
		mp = new MarkerPlaces();
		mp.places.clear();
		map.clear();
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		 try {
		super.onPostExecute(jsonarray);
		Locations l = new Locations();
		
		for(int i = 0; i < jsonarray.length(); i += 1)
        {	
			
			LatLng position = null;
			JSONObject json = new JSONObject();
			json = (JSONObject) jsonarray.get(i);
	
		
			
			//convert latlng to human readable
			double latitude = latlngdb.latFromDb(json.getString("latitude"));
			double longitude = latlngdb.lngFromDb(json.getString("longitude"));
			position = new LatLng(latitude, longitude);
			
     	l.setAddress(json.getString("address"));
        l.setName(json.getString("name"));
        l.setPub(Boolean.parseBoolean(json.getString("free")));
        l.setHandicapped(Boolean.parseBoolean(json.getString("handicapped")));
        l.setClean(Float.parseFloat(json.getString("ratings")));
        l.setLongitude(longitude);
        l.setLat(latitude);


			Marker marker = 
					map.addMarker(new MarkerOptions()
		    .position(position).title(json.getString("address"))
		    .icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));
			marker.setVisible(true);


			Locations load_location = new Locations(l);
			mp.storePlaces(marker.getId(), load_location);
        
			
			
        }//end for statement
		LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
		map.addMarker(new MarkerOptions().position(position).title("You are here")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));

        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(checked_address == false){
		 try {
			add_location_question();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 checked_address = true;
		 }
Variables.LOCKED = false; 
	}
	
	
}


/*
 * Geocodeaddress takes the address and finds its lat and lng
 * If params[1] which is the flag, is set to GPS_OFF it is stored in
 * Variables.curr_* and then follows the route of normal GPS locations to
 * ask whether or not to store location.
 * otherwise it is considered stored in Variables.geocoded_*
 * and sent to Add_location.class
 * Currently it is not being used when GPS is on.
 * 
 */


class GeoCodeAddress extends AsyncTask<String, Void, JSONObject>{

	@Override
	protected JSONObject doInBackground(String...params) {
	    String FLAG = params[1];
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("address", params[0]));
		 HttpClient client = new DefaultHttpClient();
		 String url = null;
		 try {
	        url = "http://maps.googleapis.com/maps/api/geocode/json?address=" +
	        URLEncoder.encode(params[0], "UTF-8") + "&sensor=false";
				
			} catch (UnsupportedEncodingException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
	        HttpGet httpGet =  new HttpGet(url);
	        HttpEntity url_entity = null;
			try {
				url_entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        httpGet.addHeader(url_entity.getContentType());
	        StringBuilder builder = new StringBuilder();
	        org.apache.http.HttpResponse response = null;
			try {
				response = client.execute(httpGet);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	      
	        try {
	                
	                StatusLine statusLine = response.getStatusLine();
	                int statusCode = statusLine.getStatusCode();
	                if (statusCode == 200) {
	                    HttpEntity entity = response.getEntity();
	                    InputStream content = entity.getContent();
	                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	                    String line;
	                    while ((line = reader.readLine()) != null) 
	                    {
	                      builder.append(line);
	                    }//end while
	   
	                       JSONObject jsonobject = new JSONObject(builder.toString());
	                       if(jsonobject.getString("status").compareTo("OK") != 0)
		           			{ 
	                    	   jsonobject.put("original_addr", params[0]);
		           				return jsonobject;
		           			}
	                       JSONArray results = jsonobject.getJSONArray("results");
	                       JSONObject address = results.getJSONObject(0);
	                 
	                       address.put("FLAG", FLAG);
	                      
		           			
	                       return address;}
	        
		else {
	                      
	               }}catch (Exception e){
	                   Log.e("log_tag", "Error in http connection" +e.getMessage());
	               }//end try catch
	       // publishProgress(1);
			return null;
			

		
	}
	


	@Override
	protected void onPostExecute(JSONObject address){
		super.onPostExecute(address);
		
		
		try {
			if(address.has("original_addr") == true)
			{
				/*
				 * Address does not exist
				 */
				nosuchaddress(address.getString("original_addr"));
			}
			else if(address.getString("FLAG").compareTo(GPS_OFF) == 0){
				/*
				 * GPS is off adds current location.
				 * Used for set location
				 */
				Variables.curr_addr = address.getString("formatted_address");
				JSONObject geometry = address.getJSONObject("geometry");
				JSONObject latlng = geometry.getJSONObject("location");
				Variables.curr_lat =  latlng.getDouble("lat");
				Variables.curr_lng = latlng.getDouble("lng");
				   
				/*
				 * Refresh() method used here? Reconsider name     
				 */
				
				LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
			    map.addMarker(new MarkerOptions().position(position).title("You are here")
			    .icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));
			    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 14);
			    map.animateCamera(update);
				FindNearby findnearby = new FindNearby();
				findnearby.execute(Variables.curr_lat, Variables.curr_lng);
				 	
			}
   else{
	   /*
	    * 
	    * Go to review location
	    */
			Variables.geocoded_addr = address.getString("formatted_address");
			JSONObject geometry = address.getJSONObject("geometry");
			JSONObject latlng = geometry.getJSONObject("location");
			Variables.geocoded_lat =  latlng.getDouble("lat");
			Variables.geocoded_lng = latlng.getDouble("lng");
			CheckAddressExist checkaddr = new CheckAddressExist();
			checkaddr.execute(Variables.geocoded_addr);
   }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
 }//end if


}

/*
 * Check if the address exists -- If the address exists fills in the address section of the review form
 * Should it fill in the name too?  People might have different names for same address.
 * An address may also have many places in same building. 
 */


class CheckAddressExist extends AsyncTask<String, Void, JSONObject>{

	
	
	@Override
	protected JSONObject doInBackground(String... params) {
		String INTERNET = Variables.get_internet_address();
		if(params[0] == null)
		{/*If blank
		*tell user address is blank.  Or tell user address doesn't exist. Then exit
	*/
			Log.i("checkaddressis blank", "blank");
			}
			HttpClient client = new DefaultHttpClient();
		
	        String url = INTERNET + "/address_exist.php?address=" + URLEncoder.encode(params[0]);
			HttpGet httpGet =  new HttpGet(url);
	        StringBuilder builder = new StringBuilder();
	        org.apache.http.HttpResponse response = null;
			try {
				response = client.execute(httpGet);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	      
	        try {
	                
	                StatusLine statusLine = response.getStatusLine();
	                int statusCode = statusLine.getStatusCode();
	                if (statusCode == 200) {
	                    HttpEntity entity = response.getEntity();
	                    InputStream content = entity.getContent();
	                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	                    String line;
	                    while ((line = reader.readLine()) != null)
	                    {
	                      builder.append(line);
	                    }//end while
	                    JSONArray jsonarray = new JSONArray(builder.toString());
	                    JSONObject jsonobject = jsonarray.getJSONObject(0);
	                       if(jsonobject.getString("success").compareTo("0") == 0)
	                       {
	                    	   
	                    	   jsonobject.put("address", params[0]);
	                       }
	                       
	                    

	               
	                
	                       return jsonobject;
	                }//end if
	    	        
	        		else {
	        	                      
	        	               }}catch (Exception e){
	        	                   Log.e("log_tag", "Error in http connection" +e.getMessage());
	        	               }//end try catch
return null;
	        		
	        		}//end function
@Override
protected void onPostExecute(JSONObject bathroom) {
	// TODO Auto-generated method stub
	super.onPostExecute(bathroom);
	Intent i = new Intent(getApplicationContext(), AddLocation.class);
	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	  try {
    if(bathroom.has("name") == false){
     i.putExtra("address", bathroom.getString("address"));
 	 i.putExtra("name", "name");
     i.putExtra("handicapped", "handicapped");
     i.putExtra("free", "free");
    }
    else{
    	i.putExtra("address", bathroom.getString("address"));
    	i.putExtra("name", bathroom.getString("name"));
    	i.putExtra("handicapped", bathroom.getString("handicapped"));
    	i.putExtra("free", bathroom.getString("free"));
    	}
    startActivity(i);
    
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	
	
}


}	

/*
 * Geocodelatlng takes lat and lng and params2 could be used as a flag
 * Currently the only task to use it is to set the current location from GPS
 * At the end it sends curr_lat curr_lng to findnearby method
 */


class GeoCodeLatlng extends AsyncTask<Double, Void, Void>{
	@Override
	protected Void doInBackground(Double...params) {
		
		
		 HttpClient client = new DefaultHttpClient();
		 String url = null;
		
		 try {
			 	 url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" +
					        URLEncoder.encode(params[0] + "," + params[1], "UTF-8") + "&sensor=false";
			} catch (UnsupportedEncodingException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
	        HttpGet httpGet =  new HttpGet(url);
	        StringBuilder builder = new StringBuilder();
	        org.apache.http.HttpResponse response = null;
			try {
				response = client.execute(httpGet);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	      
	        try {
	                
	                StatusLine statusLine = response.getStatusLine();
	                int statusCode = statusLine.getStatusCode();
	                if (statusCode == 200) {
	                    HttpEntity entity = response.getEntity();
	                    InputStream content = entity.getContent();
	                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	                    String line;
	                    while ((line = reader.readLine()) != null) 
	                    {
	                      builder.append(line);
	                    }//end while
	   
	                       JSONObject jsonobject = new JSONObject(builder.toString());
	                   
	                       JSONArray results = jsonobject.getJSONArray("results");
	                       JSONObject address = results.getJSONObject(0);
	
	           		Variables.curr_addr = address.getString("formatted_address");
	        		FindNearby findnearby = new FindNearby();
	        		findnearby.execute(Variables.curr_lat, Variables.curr_lng);
	           			
	           			

	                  
	                }//end if
	        
		else {
	                      
	               }}catch (Exception e){
	                   Log.e("log_tag", "Error in http connection" +e.getMessage());
	               }//end try catch
			return null;

		
	}
	
	
}
public HashMap<String, Double> boundingCoordinates(double distance, double Lat, double Lon) {
double radius = 6371.0;
double radLat = (Lat*Math.PI)/180;
double radLon = (Lon*Math.PI)/180;
HashMap bounded_latlng = new HashMap<String, Double>(4);
	if (radius < 0 || distance < 0)
		throw new IllegalArgumentException();

	// angular distance in radians on a great circle
	double radDist = distance / radius;

	double minLat = radLat - radDist;
	double maxLat = radLat + radDist;

	double minLon, maxLon;
	
		double deltaLon = Math.asin(Math.sin(radDist) /
			Math.cos(radLat));
		minLon = radLon - deltaLon;
		maxLon = radLon + deltaLon;
		minLon = (minLon*180)/Math.PI;
		minLat = (minLat*180)/Math.PI;
		maxLat = (maxLat*180)/Math.PI;
		maxLon = (maxLon*180)/Math.PI;
		
		
		bounded_latlng.put("minLng", minLon);
		bounded_latlng.put("minLat", minLat);
		bounded_latlng.put("maxLng", maxLon);
		bounded_latlng.put("maxLat", maxLat);

	return bounded_latlng;
	}


}








