package com.example.mapmapagain;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.widget.EditText;

public class Preferences extends PreferenceActivity{
		  @Override
		  public void onCreate(Bundle savedInstanceState) {
		      super.onCreate(savedInstanceState);
		      addPreferencesFromResource(R.xml.preference);
		      
		  }
		} 







