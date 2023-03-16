package gamma.editor.core;

import gamma.engine.core.components.Camera3D;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import vecmatlib.matrix.Mat4f;
import vecmatlib.vector.Vec3f;

public class EditorCamera extends Camera3D {

	public Vec3f position = Vec3f.Zero();
	public float yaw = 0.0f;
	public float pitch = 0.0f;

	private ImVec2 mouseDragPoint = new ImVec2();

	@Override
	protected void editorUpdate() {
		super.editorUpdate();
		ImGuiIO io = ImGui.getIO();
		if(!io.getWantCaptureMouse()) {
			ImVec2 nextDragPoint = ImGui.getMousePos();
			float dragX = (this.mouseDragPoint.x - nextDragPoint.x);
			float dragY = (nextDragPoint.y - this.mouseDragPoint.y);
			if(ImGui.isMouseDown(0)) {
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
			this.mouseDragPoint = nextDragPoint;
		}
	}

	@Override
	public Vec3f globalPosition() {
		return position;
	}

	@Override
	public Mat4f viewMatrix() {
		return Mat4f.translation(this.globalPosition().negated()).multiply(Mat4f.rotation(this.yaw, this.pitch, 0.0f));
	}
}
