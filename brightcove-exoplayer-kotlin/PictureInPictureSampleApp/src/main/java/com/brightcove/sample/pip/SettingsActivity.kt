package com.brightcove.sample.pip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, PictureInPicturePreferenceFragment())
            .commit()
    }

    class PictureInPicturePreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_picture_in_picture_settings, rootKey)
        }

        /*override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            val key = preference?.key
            if (key == "key_for_your_preference") {
                // Handle the click event for your preference
                return true
            }
            return super.onPreferenceTreeClick(preference)
        }*/
    }
}
