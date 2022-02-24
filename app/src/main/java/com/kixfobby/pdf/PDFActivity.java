package com.kixfobby.pdf;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.util.Objects;

public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private int page;
    private int i = 0;
    private boolean bar, toast;
    private static String PDF_PASSWORD = "";
    private Uri uri;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_pdf);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.enableAnnotationRendering(true);
        pdfView.computeScroll();
        pdfView.useBestQuality(true);
        pdfView.performPageSnap();
        pdfView.setPageFling(true);
        pdfView.enableAntialiasing(true);

        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                if (i == 0) {
                    getSupportActionBar().hide();
                    bar = sharedPref.getBoolean("toolbar", false);
                    if (bar == true) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        getSupportActionBar().hide();
                    } else {
                    }
                    i++;
                } else {
                    getSupportActionBar().show();
                    bar = sharedPref.getBoolean("toolbar", false);
                    if (bar == true) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        getSupportActionBar().show();
                    } else {
                    }
                    i = 0;
                }
            }
        });

        loadPdf();

    }

    void loadPdf() {
        Intent intent = this.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        try {
            if (Intent.ACTION_VIEW.equals(action)) {
                uri = intent.getData();
                String fPath = uri.getPath();
                String name = fPath.substring(fPath.lastIndexOf("/") + 1);

                if (uri != null) {
                    pdfView.fromUri(uri).defaultPage(0).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            toast = sharedPref.getBoolean("toast", false);
                            if (toast == true) {
                                String page;
                                if (String.valueOf(nbPages).equals("1")) {
                                    page = "Contains A Single Page";
                                } else {
                                    page = "Contains " + String.valueOf(nbPages) + " Pages";
                                }
                                Toasty.info(PDFActivity.this, page, Toast.LENGTH_LONG).show();
                            } else {
                            }
                        }
                    })
                            .scrollHandle(new DefaultScrollHandle(this))
                            .pageFitPolicy(FitPolicy.BOTH)
                            .password(PDF_PASSWORD)
                            .swipeHorizontal(false)
                            .autoSpacing(true)
                            .pageSnap(true)
                            .pageFling(true)
                            .load();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    Objects.requireNonNull(getSupportActionBar()).setSubtitle(name);
                } else {
                    Toasty.error(getApplicationContext(), "Can't Read File", Toast.LENGTH_SHORT).show();
                }
            } else {
                String path = Objects.requireNonNull(intent.getExtras()).getString("PATH");
                String name = path.substring(path.lastIndexOf("/") + 1);

                File file = new File(path);
                if (file.canRead()) {
                    pdfView.fromFile(file).defaultPage(0).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            toast = sharedPref.getBoolean("toast", false);
                            if (toast == true) {
                                String page;
                                if (String.valueOf(nbPages).equals("1")) {
                                    page = "Contains A Single Page";
                                } else {
                                    page = "Contains " + nbPages + " Pages";
                                }
                                Toasty.info(PDFActivity.this, page, Toast.LENGTH_LONG).show();
                            } else {
                            }
                        }
                    })
                            .scrollHandle(new DefaultScrollHandle(this))
                            .pageFitPolicy(FitPolicy.BOTH)
                            .password(PDF_PASSWORD)
                            .swipeHorizontal(false)
                            .autoSpacing(true)
                            .pageSnap(true)
                            .pageFling(true)
                            .load();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    Objects.requireNonNull(getSupportActionBar()).setSubtitle(name);
                }
            }

        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "Can't Open File (It's of invalid format)", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    void unlockPdf() {

        final EditText input = new EditText(this);
        input.setPadding(19, 19, 19, 19);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle(R.string.password)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PDF_PASSWORD = input.getText().toString();
                        if (uri != null)
                            loadPdf();
                    }
                })
                //.setIcon(R.drawable.lock_icon)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("page number");
        mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = Integer.parseInt(query);
                if (page > pdfView.getPageCount()) {
                    Toasty.error(getApplicationContext(), "Sorry, not up to " + query + " pages", Toast.LENGTH_SHORT).show();
                } else
                    pdfView.jumpTo(page - 1, true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_unlock:
                unlockPdf();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
