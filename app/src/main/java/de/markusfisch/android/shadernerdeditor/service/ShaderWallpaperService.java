package de.markusfisch.android.shadernerdeditor.service;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import de.markusfisch.android.shadernerdeditor.app.ShaderNerdEditorApp;
import de.markusfisch.android.shadernerdeditor.database.Database;
import de.markusfisch.android.shadernerdeditor.preference.Preferences;
import de.markusfisch.android.shadernerdeditor.receiver.BatteryLevelReceiver;
import de.markusfisch.android.shadernerdeditor.widget.ShaderView;

public class ShaderWallpaperService extends WallpaperService {
	private static ShaderWallpaperEngine engine;

	private ComponentName batteryLevelComponent;

	public static boolean isRunning() {
		return engine != null;
	}

	public static void setRenderMode(int renderMode) {
		if (engine != null) {
			engine.setRenderMode(renderMode);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		batteryLevelComponent = new ComponentName(this,
				BatteryLevelReceiver.class);
		enableComponent(batteryLevelComponent, true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		enableComponent(batteryLevelComponent, false);
		engine = null;
	}

	@Override
	public Engine onCreateEngine() {
		engine = new ShaderWallpaperEngine();
		return engine;
	}

	private class ShaderWallpaperEngine
			extends Engine
			implements SharedPreferences.OnSharedPreferenceChangeListener {
		private final Handler handler = new Handler();

		private ShaderWallpaperView view;

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences preferences,
				String key) {
			if (Preferences.WALLPAPER_SHADER.equals(key)) {
				setShader();
			}
		}

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
			view = new ShaderWallpaperView();
			setShader();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			view.destroy();
			view = null;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);

			if (visible) {
				view.onResume();
			} else {
				view.onPause();
			}
		}

		@Override
		public void onTouchEvent(MotionEvent e) {
			super.onTouchEvent(e);
			view.getRenderer().touchAt(e);
		}

		@Override
		public void onOffsetsChanged(
				float xOffset,
				float yOffset,
				float xStep,
				float yStep,
				int xPixels,
				int yPixels) {
			view.getRenderer().setOffset(xOffset, yOffset);
		}

		private ShaderWallpaperEngine() {
			super();

			ShaderNerdEditorApp.preferences.getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);

			setTouchEventsEnabled(true);
		}

		private void setRenderMode(int renderMode) {
			if (view == null) {
				return;
			}

			view.setRenderMode(renderMode);
		}

		private void setShader() {
			if (!ShaderNerdEditorApp.db.isOpen()) {
				handler.postDelayed(this::setShader, 100);

				return;
			}

			Cursor cursor = ShaderNerdEditorApp.db.getShader(
					ShaderNerdEditorApp.preferences.getWallpaperShader());

			boolean randomShader = false;

			while (cursor == null || !cursor.moveToFirst()) {
				if (cursor != null) {
					cursor.close();
				}

				if (randomShader) {
					return;
				}

				randomShader = true;
				cursor = ShaderNerdEditorApp.db.getRandomShader();
			}

			if (randomShader) {
				ShaderNerdEditorApp.preferences.setWallpaperShader(
						Database.getLong(cursor, Database.SHADERS_ID));
			}

			if (view != null) {
				view.getRenderer().setFragmentShader(
						Database.getString(cursor, Database.SHADERS_FRAGMENT_SHADER),
						Database.getFloat(cursor, Database.SHADERS_QUALITY));
			}

			cursor.close();
		}

		private class ShaderWallpaperView extends ShaderView {
			public ShaderWallpaperView() {
				super(ShaderWallpaperService.this,
						ShaderNerdEditorApp.preferences.isBatteryLow()
								? GLSurfaceView.RENDERMODE_WHEN_DIRTY
								: GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			}

			@Override
			public final SurfaceHolder getHolder() {
				return ShaderWallpaperEngine.this.getSurfaceHolder();
			}

			public void destroy() {
				super.onDetachedFromWindow();
			}
		}
	}

	private void enableComponent(ComponentName name, boolean enable) {
		getPackageManager().setComponentEnabledSetting(name,
				enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}
}
