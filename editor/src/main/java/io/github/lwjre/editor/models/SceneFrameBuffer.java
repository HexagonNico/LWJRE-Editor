package io.github.lwjre.editor.models;

import io.github.hexagonnico.vecmatlib.vector.Vec2i;
import io.github.lwjre.engine.debug.DebugRenderer;
import io.github.lwjre.engine.servers.RenderingServer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Structure that represents an OpenGL frame buffer used to render the current scene to a texture to be displayed in the editor.
 *
 * @author Nico
 */
public class SceneFrameBuffer {

	/** Frame buffer object */
	public final int frameBuffer;
	/** Frame buffer texture */
	public final int texture;
	/** Render buffer */
	public final int renderBuffer;

	/** Width of the frame buffer */
	private final int width;
	/** Height of the frame buffer */
	private final int height;

	/**
	 * Constructs a frame buffer of the given size.
	 *
	 * @param width Width of the frame buffer
	 * @param height Height of the frame buffer
	 */
	public SceneFrameBuffer(int width, int height) {
		this.frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBuffer);
		this.texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.texture, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this.renderBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT32, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBuffer);
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Could not create frame buffer");
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a frame buffer of the given size.
	 *
	 * @param size Size of the frame buffer
	 */
	public SceneFrameBuffer(Vec2i size) {
		this(size.x(), size.y());
	}

	/**
	 * Binds the frame buffer and renders the current scene to its texture.
	 * The texture can be used with {@link SceneFrameBuffer#texture}.
	 */
	public void drawScene() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBuffer);
		RenderingServer.setViewport(this.width, this.height);
		RenderingServer.deptTest(true);
		RenderingServer.backFaceCulling(true);
		RenderingServer.clearScreen();
		RenderingServer.render();
		RenderingServer.lineWidth(3.0f);
		RenderingServer.fillPolygons(false);
		RenderingServer.backFaceCulling(false);
		DebugRenderer.render();
		RenderingServer.lineWidth(1.0f);
		RenderingServer.fillPolygons(true);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	/**
	 * Gets the frame buffer's aspect ratio.
	 *
	 * @return The ratio between the frame buffer's width and the frame buffer's height
	 */
	public float aspectRatio() {
		return (float) this.width / this.height;
	}

	/**
	 * Deletes the frame buffer.
	 * Must be called when the frame buffer is no longer being used.
	 */
	public void cleanUp() {
		GL30.glDeleteRenderbuffers(this.renderBuffer);
		GL11.glDeleteTextures(this.texture);
		GL30.glDeleteFramebuffers(this.frameBuffer);
	}
}
