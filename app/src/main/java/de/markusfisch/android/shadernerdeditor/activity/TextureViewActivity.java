package de.markusfisch.android.shadernerdeditor.activity;

import android.os.Bundle;

import de.markusfisch.android.shadernerdeditor.R;
import de.markusfisch.android.shadernerdeditor.fragment.TextureViewFragment;
import de.markusfisch.android.shadernerdeditor.view.SystemBarMetrics;
import de.markusfisch.android.shadernerdeditor.widget.ScalingImageView;

public class TextureViewActivity
		extends AbstractSubsequentActivity
		implements TextureViewFragment.ScalingImageViewProvider {
	private ScalingImageView scalingImageView;

	@Override
	public ScalingImageView getScalingImageView() {
		return scalingImageView;
	}

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_view_texture);

		scalingImageView = (ScalingImageView) findViewById(
				R.id.scaling_image_view);

		SystemBarMetrics.initSystemBars(this);
		AbstractSubsequentActivity.initToolbar(this);

		if (state == null) {
			setFragmentForIntent(new TextureViewFragment(), getIntent());
		}
	}
}
