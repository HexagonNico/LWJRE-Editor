package gamma.engine.editor;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLX;
import org.lwjgl.opengl.GLX13;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.jawt.*;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.linux.XVisualInfo;

import java.awt.*;
import java.util.Objects;

public final class EditorCanvas extends Canvas {

	private final JAWT jawt;
	private final JAWTDrawingSurface drawingSurface;

	private long context;
	private GLCapabilities capabilities;

	public EditorCanvas() {
		this.jawt = JAWT.calloc();
		this.jawt.version(JAWTFunctions.JAWT_VERSION_9);
		if(!JAWTFunctions.JAWT_GetAWT(this.jawt)) {
			throw new IllegalStateException("Failed to initialize JAWT");
		}
		this.drawingSurface = JAWTFunctions.JAWT_GetDrawingSurface(this, this.jawt.GetDrawingSurface());
		if(this.drawingSurface == null) {
			throw new IllegalStateException("Failed to get JAWT drawing surface");
		}
	}

	@Override
	public void update(Graphics graphics) {
		this.paint(graphics);
	}

	@Override
	public void paint(Graphics graphics) {
		this.paint();
		super.repaint();
	}

	private void paint() {
		int lock = JAWTFunctions.JAWT_DrawingSurface_Lock(this.drawingSurface, this.drawingSurface.Lock());
		if((lock & JAWTFunctions.JAWT_LOCK_ERROR) != 0) {
			throw new IllegalStateException("Could not lock the drawing surface");
		}
		try {
			JAWTDrawingSurfaceInfo info = JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo(this.drawingSurface, this.drawingSurface.GetDrawingSurfaceInfo());
			if(info == null) {
				throw new IllegalStateException("Could not get drawing surface info");
			}
			try {
				switch(Platform.get()) {
					case LINUX -> this.linuxPlatform(info);
					case WINDOWS -> this.windowsPlatform(info);
				}
			} finally {
				JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo(info, this.drawingSurface.FreeDrawingSurfaceInfo());
			}
		} finally {
			JAWTFunctions.JAWT_DrawingSurface_Unlock(this.drawingSurface, this.drawingSurface.Unlock());
		}
	}

	private void linuxPlatform(JAWTDrawingSurfaceInfo info) {
		JAWTX11DrawingSurfaceInfo dsi_x11 = JAWTX11DrawingSurfaceInfo.create(info.platformInfo());
		long drawable = dsi_x11.drawable();
		if (drawable == MemoryUtil.NULL) {
			return;
		}
		if (context == MemoryUtil.NULL) {
			long display = dsi_x11.display();
			drawable = dsi_x11.drawable();
			PointerBuffer configs = Objects.requireNonNull(GLX13.glXGetFBConfigs(display, 0));
			long config = MemoryUtil.NULL;
			for (int i = 0; i < configs.remaining(); i++) {
				try(XVisualInfo vi = GLX13.glXGetVisualFromFBConfig(display, configs.get(i))) {
					if (vi == null) {
						continue;
					}
					if (vi.visualid() == dsi_x11.visualID()) {
						config = configs.get(i);
						break;
					}
				}
			}
			X11.XFree(configs);
			if (config == MemoryUtil.NULL) {
				throw new IllegalStateException("Failed to find a compatible GLXFBConfig");
			}
			this.context = GLX13.glXCreateNewContext(display, config, GLX13.GLX_RGBA_TYPE, MemoryUtil.NULL, true);
			if (this.context == MemoryUtil.NULL) {
				throw new IllegalStateException("glXCreateContext() failed");
			}

			if (!GLX.glXMakeCurrent(display, drawable, this.context)) {
				throw new IllegalStateException("glXMakeCurrent() failed");
			}

			this.capabilities = GL.createCapabilities();
		} else {
			if (!GLX.glXMakeCurrent(dsi_x11.display(), drawable, this.context)) {
				throw new IllegalStateException("glXMakeCurrent() failed");
			}
			GL.setCapabilities(this.capabilities);
		}
		this.render();
		GLX.glXSwapBuffers(dsi_x11.display(), drawable);
		GLX.glXMakeCurrent(dsi_x11.display(), MemoryUtil.NULL, MemoryUtil.NULL);
		GL.setCapabilities(null);
	}

	private void windowsPlatform(JAWTDrawingSurfaceInfo info) {
		JAWTWin32DrawingSurfaceInfo dsi_win = JAWTWin32DrawingSurfaceInfo.create(info.platformInfo());
		long hdc = dsi_win.hdc();
		if (hdc == MemoryUtil.NULL) {
			return;
		}

		if (this.context == MemoryUtil.NULL) {
			this.context = GLFWNativeWin32.glfwAttachWin32Window(dsi_win.hwnd(), MemoryUtil.NULL);
			if (this.context == MemoryUtil.NULL) {
				throw new IllegalStateException("Failed to attach win32 window.");
			}
			GLFW.glfwMakeContextCurrent(context);
			this.capabilities = GL.createCapabilities();
		} else {
			GLFW.glfwMakeContextCurrent(this.context);
			GL.setCapabilities(this.capabilities);
		}
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			IntBuffer pw = stack.mallocInt(1);
//			IntBuffer ph = stack.mallocInt(1);
//
//			GLFW.glfwGetFramebufferSize(context, pw, ph);

			this.render();
//		}
		GLFW.glfwSwapBuffers(this.context);
		GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
		GL.setCapabilities(null);
	}

	private void render() {

	}

	public void destroy() {
		JAWTFunctions.JAWT_FreeDrawingSurface(this.drawingSurface, this.jawt.FreeDrawingSurface());
		this.jawt.free();
	}
}
