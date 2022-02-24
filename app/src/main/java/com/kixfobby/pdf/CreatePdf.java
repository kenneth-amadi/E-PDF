package com.kixfobby.pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.kixfobby.pdf.Adapter.GridAdapter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CreatePdf extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static int mImageCounter = 0;
    private DrawerLayout drawer;
    private ArrayList<String> imagesUri;
    private ArrayList<String> tempUris;
    private ArrayList<String> nameUris;
    private Uri imageInUri;
    private Bundle bundle;
    private String path, filename;
    private Image image;
    private GridAdapter grid;
    private GridView gview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagesUri = new ArrayList<>();
        tempUris = new ArrayList<>();
        nameUris = new ArrayList<>();

        gview = findViewById(R.id.grid);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout nav_header = headerView.findViewById(R.id.nav_header);
        nav_header.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_image:
                selectImages();
                break;

            case R.id.add_text_image:
                addText();
                break;

            case R.id.crop_image:
                cropImages();
                break;

            case R.id.create_pdf:
                createPdf();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void addText() {
        /*Intent intent = new Intent(this, AddText.class);
        startActivityForResult(intent, 123);*/
    }

    public void cropImages() {
        if (tempUris.size() == 0) {
            Toasty.warning(getBaseContext(), R.string.toast_no_images, Toast.LENGTH_SHORT).show();
            return;
        }
        next();
    }

    void next() {
        if (mImageCounter != tempUris.size()) {
            CropImage.activity(Uri.fromFile(new File(tempUris.get(mImageCounter))))
                    .setActivityMenuIconColor(color(R.color.colorAccent))
                    .setInitialCropWindowPaddingRatio(0)
                    .setAllowRotation(true)
                    .setAllowCounterRotation(true)
                    .setAllowFlipping(true)
                    .setAutoZoomEnabled(true)
                    .setShowCropOverlay(true)
                    .setBackgroundColor(Color.DKGRAY)
                    .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                    .start(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createPdf() {
        if (imagesUri.size() == 0) {
            if (tempUris.size() == 0) {
                Toasty.warning(getBaseContext(), R.string.toast_no_images, Toast.LENGTH_LONG).show();
                return;
            } else {
                imagesUri = (ArrayList<String>) tempUris.clone();
            }
        }
        new MaterialDialog.Builder(this)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .backgroundColor(color(R.color.colorPrimaryLight))
                .titleColor(color(R.color.colorAccent))
                .contentColor(Color.BLUE)
                .buttonRippleColor(getColor(R.color.colorAccent))
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        path = path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                getBaseContext().getString(R.string.pdf_dir);

                        File pth = new File(path + input.toString() + ".pdf");

                        if (input == null || input.toString().trim().equals("")) {
                            Toasty.warning(getBaseContext(), R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();
                            dialog.show();
                        } else if (pth.exists()) {
                            Toasty.error(getBaseContext(), "File name already exists", Toast.LENGTH_SHORT).show();
                            dialog.show();
                        } else {
                            filename = input.toString();

                            new CreatingPdf().execute();
                        }
                    }
                })
                .show();
    }

    void openPdf() {
        path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                getBaseContext().getString(R.string.pdf_dir);
        path = path + filename + getBaseContext().getString(R.string.pdf_ext);

        Intent intent = new Intent(this, PDFActivity.class);
        intent.putExtra("PATH", path);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toasty.info(getBaseContext(), "Error loading Pdf, Open from E-reader", Toast.LENGTH_LONG).show();
        }
    }

    public void selectImages() {
        Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);
        //add to intent the URIs of the already selected images
        //first they are converted to Uri objects
        ArrayList<Uri> uris = new ArrayList<>(tempUris.size());
        for (String stringUri : tempUris) {
            uris.add(Uri.fromFile(new File(stringUri)));
        }

        // add them to the intent
        intent.putExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, uris);
        ImagePickerActivity.getConfig().setFlashOn(true);
        ImagePickerActivity.getConfig().setToolbarTitleRes(R.string.select_images_text);
        ImagePickerActivity.getConfig().setSelectionMin(1);
        //ImagePickerActivity.getConfig().setCameraHeight(dimen(R.dimen.camera_height));
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            tempUris.clear();
            nameUris.clear();

            ArrayList<Uri> imageUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            assert imageUris != null;
            for (int i = 0; i < imageUris.size(); i++) {
                tempUris.add(imageUris.get(i).getPath());
                nameUris.add(Objects.requireNonNull(imageUris.get(i).getPath()).substring(Objects.requireNonNull(imageUris.get(i).getPath()).lastIndexOf("/") + 1));

            }

            grid = new GridAdapter(getBaseContext(), nameUris, imageUris);
            gview.setAdapter(grid);


            Toasty.success(getBaseContext(), R.string.toast_images_added, Toast.LENGTH_LONG).show();
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                imagesUri.add(resultUri.getPath());
                Toasty.success(getBaseContext(), R.string.toast_imagecropped, Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toasty.error(getBaseContext(), R.string.toast_error_getCropped, Toast.LENGTH_LONG).show();
                imagesUri.add(tempUris.get(mImageCounter));
                error.printStackTrace();
            } else {
                imagesUri.add(tempUris.get(mImageCounter));
            }
            mImageCounter++;
            next();
        }
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    @Override
    public void onClick(View v) {

    }

    private void refresh() {
        Intent r = new Intent(getApplicationContext(), CreatePdf.class);
        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(r);
        finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        //refresh();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * An async task that converts selected images to Pdf
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public class CreatingPdf extends AsyncTask<String, String, String> {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(CreatePdf.this)
                .title(R.string.please_wait)
                .content(R.string.populating_list)
                .backgroundColor(getColor(R.color.colorPrimaryLight))
                .titleColor(getColor(R.color.colorAccent))
                .contentColor(Color.BLUE)
                .cancelable(false)
                .progress(true, 0);
        MaterialDialog dialog = builder.build();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    getBaseContext().getString(R.string.pdf_dir);

            File folder = new File(path);
            if (!folder.exists()) {
                boolean success = folder.mkdir();
                if (!success) {
                    //Toasty.error(getBaseContext(), "Error on creating application folder", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            path = path + filename + getBaseContext().getString(R.string.pdf_ext);
            Log.v("stage 1", "store the pdf in sd card");

            Document document = new Document(PageSize.A4, 18, 18, 28, 18);
            Log.v("stage 2", "Document Created");

            Rectangle documentRect = document.getPageSize();

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                Log.v("Stage 3", "Pdf writer");

                document.open();
                Log.v("Stage 4", "Document opened");

                for (int i = 0; i < imagesUri.size(); i++) {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(
                            getBaseContext().getContentResolver(), Uri.fromFile(new File(imagesUri.get(i))));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
                    image = Image.getInstance(imagesUri.get(i));

                    if (bmp.getWidth() > documentRect.getWidth()
                            || bmp.getHeight() > documentRect.getHeight()) {
                        //bitmap is larger than page,so set bitmap's size similar to the whole page
                        image.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
                    } else {
                        //bitmap is smaller than page, so add bitmap simply.
                        //[note: if you want to fill page by stretching image,
                        // you may set size similar to page as above]
                        image.scaleAbsolute(bmp.getWidth(), bmp.getHeight());
                    }
                    Log.v("Stage 6", "Image path adding");

                    image.setAbsolutePosition(
                            (documentRect.getWidth() - image.getScaledWidth()) / 2,
                            (documentRect.getHeight() - image.getScaledHeight()) / 2);
                    Log.v("Stage 7", "Image Alignments");

                    //image.setBorder(Image.NO_BORDER);
                    image.setBorder(Image.BOX);
                    image.setBorderWidth(1);
                    document.add(image);
                    document.newPage();
                }
                Log.v("Stage 8", "Image adding");

                document.close();
                Log.v("Stage 7", "Document Closed" + path);

            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            imagesUri.clear();
            tempUris.clear();
            nameUris.clear();
            mImageCounter = 0;
            refresh();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //textView.setVisibility(View.VISIBLE);
            dialog.dismiss();
            openPdf();
        }
    }
}
