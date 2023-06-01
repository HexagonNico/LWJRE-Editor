package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.tree.Node;
import io.github.hexagonnico.vecmatlib.vector.VecFloat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

public class FloatVectorField implements InspectorField {

	private final InputFunction inputFunction;
	private final DragFunction dragFunction;
	private final SliderFunction sliderFunction;
	private final Function<float[], VecFloat<?>> constructorFunction;

	public FloatVectorField(InputFunction inputFunction, DragFunction dragFunction, SliderFunction sliderFunction, Function<float[], VecFloat<?>> constructorFunction) {
		this.inputFunction = inputFunction;
		this.dragFunction = dragFunction;
		this.sliderFunction = sliderFunction;
		this.constructorFunction = constructorFunction;
	}

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		VecFloat<?> vector = (VecFloat<?>) field.get(node);
		float[] ptr = vector.toArray();
		String label = "##" + node.getClass() + ":" + field.getName();
		if(slider != null) {
			if(this.sliderFunction.apply(label, ptr, slider.min(), slider.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(node, vector);
				values.put(field.getName(), vector);
			}
		} else if(range != null) {
			if(this.dragFunction.apply(label, ptr, range.step(), range.min(), range.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(node, vector);
				values.put(field.getName(), vector);
			}
		} else if(this.inputFunction.apply(label, ptr)) {
			vector = this.constructorFunction.apply(ptr);
			field.set(node, vector);
			values.put(field.getName(), vector);
		}
	}

	public interface DragFunction {

		boolean apply(String key, float[] ptr, float step, float min, float max);
	}

	public interface SliderFunction {

		boolean apply(String key, float[] ptr, float min, float max);
	}

	public interface InputFunction {

		boolean apply(String key, float[] ptr);
	}
}
