package com.kixfobby.pdf.Preference;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.kixfobby.pdf.PDFListActivity;

public class PdfSettings extends AppCompatActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PdfFrag fragment = new PdfFrag();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent r = new Intent(getApplicationContext(), PDFListActivity.class);
        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(r);
        finish();

        super.onBackPressed();
    }
}
