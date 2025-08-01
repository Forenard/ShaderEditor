package de.markusfisch.android.shadereditor.opengl;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import androidx.annotation.NonNull;

public class TextureParameters {
	protected static final String HEADER = "///";
	protected static final String SEPARATOR = ";";
	protected static final String ASSIGN = ":";

	private static final String MIN = "min";
	private static final String MAG = "mag";
	private static final String WRAP_S = "s";
	private static final String WRAP_T = "t";
	private static final Matrix flipMatrix = new Matrix();

	static {
		flipMatrix.postScale(1f, -1f);
	}

	private final int defaultMin;
	private final int defaultMag;
	private final int defaultWrapS;
	private final int defaultWrapT;

	private int min;
	private int mag;
	private int wrapS;
	private int wrapT;

	public TextureParameters() {
		defaultMin = GLES30.GL_NEAREST;
		defaultMag = GLES30.GL_LINEAR;
		defaultWrapS = GLES30.GL_REPEAT;
		defaultWrapT = GLES30.GL_REPEAT;
		set(defaultMin, defaultMag, defaultWrapS, defaultWrapT);
	}

	TextureParameters(int min, int mag, int wrapS, int wrapT) {
		defaultMin = min;
		defaultMag = mag;
		defaultWrapS = wrapS;
		defaultWrapT = wrapT;
		set(min, mag, wrapS, wrapT);
	}

	TextureParameters(String params) {
		this();
		parse(params);
	}

	public void set(
			String minShortcut,
			String magShortcut,
			String wrapSShortcut,
			String wrapTShortcut) {
		min = shortcutToMin(minShortcut);
		mag = shortcutToMag(magShortcut);
		wrapS = shortcutToWrap(wrapSShortcut);
		wrapT = shortcutToWrap(wrapTShortcut);
	}

	@NonNull
	@Override
	public String toString() {
		if (min == defaultMin &&
				mag == defaultMag &&
				wrapS == defaultWrapS &&
				wrapT == defaultWrapT) {
			// Use empty string for default values.
			return "";
		}
		return HEADER +
				MIN + ASSIGN + getMinShortcut() + SEPARATOR +
				MAG + ASSIGN + getMagShortcut() + SEPARATOR +
				WRAP_S + ASSIGN + getWrapSShortcut() + SEPARATOR +
				WRAP_T + ASSIGN + getWrapTShortcut() + SEPARATOR;
	}

	public String getMinShortcut() {
		return minToShortcut(min);
	}

	public String getMagShortcut() {
		return magToShortcut(mag);
	}

	public String getWrapSShortcut() {
		return wrapToShortcut(wrapS);
	}

	public String getWrapTShortcut() {
		return wrapToShortcut(wrapT);
	}

	void set(int min, int mag, int wrapS, int wrapT) {
		this.min = min;
		this.mag = mag;
		this.wrapS = wrapS;
		this.wrapT = wrapT;
	}

	void setParameters(int target) {
		GLES30.glTexParameteri(
				target,
				GLES30.GL_TEXTURE_MIN_FILTER,
				min);
		GLES30.glTexParameteri(
				target,
				GLES30.GL_TEXTURE_MAG_FILTER,
				mag);
		GLES30.glTexParameteri(
				target,
				GLES30.GL_TEXTURE_WRAP_S,
				wrapS);
		GLES30.glTexParameteri(
				target,
				GLES30.GL_TEXTURE_WRAP_T,
				wrapT);
	}

	static String setBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		// Flip bitmap because 0/0 is bottom left in OpenGL.
		Bitmap flippedBitmap = Bitmap.createBitmap(
				bitmap,
				0,
				0,
				bitmap.getWidth(),
				bitmap.getHeight(),
				flipMatrix,
				true);
		String message = null;
		try {
			GLUtils.texImage2D(
					GLES30.GL_TEXTURE_2D,
					0,
					GLES30.GL_RGBA,
					flippedBitmap,
					GLES30.GL_UNSIGNED_BYTE,
					0);
		} catch (IllegalArgumentException e) {
			// Format/color space is invalid.
			message = e.getMessage();
		}
		flippedBitmap.recycle();
		return message;
	}

	void parse(String params) {
		if (params == null) {
			return;
		}
		params = params.trim();
		int p = params.indexOf(HEADER);
		if (p != 0) {
			return;
		}
		params = params.substring(p + 3);
		for (String param : params.split(SEPARATOR)) {
			String[] exp = param.split(ASSIGN);
			if (exp.length != 2) {
				continue;
			}
			parseParameter(exp[0], exp[1]);
		}
	}

	protected void parseParameter(String name, String value) {
		switch (name) {
			default:
			case MIN:
				min = shortcutToMin(value);
				break;
			case MAG:
				mag = shortcutToMag(value);
				break;
			case WRAP_S:
				wrapS = shortcutToWrap(value);
				break;
			case WRAP_T:
				wrapT = shortcutToWrap(value);
				break;
		}
	}

	private static int shortcutToMin(String shortcut) {
		switch (shortcut) {
			case "n":
				return GLES30.GL_NEAREST;
			case "l":
				return GLES30.GL_LINEAR;
			case "nn":
				return GLES30.GL_NEAREST_MIPMAP_NEAREST;
			case "ln":
				return GLES30.GL_LINEAR_MIPMAP_NEAREST;
			case "ll":
				return GLES30.GL_LINEAR_MIPMAP_LINEAR;
			default:
				return GLES30.GL_NEAREST_MIPMAP_LINEAR;
		}
	}

	private static String minToShortcut(int min) {
		switch (min) {
			case GLES30.GL_NEAREST:
				return "n";
			case GLES30.GL_LINEAR:
				return "l";
			case GLES30.GL_NEAREST_MIPMAP_NEAREST:
				return "nn";
			case GLES30.GL_LINEAR_MIPMAP_NEAREST:
				return "ln";
			case GLES30.GL_LINEAR_MIPMAP_LINEAR:
				return "ll";
			default:
				return "nl";
		}
	}

	private static int shortcutToMag(String shortcut) {
		if (shortcut.equals("n")) {
			return GLES30.GL_NEAREST;
		} else {
			return GLES30.GL_LINEAR;
		}
	}

	private static String magToShortcut(int mag) {
		return mag == GLES30.GL_NEAREST ? "n" : "l";
	}

	private static int shortcutToWrap(String shortcut) {
		switch (shortcut) {
			case "c":
				return GLES30.GL_CLAMP_TO_EDGE;
			case "m":
				return GLES30.GL_MIRRORED_REPEAT;
			default:
				return GLES30.GL_REPEAT;
		}
	}

	private static String wrapToShortcut(int wrap) {
		switch (wrap) {
			case GLES30.GL_CLAMP_TO_EDGE:
				return "c";
			case GLES30.GL_MIRRORED_REPEAT:
				return "m";
			default:
				return "r";
		}
	}
}
