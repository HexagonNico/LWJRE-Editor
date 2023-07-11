package io.github.lwjre.editor.gui.fields;

import io.github.hexagonnico.vecmatlib.vector.VecFloat;
import io.github.lwjre.engine.annotations.EditorRange;
import io.github.lwjre.engine.annotations.EditorSlider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

public class FloatVectorFieldGui implements FieldGui {

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
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		VecFloat<?> vector = (VecFloat<?>) field.get(object);
		float[] ptr = vector.toArray();
		String label = "##" + object.getClass() + ":" + field.getName();
		if(slider != null) {
			if(this.sliderFunction.apply(label, ptr, slider.min(), slider.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(object, vector);
				values.put(field.getName(), vector);
			}
		} else if(range != null) {
			if(this.dragFunction.apply(label, ptr, range.step(), range.min(), range.max())) {
				vector = this.constructorFunction.apply(ptr);
				field.set(object, vector);
				values.put(field.getName(), vector);
			}
		} else if(this.inputFunction.apply(label, ptr)) {
			vector = this.constructorFunction.apply(ptr);
			field.set(object, vector);
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
