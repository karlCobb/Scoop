package com.example.mapmapagain;

public class Variables {

private static final String INTERNET_CONNECTION_ADDRESS = 
//"http://gotobathroom.pagekite.me";
//"http://192.168.0.15";
"http://107.22.165.50";

public static String get_internet_address(){
	return INTERNET_CONNECTION_ADDRESS;
}

public static double geocoded_lat;
public static double geocoded_lng;
public static double curr_lat;
public static double curr_lng;
public static String curr_addr = "";	
public static String geocoded_addr = "";
public static String addlocation_address;
public static String addlocation_name;
public static String addlocation_handicapped;
public static String addlocation_free;
public static boolean LOCKED = false;

public void geocodeToCurrLat(){
	curr_lat = geocoded_lat;
}

public void geocodeToCurrLng(){
	curr_lng = geocoded_lng;
}

}
