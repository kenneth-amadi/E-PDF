package com.kixfobby.pdf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kixfobby.pdf.Adapter.PDFAdapter;
import com.kixfobby.pdf.Preference.PdfSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PDFListActivity extends AppCompatActivity {
    private Context context;
    private File filesFolder;
    private RecyclerView rv;
    private PDFAdapter mAdapter;
    private int animation_type = ItemAnimation.FADE_IN;
    private String location;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.pdf_main);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        mAdapter = new PDFAdapter(this, getPdfItems(), animation_type);
        rv.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener((view, pdf, position) -> openPDFView(pdf.getPath()));
				
		/*if (isConnectingToInternet())
		{
			new DownloadTask(getBaseContext(), Utils.downloadPdfUrl);

			new DownloadTask(getBaseContext(), Utils.downloadDocUrl);

			new DownloadTask(getBaseContext(), Utils.downloadZipUrl);

			new DownloadTask(getBaseContext(), Utils.downloadVideoUrl);

			new DownloadTask(getBaseContext(), Utils.downloadMp3Url);
		}
		else
			Toasty.error(getApplicationContext(), "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
	*/
    }

    //Check if internet is present or not
	/*private boolean isConnectingToInternet()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
			.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}*/


    //OPEN PDF VIEW
    private void openPDFView(String path) {
        Intent i = new Intent(this, PDFActivity.class);
        i.putExtra("PATH", path);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        startActivity(i);

    }

    private List<PDFDoc> getPdfItems() {
        List<PDFDoc> pdfDocs = new ArrayList<>();

        location = sharedPref.getString("locations", "0");
        if (location.equals("1"))
            filesFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/kixfobby/MyWorld/Document/Created-Pdf");
        else if (location.equals("2"))
            filesFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
        else if (location.equals("3"))
            filesFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Xender/Other");
        else if (location.equals("4"))
            filesFolder = new File(getExternalFilesDir(null).getAbsolutePath() + "/.che");
        else
            filesFolder = new File(getExternalFilesDir(null).getAbsolutePath() + "/.book");


        if (filesFolder.exists()) {
            File[] files = filesFolder.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];

                    if (file.getPath().endsWith("pdf")) {
                        // Handle other intents, such as being started from the home screen
                        PDFDoc pdfDoc = new PDFDoc();
                        pdfDoc.setName(file.getName());
                        pdfDoc.setPath(file.getAbsolutePath());
                        pdfDocs.add(pdfDoc);
                    }
                }
            }
        }

        return pdfDocs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                startActivity(new Intent(this, CreatePdf.class));
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, PdfSettings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
