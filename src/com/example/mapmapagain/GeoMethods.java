package com.example.mapmapagain;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


public class GeoMethods{
	
	
	

	
	
	
	/*returns an address from a latlng
	 *After google update no longer used
	 */    
	public String ReverseGeo(double lat, double longitude, Context c)
	{
	Geocoder geo = new Geocoder(c);
		String address = " ";
				try {
	       List<Address> list = geo.getFromLocation(lat, longitude, 1);
	       address = list.get(0).getAddressLine(0) + ", " 
	       + list.get(0).getAddressLine(1) + ", " + list.get(0).getAddressLine(2);
	 } catch (IOException e) {
	    e.printStackTrace();
	}
		  return address;
	}
		
		
		/*returns a latlng from an address
		*/
		public LatLng GeoCode(String address, Context c) throws IOException{
		double lat;
		double longitude;
		Log.i("geocode", "1");
		LatLng latlng = new LatLng(0.0, 0.0);
		Log.i("geocode", "2");
		Geocoder geo = new Geocoder(c);
		Log.i("geocode", "3");
		List<Address> list = geo.getFromLocationName(address, 1);
		Log.i("geocode", "4 " + list.toString());
		if(list.get(0).hasLatitude() && list.get(0).hasLongitude())
		{
		lat = list.get(0).getLatitude();
		longitude = list.get(0).getLongitude();
		LatLng templatlng = new LatLng(lat, longitude);
		latlng = templatlng;
		}
		Log.i("geocode", latlng.toString());
		return latlng;
		}
		
	
}
