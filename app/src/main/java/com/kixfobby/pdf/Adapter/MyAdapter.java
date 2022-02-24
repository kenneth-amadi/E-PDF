package com.kixfobby.pdf.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kixfobby.pdf.PDFActivity;
import com.kixfobby.pdf.PDFDoc;
import com.kixfobby.pdf.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter 
{

	Context c;
    ArrayList<PDFDoc> pdfDocs;

    public MyAdapter(Context c, ArrayList<PDFDoc> pdfDocs) {
        this.c = c;
        this.pdfDocs = pdfDocs;
    }

    @Override
    public int getCount() {
        return pdfDocs.size();
    }

    @Override
    public Object getItem(int i) {
        return pdfDocs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            //INFLATE CUSTOM LAYOUT
            view= LayoutInflater.from(c).inflate(R.layout.item_pdf,viewGroup,false);
        }

        final PDFDoc pdfDoc= (PDFDoc) this.getItem(i);

        TextView nameTxt= (TextView) view.findViewById(R.id.name);
        ImageView img= (ImageView) view.findViewById(R.id.image);

        //BIND DATA
        nameTxt.setText(pdfDoc.getName());
		nameTxt.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/a.ttf"));
        img.setImageResource(R.drawable.pdf_icon);

        //VIEW ITEM CLICK
        view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					openPDFView(pdfDoc.getPath());
				}
			});
        return view;
    }

    //OPEN PDF VIEW
    private void openPDFView(String path)
    {
        Intent i=new Intent(c,PDFActivity.class);
        i.putExtra("PATH",path);
        c.startActivity(i);
		
		}
		
}
