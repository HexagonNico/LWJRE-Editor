package gamma.editor.controls;

import gamma.engine.rendering.Shader;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import io.github.hexagonnico.vecmatlib.matrix.Mat4f;
import io.github.hexagonnico.vecmatlib.vector.Vec2i;
import io.github.hexagonnico.vecmatlib.vector.Vec3f;

public final class EditorCamera {

	public Vec3f position = new Vec3f(0.0f, 7.5f, 15.0f);
	public float yaw = 0.0f;
	public float pitch = 0.0f;

	public float fov = 1.22173f;
	public float nearPlane = 0.1f;
	public float farPlane = 1000.0f;

	private ImVec2 mouseDragPoint = new ImVec2();

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

	public Mat4f viewMatrix() {
		return Mat4f.translation(this.position.negated()).multiply(Mat4f.rotation(this.yaw, this.pitch, 0.0f));
	}
}
