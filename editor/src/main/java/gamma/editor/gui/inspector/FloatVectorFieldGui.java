package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.scene.Component;
import vecmatlib.vector.VecFloat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

public class FloatVectorFieldGui implements IFieldGui {

	private final InputFunction inputFunction;
	private final DragFunction dragFunction;
	private final SliderFunction sliderFunction;
	private final Function<float[], VecFloat<?>> constructorFunction;

	public FloatVectorFieldGui(InputFunction inputFunction, DragFunction dragFunction, SliderFunction sliderFunction, Function<float[], VecFloat<?>> constructorFunction) {
		this.inputFunction = inputFunction;
		this.dragFunction = dragFunction;
		this.sliderFunction = sliderFunction;
		this.constructorFunction = constructorFunction;
	}

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		VecFloat<?> vector = (VecFloat<?>) field.get(component);
		float[] ptr = vector.toArray();
		if(slider != null) {
			if(this.sliderFunction.apply("##" + component.getClass() + ":" + field.getName(), ptr, slider.min(), slider.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(component, vector);
				values.put(field.getName(), vector);
			}
		} else if(range != null) {
			if(this.dragFunction.apply("##" + component.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(component, vector);
				values.put(field.getName(), vector);
			}
		} else if(this.inputFunction.apply("##" + component.getClass() + ":" + field.getName(), ptr)) {
			vector = this.constructorFunction.apply(ptr);
			field.set(component, vector);
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
