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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
	
	
	@Override
    public void onCreate(Bundle savedInstanceState)
		{    		


			super.onCreate(savedInstanceState); 
			setContentView(R.layout.activity_main);
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	
			//Dummy location for testing purposes
			LatLng position = new LatLng(40.807429, -73.964437);
			map.addMarker(new MarkerOptions().position(position).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));
			Variables.curr_lat = 40.807429;
			Variables.curr_lng = -73.964437;
			if(savedInstanceState != null)
				  {
					    	checked_address = savedInstanceState.getBoolean(CHECKED_ADDRESS, false);
					    	checked_gps = savedInstanceState.getBoolean(CHECKED_GPS, false);
					    	FindNearby findnearby = new FindNearby();
					    	findnearby.execute(Variables.curr_lat, Variables.curr_lng);
				  }
			
			
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
	        else if (isNetworkEnabled) {
                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        6000,
                        100, loclist);
                Log.d("Network", "Network");}			
			else{
				if(checked_gps == false){
					gpsSettings();
					
					//for emulator bc can't geolocate
					FindNearby findnearby = new FindNearby();
					findnearby.execute(Variables.curr_lat, Variables.curr_lng);
					checked_gps = true;
				}else{
				FindNearby findnearby = new FindNearby();
				findnearby.execute(Variables.curr_lat, Variables.curr_lng);;	
				}
			}
				
			

				
			
	
 
		
map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	
	@Override
	public void onInfoWindowClick(Marker arg0) {	
		
		if(arg0.getTitle().toString().trim().compareTo("You are here") != 0)
		{
			Log.i("infowindowinfo", arg0.getTitle() + " " + arg0.getPosition().toString());
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
		View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		if(arg0.getTitle().toString().trim().compareTo("You are here") != 0)
		{
			Log.i("arg0", arg0.getId());
			l = new Locations(MarkerPlaces.places.get(arg0.getId()));
			Log.i("Return direct", mp.places.get(arg0.getId()).getAddress());
		Log.i("location getinfowindow", l.getAddress().toString());
		
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
			Log.i("last", Integer.toString(last));
			String text1 = text.substring(0, comma+2);
			Log.i("text1", text1);
			Log.i("comma", Integer.toString(comma));
			text1 += "\n"; 
			text1 += text.substring(comma+2, last);
			Log.i("text2", text1);
			address.setText(text1);
			}
		
		
	
		if(l.getHandicapped() == true)
			handicapped.setId(R.drawable.handicapped);
			else
				handicapped.setId(R.drawable.not_handicapped);
			
		if(l.getPub() == true)
			free.setId(R.drawable.money);
			else
				free.setId(R.drawable.nomoney);
		return v;
		}else{
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
		}
	
	
		
	}
	
});

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
            

    	
    		if(location != null)
    		{
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
    		}
    		//also check if address changed significantly???
    		if(checked_address == false)
    		{
    			new GeoCodeLatlng().execute(Variables.curr_lat, Variables.curr_lng);
    		}//end if checked_address
    		

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
	Log.i("map_display", "1");
	List<Locations> l = new ArrayList<Locations>();
	double lat;
	double longitude;
	String addr;
		    LatLng position = new LatLng(Variables.curr_lat, Variables.curr_lng);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 14);
			map.animateCamera(update);
			
		FindNearby getfromserver = new FindNearby();
		getfromserver.execute(Variables.curr_lat, Variables.curr_lng);			
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
        CheckAddressExist checkaddressexist = new CheckAddressExist();
        checkaddressexist.execute(Variables.curr_addr);
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
public void onPause(){
super.onPause();
Log.i("onpause()", "++ ON PAUSE ++");
}

@Override
public void onStop(){
super.onStop();
Log.i("onStop()", "++ ON STOP ++");
}



@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
    //Save the user's current game state
	Log.i("save", savedInstanceState.toString());
    savedInstanceState.putBoolean(CHECKED_ADDRESS, checked_address);
    savedInstanceState.putBoolean(CHECKED_GPS, checked_gps);
    savedInstanceState.putBoolean(CHECKED_LOCATION, checked_location);
    // Always call the superclass so it can save the view hierarchy state
    
    Log.i("save", savedInstanceState.toString());
    super.onSaveInstanceState(savedInstanceState);
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
		    Toast.makeText(this, "Enter default location and search parameters.",
		      Toast.LENGTH_LONG).show();
		    break;
		  case R.id.exit:
			  finish();
		}
		return super.onOptionsItemSelected(item);
	}




public void AddCurrentLocation(View v){
	if(Variables.curr_addr != null){
CheckAddressExist checkaddressexist = new CheckAddressExist();
checkaddressexist.execute(Variables.curr_addr);
	}
	else{
		nosuchaddress(Variables.curr_addr);
	}
	}

public void addAnotherLocation(View v){
	
	AlertDialog.Builder addlocationform = new AlertDialog.Builder(this);
	LayoutInflater inflater = getLayoutInflater();
	addlocationform.setMessage("Please enter a location to search for: ");
	addlocationform.setView(inflater.inflate(R.layout.custom_alert_dialog, null)).setPositiveButton("Review", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Dialog dial = (Dialog) dialog;
			
			EditText putaddress = (EditText) dial.findViewById(R.id.searchAddress);
			String search_address = putaddress.getText().toString();
			if(search_address.isEmpty() || search_address == null)
				{dialog.cancel();}
			Log.i("search address", search_address);
			GeoCodeAddress geocode = new GeoCodeAddress();
			geocode.execute(search_address, "NOT");
			Log.i("search address", search_address);

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
			Log.i("search address", search_address);
			GeoCodeAddress geocode = new GeoCodeAddress();
			geocode.execute(search_address, GPS_OFF);
			Log.i("search address", search_address);
		
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





class FindNearby extends AsyncTask<Double, Void, JSONArray>
{


	@Override
	protected JSONArray doInBackground(Double...params) {
		
	
	//	StringBuilder builder = new StringBuilder();
		final String uri = INTERNET + "/find_nearbypref.php?";
		
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String location_pref = prefs.getString("LOCATION", "0");

		String dist_pref = prefs.getString("RADIUS", "100");
		double dist;
		
		if(dist_pref.compareTo("") == 0)
			dist = .25;
		else{
		int dist_int = Integer.parseInt(dist_pref);
		Log.i("dist_pref", dist_pref);
		dist = dist_int/100.0;
		Log.i("dist", Double.toString(dist));
		}
		
		int rating_int = 0;
		String rating_string = prefs.getString("RATINGS", "0");
		if(rating_string.compareTo("") == 0){
			rating_int = 0;
		}else{
		rating_int = Integer.parseInt(rating_string);
		}
		
		
		
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
		
		
		Log.i("minmaxlatlng", minlat + " " + maxlat  + " " + minlng + " " +  maxlng);
		
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
                       Log.i("findNEARBY jsonarray", jsonarray.toString());
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
		mp = new MarkerPlaces();
		
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
			
		Log.i("jsonobject", json.toString());
     	l.setAddress(json.getString("address"));
        l.setName(json.getString("name"));
        l.setPub(Boolean.parseBoolean(json.getString("free")));
        l.setHandicapped(Boolean.parseBoolean(json.getString("handicapped")));
        l.setClean(Float.parseFloat(json.getString("ratings")));
        Log.i("ratings from db", json.getString("ratings"));
        l.setLongitude(longitude);
        l.setLat(latitude);
		Log.i("latlng", Double.toString(position.latitude));
		Log.i("latlng map", position.toString());


			Marker marker = 
					map.addMarker(new MarkerOptions()
		    .position(position).title(json.getString("address"))
		    .icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet)));
			marker.setVisible(true);

			
			Locations load_location = new Locations(l);
			mp.storePlaces(marker.getId(), load_location);
        
			
			
        }//end for statement
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
        }
	
}


/*
 * Geocodeaddress takes the address and finds its lat and lng
 * If params[1] which is the flag, is set to GPS_OFF it is stored in
 * Variables.curr_* and then follows the route of normal GPS locations to
 * ask whether or not to store location.
 * otherwise it is considered stored in Variables.geocoded_*
 * and sent to Add_location.class
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
	       Log.i("url", url.toString());
				
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
	                       Log.i("GEO Jsonarray", jsonobject.toString());
	                       if(jsonobject.getString("status").compareTo("OK") != 0)
		           			{ 
	                    	   jsonobject.put("original_addr", params[0]);
		           				return jsonobject;
		           			}
	                       JSONArray results = jsonobject.getJSONArray("results");
	                       JSONObject address = results.getJSONObject(0);
	                 
	                       Log.i("flag", FLAG);
	                       address.put("FLAG", FLAG);
	                      
	                       Log.i("onPostExecute", address.toString());
		           			
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
			Log.i("onPostExecute", address.toString());
			if(address.has("original_addr") == true)
			{
				nosuchaddress(address.getString("original_addr"));
			}
			else if(address.getString("FLAG").compareTo(GPS_OFF) == 0){
	   Variables.curr_addr = address.getString("formatted_address");
				Log.i("curr_addr", Variables.curr_addr);
				JSONObject geometry = address.getJSONObject("geometry");
				JSONObject latlng = geometry.getJSONObject("location");
				        Variables.curr_lat =  latlng.getDouble("lat");
				        Variables.curr_lng = latlng.getDouble("lng");
				     Log.i("curr latitude", Double.toString(Variables.curr_lat));
				     
				FindNearby findnearby = new FindNearby();
				findnearby.execute(Variables.curr_lat, Variables.curr_lng);
				 	
			}
   else{
			Variables.geocoded_addr = address.getString("formatted_address");
			Log.i("curr_addr", Variables.geocoded_addr);
			JSONObject geometry = address.getJSONObject("geometry");
			JSONObject latlng = geometry.getJSONObject("location");
			        Variables.geocoded_lat =  latlng.getDouble("lat");
			        Variables.geocoded_lng = latlng.getDouble("lng");
			     Log.i("GeoCode latitude", Double.toString(Variables.geocoded_lat));
			     CheckAddressExist checkaddr = new CheckAddressExist();
			     checkaddr.execute(Variables.geocoded_addr);
   }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
 }//end if


}



class CheckAddressExist extends AsyncTask<String, Void, JSONObject>{

	
	
	@Override
	protected JSONObject doInBackground(String... params) {
		String INTERNET = Variables.get_internet_address();
		if(params[0] == null)
		{
			Log.i("checkaddressis blank", "blank");}
			HttpClient client = new DefaultHttpClient();
		
	        String url = INTERNET + "/address_exist.php?address=" + URLEncoder.encode(params[0]);
			HttpGet httpGet =  new HttpGet(url);
	        StringBuilder builder = new StringBuilder();
	        Log.i("check address exist", url.toString());
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
	                    Log.i("get success", jsonobject.get("success").toString());
	                       if(jsonobject.getString("success").compareTo("0") == 0)
	                       {
	                    	   
	                    	   jsonobject.put("address", params[0]);
	                    	   Log.i("success???", jsonobject.toString());
	                       }
	                       
	                    

	                 
	                       Log.i("Jsonobject AddLocation", jsonobject.toString());
	                
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
	Log.i("onpostexecute", bathroom.toString());
	Intent i = new Intent(getApplicationContext(), AddLocation.class);
	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	  try {
			Log.i("onpostexecute", bathroom.toString());
    if(bathroom.has("name") == false){
    	Log.i("not added", "not added");
     i.putExtra("address", bathroom.getString("address"));
 	 i.putExtra("name", "name");
     i.putExtra("handicapped", "handicapped");
     i.putExtra("free", "free");
    }
    else{
    	Log.i("Added", "added");
    	i.putExtra("address", bathroom.getString("address"));
    	i.putExtra("name", bathroom.getString("name"));
    	i.putExtra("handicapped", bathroom.getString("handicapped"));
    	i.putExtra("free", bathroom.getString("free"));
    	}
    Log.i("go to next", i.toString());
    startActivity(i);
 
    
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	
	
}


}	

/*
 * Geocodelatlng takes lat and lng and params2 could be used as a flag
 * Currently the only task to use it is from find current location
 * Everything else uses geocode address
 * 
 */


class GeoCodeLatlng extends AsyncTask<Double, Void, Void>{
	@Override
	protected Void doInBackground(Double...params) {
		
		
		 HttpClient client = new DefaultHttpClient();
		 String url = null;
		
		 try {
			 	 url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" +
					        URLEncoder.encode(params[0] + "," + params[1], "UTF-8") + "&sensor=false";
					       Log.i("url", url.toString());
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
	                       Log.i("latlng Jsonarray", jsonobject.toString());
	                       JSONArray results = jsonobject.getJSONArray("results");
	                       JSONObject address = results.getJSONObject(0);
	
	           		Variables.curr_addr = address.getString("formatted_address");
	        		FindNearby findnearby = new FindNearby();
	        		findnearby.execute(Variables.curr_lat, Variables.curr_lng);
	           			
	           			
	           			Log.i("curr_addr", Variables.curr_addr);
	           			     Log.i("curr_lat", Double.toString(Variables.curr_lat));
	           			  Log.i("curr_lng", Double.toString(Variables.curr_lng));
	                  
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
		
		Log.i("minmaxlatlng", Double.toString(minLat) + " " + Double.toString(maxLat) + " " + Double.toString(minLon)  + " "  + Double.toString(maxLon));
		
		bounded_latlng.put("minLng", minLon);
		bounded_latlng.put("minLat", minLat);
		bounded_latlng.put("maxLng", maxLon);
		bounded_latlng.put("maxLat", maxLat);

	return bounded_latlng;
	}


}








