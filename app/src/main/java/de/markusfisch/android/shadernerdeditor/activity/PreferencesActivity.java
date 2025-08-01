package de.markusfisch.android.shadernerdeditor.activity;

import androidx.fragment.app.Fragment;

import de.markusfisch.android.shadernerdeditor.fragment.PreferencesFragment;

public class PreferencesActivity extends AbstractContentActivity {
	@Override
	protected Fragment defaultFragment() {
		return new PreferencesFragment();
	}
}
