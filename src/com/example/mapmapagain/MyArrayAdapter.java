package com.example.mapmapagain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.data.Freezable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyArrayAdapter extends ArrayAdapter<Locations>{
	Context context;
	ArrayList<Locations> data;
	int resource;

	
	
	
	public MyArrayAdapter(Context context, int resource, ArrayList<Locations> data
		) {
		super(context, resource, data);
		this.context = context;
		this.data = data;
		this.resource = resource;
	}
	


	  static class ViewHolder {
	    public TextView text;
	    public RatingBar ratingbar;
	    public ImageView handicappedimage;
	    public ImageView freeImage;
	  }



  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    ViewHolder viewHolder;
    Locations l = data.get(position);
    
    
    Log.i("getView", l.getReview().toString());
    if (rowView == null)
    {
    	
      viewHolder = new ViewHolder();
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //get review_info which is the info inside list_review
      rowView = inflater.inflate(R.layout.review_info, null);
      viewHolder.text = (TextView) rowView.findViewById(R.id.displayReview);
      viewHolder.ratingbar = (RatingBar) rowView.findViewById(R.id.ratingReview);
      viewHolder.handicappedimage = (ImageView) rowView.findViewById(R.id.handicappedImage);
      viewHolder.freeImage = (ImageView) rowView.findViewById(R.id.freeImage);
      
      
      rowView.setTag(viewHolder); 
    }
    else
    {
        viewHolder = (ViewHolder) rowView.getTag();
    }
 
    if(l.getReview().isEmpty()){
    	viewHolder.text.setText("No review was left.");
  }else{
    viewHolder.text.setText(l.getReview());
  }
    viewHolder.ratingbar.setRating(l.getClean());
	Log.i("ArrayAdapter handicapped", String.valueOf(l.getHandicapped()));
    if(l.getHandicapped() == true){
    	Log.i("True handicapped", String.valueOf(l.getHandicapped()));
    	viewHolder.handicappedimage.setImageResource(R.drawable.handicapped_small);
    }else{
    	Log.i("False handicapped", String.valueOf(l.getHandicapped()));
    	viewHolder.handicappedimage.setImageResource(R.drawable.not_handicapped);
    }
    if(l.getPub() == true){
    	viewHolder.freeImage.setImageResource(R.drawable.money_small);
    }else{
    	viewHolder.freeImage.setImageResource(R.drawable.nomoney);
    }
    
    Log.i("review adapter", l.getReview());
    
    return rowView;
  }
  


} 





















