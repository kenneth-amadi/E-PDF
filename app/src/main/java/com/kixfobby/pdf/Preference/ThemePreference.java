package com.kixfobby.pdf.Preference;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.*;


public class ThemePreference extends DialogPreference
{
	private List<String> theme = null;
	private int selected;

	private SharedPreferences sharedPref;
	private String mode;

	public ThemePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mode = sharedPref.getString("mode", "Theme Mode 2");

		if (mode.equals("Theme Mode 1"))
			selected = 1;
		else if (mode.equals("Theme Mode 2"))
			selected = 2;
		else if (mode.equals("Theme Mode 3"))
			selected = 3;
		else if (mode.equals("Theme Mode 4"))
			selected = 4;
		else if (mode.equals("Theme Mode 5"))
			selected = 5;
		else 
			selected = 0;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
	{
		builder.setTitle("Select App Theme");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

					if (selected == 0)
						editor.putString("mode", "Default Theme");
					else if (selected == 1)
						editor.putString("mode", "Theme Mode 1");
					else if (selected == 2)
						editor.putString("mode", "Theme Mode 2");
					else if (selected == 3)
						editor.putString("mode", "Theme Mode 3");
					else if (selected == 4)
						editor.putString("mode", "Theme Mode 4");
					else 
						editor.putString("mode", "Theme Mode 5");

					editor.commit();
					notifyChanged();

				}
			});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{}
			});

		String[] arrayOftheme = {"Default Theme", "Theme Mode 1", "Theme Mode 2", "Theme Mode 3", "Theme Mode 4", "Theme Mode 5"};
		theme = Arrays.asList(arrayOftheme);

		ThemeArrayAdapter adapter = new ThemeArrayAdapter(getContext(), android.R.layout.simple_list_item_single_choice, theme);
		builder.setSingleChoiceItems(adapter, selected, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					selected = which;
				}
			});		
    } 

	public class ThemeArrayAdapter extends ArrayAdapter<String>
	{
		public ThemeArrayAdapter(Context context, int resource, List<String> objects)
		{
			super(context, resource, objects);
		} 

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			// get the view that would normally be returned
			View v = super.getView(position, convertView, parent);
			final TextView tv = (TextView) v;

			final String option = tv.getText().toString();			
			if (option.equals("Default Theme"))
			{
				tv.setTextColor(Color.parseColor("#3B546E"));
			}
			else if (option.equals("Theme Mode 1"))
			{
				tv.setTextColor(Color.parseColor("#2E7D32"));
			}
			else if (option.equals("Theme Mode 2"))
			{
				tv.setTextColor(Color.parseColor("#004AF2"));
			}
			else if (option.equals("Theme Mode 3"))
			{
				tv.setTextColor(Color.parseColor("#B90808"));
			}
			else if (option.equals("Theme Mode 4"))
			{
				tv.setTextColor(Color.parseColor("#F57C00"));
			}
			else if (option.equals("Theme Mode 5"))
			{
				tv.setTextColor(Color.parseColor("#212121"));
			}


			// general options
			tv.setPadding(40, 3, 10, 3);
			tv.setAllCaps(true);
			tv.setTypeface(Typeface.DEFAULT_BOLD);
			return v;	
		} // end getView()

	} // end class ThemeArrayAdapter

} // end class modeTypePreference
