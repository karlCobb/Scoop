package com.example.mapmapagain;

import android.util.Log;

public class LatlngDbConversion {

public String latDb(double lat){

	double lat_add = 90.0 + lat;
	int int_lat = (int) (lat_add*1E7);
	StringBuilder latBuilder = new StringBuilder(Integer.toString(int_lat));
	int lat_length = latBuilder.length();
	int lat_diff = 10 - lat_length;
	
	if(lat_diff > 0)
	{latBuilder.reverse();
	for(int i = 0; i < lat_diff; i+=1)
	{
	latBuilder.append("0");	
	}
	latBuilder.reverse();
	}

	Log.i("latconverted fro db", latBuilder.toString());
	
	return latBuilder.toString();
}


public String lngDb(double lng){
	double lng_add = 180.0 + lng;
	int int_lng = (int) (lng_add*1E7);
	StringBuilder lngBuilder = new StringBuilder(Integer.toString(int_lng));
	int lng_length = lngBuilder.length();
	int lng_diff = 10 - lng_length;
	if(lng_diff > 0)
	{lngBuilder.reverse();
	for(int i = 0; i < lng_diff; i+=1)
	{
	lngBuilder.append("0");	
	}
	lngBuilder.reverse();
	}
	Log.i("lng converted for db", lngBuilder.toString());
	return lngBuilder.toString();
}

public double latFromDb(String lat){
	if(lat.isEmpty() == false){
	int int_latitude = Integer.parseInt(lat);
	Log.i("lat from db", Integer.toString(int_latitude));
	double latitude = (int_latitude/1E7);
	Log.i("lat from db", Double.toString(latitude));
	latitude = latitude - 90.0;
	Log.i("lat from db", Double.toString(latitude));
	latitude = Math.round(latitude*10000000.0)/10000000.0;
	Log.i("lat from db", Double.toString(latitude));
	return latitude;
	}
	return 0.0;
}
public double lngFromDb(String lng){
	if(lng.isEmpty() == false){
	int int_longitude = Integer.parseInt(lng);
	Log.i("lng from db", Integer.toString(int_longitude));
	double longitude = (int_longitude/1E7);
	longitude = longitude - 180.0;
	longitude = Math.round(longitude*10000000.0)/10000000.0;
	Log.i("lng from db", Double.toString(longitude));
return longitude;
}
	return 0.0;
}

	
}


