package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.tree.Node;
import io.github.hexagonnico.vecmatlib.vector.VecInt;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

public class IntVectorField implements InspectorField {

	private final InputFunction inputFunction;
	private final DragFunction dragFunction;
	private final SliderFunction sliderFunction;
	private final Function<int[], VecInt<?>> constructorFunction;

	public IntVectorField(InputFunction inputFunction, DragFunction dragFunction, SliderFunction sliderFunction, Function<int[], VecInt<?>> constructorFunction) {
		this.inputFunction = inputFunction;
		this.dragFunction = dragFunction;
		this.sliderFunction = sliderFunction;
		this.constructorFunction = constructorFunction;
	}

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		VecInt<?> vector = (VecInt<?>) field.get(node);
		int[] ptr = vector.toArray();
		if(slider != null) {
			if(this.sliderFunction.apply("##" + node.getClass() + ":" + field.getName(), ptr, (int) slider.min(), (int) slider.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(node, vector);
				values.put(field.getName(), vector);
			}
		} else if(range != null) {
			if(this.dragFunction.apply("##" + node.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(node, vector);
				values.put(field.getName(), vector);
			}
		} else if(this.inputFunction.apply("##" + node.getClass() + ":" + field.getName(), ptr)) {
			vector = this.constructorFunction.apply(ptr);
			field.set(node, vector);
			values.put(field.getName(), vector);
		}
	}

	public interface DragFunction {

		boolean apply(String key, int[] ptr, float step, float min, float max);
	}

	public interface SliderFunction {

		boolean apply(String key, int[] ptr, int min, int max);
	}

	public interface InputFunction {

		boolean apply(String key, int[] ptr);
	}
}
