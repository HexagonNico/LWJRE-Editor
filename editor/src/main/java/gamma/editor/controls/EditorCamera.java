package gamma.editor.controls;

import gamma.engine.resources.Shader;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import vecmatlib.matrix.Mat4f;
import vecmatlib.vector.Vec2i;
import vecmatlib.vector.Vec3f;

/**
 * Represents the editor's camera.
 *
 * @author Nico
 */
public final class EditorCamera {

	/** Camera position in the 3D world */
	public Vec3f position = Vec3f.Zero();
	/** Camera x rotation */
	public float yaw = 0.0f;
	/** Camera y rotation */
	public float pitch = 0.0f;

	/** Field of view */
	public float fov = 1.22173f;
	/** Near plane distance */
	public float nearPlane = 0.1f;
	/** Far plane distance */
	public float farPlane = 1000.0f;

	/** Position of the mouse cursor */
	private ImVec2 mouseDragPoint = new ImVec2();

	/**
	 * Detects user mouse input and updates the camera.
	 */
	public void update() {
		ImGuiIO io = ImGui.getIO();
		ImVec2 nextDragPoint = ImGui.getMousePos();
		if(ImGui.isWindowFocused()) {
			float dragX = (this.mouseDragPoint.x - nextDragPoint.x);
			float dragY = (nextDragPoint.y - this.mouseDragPoint.y);
			if(io.getMouseDown(0)) {
				this.position = this.position.plus(dragX * 0.0075f, dragY * 0.0075f, 0.0f);
			} else if(ImGui.isMouseDown(1)) {
				this.pitch += dragX * 0.001f;
				this.yaw -= dragY * 0.001f;
			}
			float scroll = io.getMouseWheel();
			if(scroll != 0.0f) {
				float cosYaw = (float) Math.cos(Math.toRadians(this.yaw));
				float sinYaw = (float) Math.sin(Math.toRadians(this.yaw));
				float cosPitch = (float) Math.cos(Math.toRadians(this.pitch));
				float sinPitch = (float) Math.sin(Math.toRadians(this.pitch));
				Vec3f forward = new Vec3f(-cosPitch * sinYaw, sinPitch, -cosPitch * cosYaw).normalized();
				this.position = this.position.plus(forward.multipliedBy(scroll));
			}
		}
		this.mouseDragPoint = nextDragPoint;
		Shader.setUniformStatic("projection_matrix", this.projectionMatrix());
		Shader.setUniformStatic("view_matrix", this.viewMatrix());
	}

	/**
	 * Computes the camera's projection matrix.
	 *
	 * @return Editor projection matrix
	 */
	public Mat4f projectionMatrix() {
		float focalLength = (float) (1.0f / Math.tan(this.fov / 2.0f));
		Vec2i windowSize = new Vec2i(1920, 1080); // TODO: Proper viewport size
		float aspect = (float) windowSize.x() / windowSize.y();
		return new Mat4f(
				focalLength, 0.0f, 0.0f, 0.0f,
				0.0f, focalLength * aspect, 0.0f, 0.0f,
				0.0f, 0.0f, -(this.farPlane + this.nearPlane) / (this.farPlane - this.nearPlane), -(2 * this.farPlane * this.nearPlane) / (this.farPlane - this.nearPlane),
				0.0f, 0.0f, -1.0f, 0.0f
		);
	}

	/**
	 * Computes the camera's view matrix.
	 *
	 * @return Editor view matrix
	 */
	public Mat4f viewMatrix() {
		return Mat4f.translation(this.position.negated()).multiply(Mat4f.rotation(this.yaw, this.pitch, 0.0f));
	}
}
