package de.markusfisch.android.shadernerdeditor.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import de.markusfisch.android.shadernerdeditor.R;
import de.markusfisch.android.shadernerdeditor.adapter.ShaderSpinnerAdapter;
import de.markusfisch.android.shadernerdeditor.app.ShaderNerdEditorApp;
import de.markusfisch.android.shadernerdeditor.database.Database;
import de.markusfisch.android.shadernerdeditor.preference.Preferences;

public class ShaderListPreferenceDialogFragment
		extends MaterialPreferenceDialogFragmentCompat {

	private ShaderSpinnerAdapter adapter;

	@NonNull
	public static ShaderListPreferenceDialogFragment newInstance(
			String key) {
		Bundle bundle = new Bundle();
		bundle.putString(ARG_KEY, key);

		ShaderListPreferenceDialogFragment fragment =
				new ShaderListPreferenceDialogFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		closeCursor();
	}

	@Override
	public void onMaterialDialogClosed(boolean positiveResult) {
		closeCursor();
	}

	@Override
	protected void onPrepareDialogBuilder(@NonNull AlertDialog.Builder builder) {
		// Don't call super.onPrepareDialogBuilder() because it'll check
		// for Entries and set up a setSingleChoiceItems() for them that
		// will never be used.

		final String key = getPreference().getKey();
		Cursor cursor = ShaderNerdEditorApp.db.getShaders();
		if (Preferences.DEFAULT_NEW_SHADER.equals(key)) {
			cursor = addEmptyItem(cursor);
		}
		adapter = new ShaderSpinnerAdapter(getContext(), cursor);

		builder.setSingleChoiceItems(
				adapter,
				0,
				(dialog, which) -> {
					if (Preferences.WALLPAPER_SHADER.equals(key)) {
						ShaderNerdEditorApp.preferences.setWallpaperShader(
								adapter.getItemId(which));
					} else {
						ShaderNerdEditorApp.preferences.setDefaultNewShader(
								adapter.getItemId(which));
					}

					ShaderListPreferenceDialogFragment.this.onClick(
							dialog,
							DialogInterface.BUTTON_POSITIVE);

					dialog.dismiss();
				});

		builder.setPositiveButton(null, null);
	}

	private void closeCursor() {
		if (adapter != null) {
			adapter.changeCursor(null);
			adapter = null;
		}
	}

	private Cursor addEmptyItem(Cursor cursor) {
		try (MatrixCursor matrixCursor = new MatrixCursor(new String[]{
				Database.SHADERS_ID,
				Database.SHADERS_THUMB,
				Database.SHADERS_NAME,
				Database.SHADERS_MODIFIED
		})) {
			matrixCursor.addRow(new Object[]{
					0,
					null,
					getString(R.string.no_shader_selected),
					null
			});

			return new MergeCursor(new Cursor[]{
					matrixCursor,
					cursor
			});
		}
	}
}
