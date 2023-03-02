package gamma.engine.editor;

import gamma.engine.core.components.Camera3D;
import vecmatlib.matrix.Mat4f;
import vecmatlib.vector.Vec2i;
import vecmatlib.vector.Vec3f;

public class EditorCamera extends Camera3D {

	public Vec3f position = new Vec3f(0.0f, 0.0f, 5.0f);
	public float yaw = 0.0f, pitch = 0.0f;
	public Vec2i viewport = new Vec2i(180, 90);

	@Override
	public Vec3f globalPosition() {
		// TODO: Camera controls
		return this.position;
	}

	@Override
	public Mat4f projectionMatrix() {
		float m00 = 1.0f / (float) Math.tan(fov / 2.0f);
		float m11 = m00 * ((float) this.viewport.x() / this.viewport.y());
		float m22 = -(farPlane + nearPlane) / (farPlane - nearPlane);
		float m23 = -(2 * farPlane * nearPlane) / (farPlane - nearPlane);
		return new Mat4f(
				m00, 0.0f, 0.0f, 0.0f,
				0.0f, m11, 0.0f, 0.0f,
				0.0f, 0.0f, m22, m23,
				0.0f, 0.0f, -1.0f, 0.0f
		);
	}

	public Vec3f forwardDirection() {
		float cosYaw = (float) Math.cos(Math.toRadians(this.yaw));
		float sinYaw = (float) Math.sin(Math.toRadians(this.yaw));
		float cosPitch = (float) Math.cos(Math.toRadians(this.pitch));
		float sinPitch = (float) Math.sin(Math.toRadians(this.pitch));
		return new Vec3f(-cosPitch * sinYaw, sinPitch, -cosPitch * cosYaw).normalized();
	}

	@Override
	public Mat4f viewMatrix() {
		return Mat4f.translation(this.globalPosition().negated()).multiply(Mat4f.rotation(this.yaw, this.pitch, 0.0f));
	}
}
