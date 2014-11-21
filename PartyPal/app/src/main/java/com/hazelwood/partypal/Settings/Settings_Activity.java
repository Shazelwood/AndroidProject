package com.hazelwood.partypal.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hazelwood.partypal.R;

/**
 * Created by Hazelwood on 11/18/14.
 */
public class Settings_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();

    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
        }
    }
}
