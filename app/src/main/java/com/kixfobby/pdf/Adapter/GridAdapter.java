package com.kixfobby.pdf.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kixfobby.pdf.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<String> str;
	private ArrayList<Uri> uri;
	private ImageView image;
	private TextView name;

	public GridAdapter(Context context, ArrayList<String> str, ArrayList<Uri> uri)
	{
		this.context = context;
		this.str = str;
		this.uri = uri;
	}

	@Override
	public int getCount()
	{
		return str.size();
	}

	@Override
	public Object getItem(int i)
	{
		return null;
	}

	@Override
	public long getItemId(int i)
	{
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup)
	{
		view = LayoutInflater.from(context).inflate(R.layout.custom_grid, null);

		image = view.findViewById(R.id.image);
		name = view.findViewById(R.id.name);

		name.setText(str.get(i));
		image.setImageURI(uri.get(i));

		return view;
	}
}
