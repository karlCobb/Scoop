package com.example.mapmapagain;

import com.example.mapmapagain.MainActivity.FindNearby;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class SplashScreen extends Activity {
	boolean isGPSEnabled = false,
			 isNetworkEnabled = false;
	MainActivity sendtomain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		
		if(!isOnline()){
			connectToNetwork();
		}else{
		
		new Handler().postDelayed(new Runnable() {
        	 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);	
                if(isNetworkEnabled){
                Variables.curr_lat = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                Variables.curr_lng = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude(); 
                }
                else if(isGPSEnabled){
              	Variables.curr_lat = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
              	Variables.curr_lng = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(); 
                }else{
                Variables.curr_lat = 0.0;
                Variables.curr_lng = 0.0;
                }
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, 500);
	}
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
	
	public void connectToNetwork(){
			AlertDialog.Builder connectToNetwork = new AlertDialog.Builder(this);
		    connectToNetwork.setTitle("No Network Connectivity");
			connectToNetwork.setMessage("This application does not work without internet.  Please connect to the internet or quit.");
			connectToNetwork.setPositiveButton("go to network settings", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(intent);
				}
			}).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			connectToNetwork.create();
			connectToNetwork.show();
			
			}
}