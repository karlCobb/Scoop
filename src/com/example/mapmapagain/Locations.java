package com.example.mapmapagain;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import android.content.Context;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Key;

public class Locations implements Serializable{

GeoMethods gm;
int id;
double lat;
double longitude;
float clean;
boolean handicapped;
boolean pub;
String name;
String address;
String review;

public Locations(){
}

public Locations(int id,  String name, String address, double lat, double longitude, boolean handicapped, boolean pub, float clean, String review){
	this.id = id;
	this.lat = lat;
	this.longitude = longitude;
	this.name = name;
	this.address = address;
	this.clean = clean;
	this.handicapped=handicapped;
	this.pub = pub;
	this.review = review;
}

public Locations(String name, String address, double lat, double longitude, int clean, boolean handicapped, boolean pub, String review){
	this.lat = lat;
	this.longitude = longitude;
	this.name = name;
	this.address = address;
	this.clean = clean;
	this.handicapped=handicapped;
	this.pub = pub;
	this.review = review;
}

public Locations(Locations l){
	this.lat = l.getLat();
	this.longitude = l.getLong();
	this.name = l.getName();
	this.address = l.getAddress();
	this.clean = l.getClean();
	this.handicapped=l.getHandicapped();
	this.pub = l.getPub();
	this.review = l.getReview();
Log.i("location add", l.getAddress());
}

public void clear(){
	this.lat = 0.0;
	this.longitude = 0.0;
	this.name = null;
	this.address = null;
	this.clean = 0;
	this.handicapped=false;
	this.pub = false;
	this.review = null;
	
	
}




// getting ID
public int getID(){
    return this.id;
}

// setting id
public void setID(int id){
    this.id = id;
}

public String getReview(){
    return this.review;
}

// setting id
public void setReview(String review){
    this.review = review;
}

//getting name
public String getName(){
return this.name;
}

//setting name
public void setName(String name){
this.name = name;
}

//getting address
public String getAddress(){
return this.address;
}

//setting address
public void setAddress(String address){
this.address = address;
}


// getting lat
public double getLat(){
    return this.lat;
}

// setting lat
public void setLat(double lat){
    this.lat = lat;
}

//getting longitude

public double getLong(){
 return this.longitude;
}

//setting longitude
public void setLongitude(double longitude){
 this.longitude = longitude;
}

public float getClean(){
return this.clean;
}

//setting name
public void setClean(float clean){
this.clean = clean;
}

public boolean getHandicapped(){
return this.handicapped;
}

//setting name
public void setHandicapped(boolean handicapped){
this.handicapped = handicapped;
}

public boolean getPub(){
return this.pub;
}

//setting name
public void setPub(boolean pub){
this.pub = pub;
}


}



