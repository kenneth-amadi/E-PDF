package com.kixfobby.pdf.Preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.kixfobby.pdf.R;

public class PdfFrag extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_pdf);

    }
}
