package com.example.mapmapagain;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.HttpResponse;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class AddLocation extends Activity{
	String address_intent = null;
	String name_intent = null;
	String handicapped_intent = null;
	String free_intent = null;
	
    private boolean finished = false;
    String INTERNET = Variables.get_internet_address();

    double latitude = 0.0;
    double longitude = 0.0;  
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_addr);
        Log.i("activity 2", "Start");
        Intent i = getIntent();
        address_intent = i.getStringExtra("address");
        name_intent = i.getStringExtra("name");
        handicapped_intent = i.getStringExtra("handicapped");
        free_intent = i.getStringExtra("free");
        EditText address = (EditText) findViewById(R.id.PutAddress);
        address.setText(address_intent);
        Log.i("nameintent", name_intent);
      if(name_intent.trim().compareTo("name") != 0)
      {
          EditText name = (EditText) findViewById(R.id.PutName);
          name.setText(name_intent);  
      } 
      
      EditText review = (EditText) findViewById(R.id.review);
   
      
      review.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			TextView counter = (TextView) findViewById(R.id.counter);
			counter.setText(Integer.toString(300-s.length()) + " characters free");
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	});
     
      
      
	}        
   
	
public void Cancel(View v){
	finish();
}
	
	
public void save_location(View v) throws IOException{
   EditText name = (EditText) findViewById(R.id.PutName);
String name_db = name.getText().toString();
if(name.getText().toString().isEmpty() == false)
{
	Log.i("save location", "clicked");
    EditText address = (EditText) findViewById(R.id.PutAddress);
	String address_db = address.getText().toString();
	LatlngDbConversion latlngdb = new LatlngDbConversion();
   	if(address_intent.compareTo(Variables.curr_addr) == 0)
{  			latitude = Variables.curr_lat;
   			longitude = Variables.curr_lng;
   			Log.i("currlatlng", Double.toString(longitude) + " " + Double.toString(latitude));
}
   	else{
    latitude = Variables.geocoded_lat;
    longitude = Variables.geocoded_lng;
    Log.i("geocodeedlatlng", Double.toString(longitude) + " " + Double.toString(latitude));
   	}

	String lat = latlngdb.latDb(latitude);
	String lng = latlngdb.lngDb(longitude);


	CheckBox free = (CheckBox) findViewById(R.id.free);
	CheckBox handicapped = (CheckBox) findViewById(R.id.handicapped);
	
	String handicapped_val = Boolean.toString(handicapped.isChecked());
	String free_val = Boolean.toString(free.isChecked());
	
	RatingBar clean = (RatingBar) findViewById(R.id.cleanrating);
	String clean_val = Float.toString(clean.getRating());
	EditText review = (EditText) findViewById(R.id.review);
	String review_val = review.getText().toString().trim();
	
	
	ContactServer contact = new ContactServer();
	contact.execute(INTERNET + "/create_location.php?", name_db, free_val, handicapped_val, review_val, clean_val,
			lat, lng);

}
	
	else
	{
alertMissingText();
}


}




private void alertMissingText(){
AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

	// set title
	alertDialogBuilder.setTitle("Confirm");

	// set dialog message
	alertDialogBuilder
		.setMessage("Name field must not be blank")
		.setCancelable(false)
		.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});
	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();

	// show it
	alertDialog.show();

}

public int boolToInt(boolean b) {
    return b ? 1 : 0;
}







class ContactServer extends AsyncTask<String, Void, String>
{
		

	@Override
	protected String doInBackground(String...params) {

		Log.i("lat long",  params[0] + " " + address_intent + " " + params[1] + " " + params[2] + " " +  params[3] + " " + 
				 params[4] + " " +  params[5] + " " + params[6] + " " + params[7]);
		Log.e("httppost 1", "start1");
		
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		/*
		 * If name_intent does not equal name then
		 * location is new and must be added to the database.
		 */
	    HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		Log.i("name compare", Integer.toString(name_intent.trim().compareTo("name")));
		if(name_intent.trim().compareTo("name") == 0)
		{
		nameValuePairs.add(new BasicNameValuePair("address", address_intent));
		nameValuePairs.add(new BasicNameValuePair("name", params[1]));
		nameValuePairs.add(new BasicNameValuePair("free", params[2]));
		nameValuePairs.add(new BasicNameValuePair("handicapped", params[3]));
		nameValuePairs.add(new BasicNameValuePair("reviews", params[4]));
		nameValuePairs.add(new BasicNameValuePair("ratings", params[5]));
		nameValuePairs.add(new BasicNameValuePair("latitude", params[6]));
		nameValuePairs.add(new BasicNameValuePair("longitude", params[7]));
		  httppost = new HttpPost(params[0]);
		  HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      httppost.addHeader(entity.getContentType());
	      httppost.setEntity(entity);
	      Log.i("httppost 1", httppost.getURI().toString());
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("address", address_intent));
			nameValuePairs.add(new BasicNameValuePair("free", params[2]));
			nameValuePairs.add(new BasicNameValuePair("handicapped", params[3]));
			nameValuePairs.add(new BasicNameValuePair("reviews", params[4]));
			nameValuePairs.add(new BasicNameValuePair("ratings", params[5]));
			  httppost = new HttpPost(INTERNET + "/create_update_location.php?");
			  HttpEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(nameValuePairs);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      httppost.addHeader(entity.getContentType());
		      httppost.setEntity(entity);
		      Log.i("httppost 2", httppost.getURI().toString());
		}
		String response = "";
		try{
	            org.apache.http.HttpResponse httpresponse = httpclient.execute(httppost);
	            Log.i("response", httpresponse.toString());
	            HttpEntity response_entity = httpresponse.getEntity();
	            InputStream is = response_entity.getContent();
	            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
	              String s = "";
	              while ((s = buffer.readLine()) != null) {
	                response += s;
	                Log.i("message", s);
	              }
	             return response;
	        }catch(Exception e){
	            Log.e("log_tag", "Error in http connection"+e.toString());
	        }
		return null;
	}
	



@Override
protected void onPostExecute(String result) {
	super.onPostExecute(result);

	if(result.isEmpty() == false){
		Toast.makeText(getApplicationContext(), "Your location has been added", 5000).show();
		finish();
	}else{
		Toast.makeText(getApplicationContext(), "Your location was not added", 5000).show();
		finish();
	}
}	

}






}
