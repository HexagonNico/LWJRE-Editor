package gamma.demo.components;

import gamma.engine.annotations.*;
import gamma.engine.components.Transform3D;
import gamma.engine.rendering.DebugRenderer;
import gamma.engine.scene.Component;
import vecmatlib.vector.Vec2f;
import vecmatlib.vector.Vec2i;

public class TestComponent extends Component {

	@EditorVariable
	@EditorRange
	private float test1 = 0.0f;

	@EditorVariable
	@EditorRange
	private int test2 = 0;

	@EditorVariable
	@EditorRange
	private Vec2f test3 = new Vec2f(0.1f, 0.1f);

	@EditorVariable
	@EditorRange
	private Vec2i test4 = new Vec2i(1, 1);

	@EditorVariable
	@EditorSlider(min = -100, max = 100)
	private float test11 = 0.0f;

	@EditorVariable
	@EditorSlider(min = -100, max = 100)
	private int test21 = 0;

	@EditorVariable
	@EditorSlider(min = -100, max = 100)
	private Vec2f test31 = new Vec2f(0.1f, 0.1f);

	@EditorVariable
	@EditorSlider(min = -100, max = 100)
	private Vec2i test41 = new Vec2i(1, 1);

	@EditorVariable
	@EditorAngle
	private float test12 = 0.0f;

	@EditorVariable
	@EditorAngle
	private int test22 = 0;

	@EditorVariable
	@EditorAngle
	private Vec2f test32 = new Vec2f(0.1f, 0.1f);

	@EditorVariable
	@EditorAngle
	private Vec2i test42 = new Vec2i(1, 1);

	@EditorVariable
	private String text1 = "text1";

	@EditorVariable
	@EditorText(maxLength = 8)
	private String text2 = "text2";

	@EditorVariable
	@EditorText(multiline = true)
	private String text3 = "text3";

	@EditorVariable
	@EditorText(hint = "An interesting hint")
	private String text4 = "";

	@EditorVariable
	@EditorText(hint = "An interesting hint", multiline = true)
	private String text5 = "";

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
