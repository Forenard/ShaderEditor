package de.markusfisch.android.shadereditor.opengl;

import android.opengl.GLES30;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

class Program {
	@NonNull
	private static List<ShaderError> infoLog = Collections.emptyList();

	@NonNull
	static List<ShaderError> getInfoLog() {
		return infoLog;
	}

	static int loadProgram(
			String vertexShader,
			String fragmentShader) {
		int vs, fs, p = 0;

		if ((vs = compileShader(
				GLES30.GL_VERTEX_SHADER,
				vertexShader)) != 0) {
			if ((fs = compileShader(
					GLES30.GL_FRAGMENT_SHADER,
					fragmentShader)) != 0) {
				p = linkProgram(vs, fs);

				// Mark shader objects as deleted so they get
				// deleted as soon as glDeleteProgram() does
				// detach them.
				GLES30.glDeleteShader(fs);
			}

			// Same as above.
			GLES30.glDeleteShader(vs);
		}

		return p;
	}

	private static int linkProgram(int... shaders) {
		int p = GLES30.glCreateProgram();

		if (p == 0) {
			return 0;
		}

		List<ShaderError> infoLog = null;

		for (int shader : shaders) {
			GLES30.glAttachShader(p, shader);
		}

		GLES30.glLinkProgram(p);

		int[] linkStatus = new int[1];
		GLES30.glGetProgramiv(
				p,
				GLES30.GL_LINK_STATUS,
				linkStatus,
				0);

		if (linkStatus[0] != GLES30.GL_TRUE) {
			infoLog = ShaderError.parseAll(GLES30.glGetProgramInfoLog(p));

			GLES30.glDeleteProgram(p);
			p = 0;
		}

		Program.infoLog = infoLog == null ? Collections.emptyList() : infoLog;

		return p;
	}

	private static int compileShader(int type, String src) {
		int s = GLES30.glCreateShader(type);

		if (s == 0) {
			infoLog = List.of(ShaderError.createGeneral("Cannot create shader"));
			return 0;
		}

		GLES30.glShaderSource(s, src);
		GLES30.glCompileShader(s);

		int[] compiled = new int[1];
		GLES30.glGetShaderiv(
				s,
				GLES30.GL_COMPILE_STATUS,
				compiled,
				0);

		if (compiled[0] == 0) {
			infoLog = ShaderError.parseAll(GLES30.glGetShaderInfoLog(s));

			GLES30.glDeleteShader(s);
			s = 0;
		}

		return s;
	}
}
