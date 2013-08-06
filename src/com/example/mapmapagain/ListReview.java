package com.example.mapmapagain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.l;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class ListReview extends Activity {
	String INTERNET = Variables.get_internet_address();
	String address = null;
	Context c = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_review);
		Bundle extras = getIntent().getExtras();
		address = extras.getString("address");
		setListView slv = new setListView();
		slv.execute(INTERNET + "/get_listview.php", address);
	}

	

	
public void back(View v){
	finish();
	
}
	

 
	class setListView extends AsyncTask<String, Void, JSONArray>{

		@SuppressWarnings("deprecation")
		@Override
		protected JSONArray doInBackground(String... params){
		

	    HttpClient client = new DefaultHttpClient();
	    String url = null;
			url = params[0] + "?address=" + URLEncoder.encode(params[1]);
		Log.i("encoded URL", url);
	    
	    //must encode url!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
	    		
	    HttpGet httpGet = new HttpGet(url);

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
	    Log.i("response", response.toString());
	    
	  
	    try {
	           
	            StatusLine statusLine = response.getStatusLine();
	            int statusCode = statusLine.getStatusCode();
	            Log.i("status code", Integer.toString(statusCode));
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
	                   Log.e("Getter", "Failed to download	 file");
	                   
	           }}catch (Exception e){
	               Log.e("log_tag", "Error in http connection" +e.getMessage());
	           }//end try catch
	    return null;
	}

		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		//		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
			
			ArrayList<Locations> location_array = new ArrayList<Locations>();
				for(int i = 0; i < result.length(); ++i)
				{
				//HashMap map = new HashMap<String, String>();
				Locations l = new Locations();
				JSONObject json = null;
					try {
					json = result.getJSONObject(i);
					l.setName(json.getString("name"));
					//l.setAddress(json.getString("address"));
					l.setReview(json.getString("reviews"));
					l.setClean(Float.parseFloat(json.getString("ratings")));
					location_array.add(l);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				String [] columns = {"reviews", "ratings"};
				int [] toView = {R.id.displayReview, R.id.ratingReview};
				//set in list_review
				TextView name_tv = (TextView) findViewById(R.id.displayName);
				TextView address_tv = (TextView) findViewById(R.id.displayAddress);
				
				Locations l = location_array.get(0);
				name_tv.setText(l.getName());
				address_tv.setText(address);
				ListView listview = (ListView) findViewById(android.R.id.list);
				MyArrayAdapter arrayadapter = new MyArrayAdapter(getApplicationContext(), R.layout.review_info, location_array);
				listview.setAdapter(arrayadapter);
				
				
				
				listview.setOnItemClickListener(new OnItemClickListener() {
			       public void onItemClick(AdapterView<?> parent, View view,
			       int position, long id) {
			    	TextView review = (TextView) view.findViewById(R.id.displayReview);
			    	String review_show = review.getText().toString();					  
							AlertDialog.Builder full_review = new AlertDialog.Builder(c);
							full_review.setTitle("Full Review");
							full_review.setMessage(review_show)
					        .setNegativeButton("Ok", new OnClickListener() {
					    		
					    		@Override
					    		public void onClick(DialogInterface dialog, int which) {
					    			dialog.cancel();
					    			
					    		}
					    	});
					        
					    	full_review.create();
					    	full_review.show();	
			        }
			     
			    });
		
			
		}
		
		
	}


	
}











