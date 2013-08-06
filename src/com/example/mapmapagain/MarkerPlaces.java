package com.example.mapmapagain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

//hold marker data
public class MarkerPlaces {
	
	public static HashMap<String, Locations> places = new HashMap<String, Locations>();
	
	
	public void storePlaces(String id, Locations l){
		places.put(id, l);
		Log.i("places get address", places.get(id).getAddress() + " " + places.put(id, l).toString());
	}
	
	
	public Locations getPlaces(String id){
		Log.i("MarkerPlaces", places.toString());
		Log.i("MarkerPlaces", id);
		Locations local = new Locations();
		local.setAddress(places.get(id).getAddress());
		local.setClean(places.get(id).getClean());
		local.setHandicapped(places.get(id).getHandicapped());
		local.setLat(places.get(id).getLat());
		local.setLongitude(places.get(id).getLong());
		local.setName(places.get(id).getName());
		local.setPub(places.get(id).getPub());
		local.setReview(places.get(id).getReview());
		Log.i("MarkerPlaces", local.getAddress().toString());
		return local;
	}	
	
}	