package de.markusfisch.android.shadernerdeditor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import de.markusfisch.android.shadernerdeditor.activity.CubeMapActivity;
import de.markusfisch.android.shadernerdeditor.app.ShaderEditorApp;

public class UniformSamplerCubePageFragment
		extends UniformSampler2dPageFragment {
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setSamplerType(AbstractSamplerPropertiesFragment.SAMPLER_CUBE);
	}

	@Override
	protected void addTexture() {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}
		startActivity(new Intent(activity, CubeMapActivity.class));
	}

	@Override
	protected Cursor loadTextures() {
		return ShaderEditorApp.db.getSamplerCubeTextures(searchQuery);
	}
}
