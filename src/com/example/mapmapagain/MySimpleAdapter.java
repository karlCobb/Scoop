package com.example.mapmapagain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;





public class MySimpleAdapter extends SimpleAdapter implements Adapter{


	private Context context;
	private List<HashMap<String, String>> hashmap;
	private int resource;
	private String[] from;
	private int[] to;
	
	
	
	@SuppressWarnings("deprecation")
	public MySimpleAdapter(Context context, List<HashMap<String, String>> hashmap, int resource,
			String[] from, int[] to) {
		super(context, hashmap, resource, from, to);
		this.context = context;
		
		this.resource = resource;
		
		for(int i = 0; i < hashmap.size(); ++i)
		{
		this.hashmap.set(i, hashmap.get(i));
		}
		
		for(int i = 0; i < from.length; ++i)
		{
			this.from[i] = from[i];
		}
		for(int i = 0; i < to.length; ++i)
		{
			this.to[i] = to[i];
		}
		
	}
	
	
	public boolean setViewValue(View view, String data,
			String textRepresentation) {
		   if (view instanceof RatingBar) {
            	  float rating_val = (float) Float.parseFloat(data);
                 RatingBar rating = (RatingBar) view;
                  rating.setRating(rating_val);
                  return true;
              }
              return false;
	
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.getCount();
	}


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.getItem(position);
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}


	public void setViewValue(int ratingreview,
			ArrayList<HashMap<String, String>> values) {
		// TODO Auto-generated method stub
		
	}
	
	
}