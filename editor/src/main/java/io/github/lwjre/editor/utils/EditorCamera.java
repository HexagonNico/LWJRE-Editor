package io.github.lwjre.editor.utils;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import io.github.hexagonnico.vecmatlib.vector.Vec3f;
import io.github.lwjre.engine.nodes.Camera3D;

/**
 * Camera used in the editor.
 *
 * @author Nico
 */
public final class EditorCamera extends Camera3D {

	/** Last mouse position */
	private ImVec2 mouseDragPoint = new ImVec2();

	/** Camera forward direction */
	private Vec3f forward = Vec3f.Forward();
	/** Camera right direction */
	private Vec3f right = Vec3f.Right();
	/** Camera up direction */
	private Vec3f up = Vec3f.Up();

	/**
	 * Constructs the editor camera and calls {@link Camera3D#makeCurrent()}.
	 */
	public EditorCamera() {
		this.makeCurrent();
	}

	@Override
	public void onUpdate(float delta) {
		ImGuiIO io = ImGui.getIO();
		ImVec2 nextDragPoint = ImGui.getMousePos();
		if(ImGui.isWindowFocused()) {
			float dragX = (this.mouseDragPoint.x - nextDragPoint.x);
			float dragY = (nextDragPoint.y - this.mouseDragPoint.y);
			if(io.getMouseDown(0)) {
				this.position = this.position.plus(this.right.multipliedBy(dragX * 0.0075f));
				this.position = this.position.plus(this.up.multipliedBy(dragY * 0.0075f));
			} else if(ImGui.isMouseDown(1)) {
				this.rotation = this.rotation.plus(-0.001f * dragY, 0.001f * dragX, 0.0f);
				float cosYaw = (float) Math.cos(this.yaw());
				float sinYaw = (float) Math.sin(this.yaw());
				float cosPitch = (float) Math.cos(this.pitch());
				float sinPitch = (float) Math.sin(this.pitch());
				this.forward = new Vec3f(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch).normalized();
				this.right = new Vec3f(cosYaw, 0.0f, sinYaw);
				this.up = this.forward.cross(this.right);
			}
			float scroll = io.getMouseWheel();
			if(scroll != 0.0f) {
				this.position = this.position.plus(this.forward.multipliedBy(-scroll));
			}
		}
		this.mouseDragPoint = nextDragPoint;
		super.onUpdate(delta);
	}
}
