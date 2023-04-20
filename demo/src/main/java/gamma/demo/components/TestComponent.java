package gamma.demo.components;

import gamma.engine.components.Transform3D;
import gamma.engine.rendering.DebugRenderer;
import gamma.engine.scene.Component;

public class TestComponent extends Component {

	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("[" + this.entity() + "]: Test component start");
	}

	@Override
	protected void editorUpdate() {
		super.editorUpdate();
		this.getComponent(Transform3D.class).ifPresent(transform -> DebugRenderer.addToBatch(this, transform.globalTransformation()));
	}

	@Override
	protected void onExit() {
		super.onExit();
		System.out.println("[" + this.entity() + "]: Test component exit");
	}
}
