package gamma.engine.editor.inspector;

import gamma.engine.core.annotations.EditorDegrees;
import gamma.engine.core.annotations.EditorRange;
import gamma.engine.core.annotations.EditorVariable;
import gamma.engine.core.scene.Entity;
import gamma.engine.core.utils.EditorGuiComponent;
import vecmatlib.vector.Vec3f;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;

public class InspectorPanel extends JScrollPane {

	private final JPanel viewport = new JPanel();

	public InspectorPanel() {
		this.viewport.setLayout(new BoxLayout(this.viewport, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(180, 0));
		this.setViewportView(this.viewport);
	}

	public void setEntity(Entity entity) {
		this.viewport.removeAll();
		entity.getComponents().forEach(component -> {
			JPanel panel = new JPanel();
			panel.add(new JLabel(component.getClass().getSimpleName()));
			panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
			this.viewport.add(panel);
			if(component instanceof EditorGuiComponent) {
				JComponent jComponent = ((EditorGuiComponent) component).guiRepresentation(component);
				this.viewport.add(jComponent);
			} else {
				JComponent fieldsGui = guiRepresentation(component);
				this.viewport.add(fieldsGui);
			}
		});
		this.viewport.validate();
		this.viewport.repaint();
	}

	private static JComponent guiRepresentation(Object object) {
		JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 4, 4));
		fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		for(Field field : object.getClass().getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
				if(field.isAnnotationPresent(EditorVariable.class)) {
					JLabel fieldNameLabel = new JLabel();
					fieldNameLabel.setFont(fieldNameLabel.getFont().deriveFont(Font.PLAIN));
					EditorVariable annotation = field.getAnnotation(EditorVariable.class);
					if(annotation.value().isEmpty()) {
						fieldNameLabel.setText(field.getName());
					} else {
						fieldNameLabel.setText(annotation.value());
					}
					fieldsPanel.add(fieldNameLabel);
					if(field.getType().equals(byte.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::byteValue));
					} else if(field.getType().equals(short.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::shortValue));
					} else if(field.getType().equals(int.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::intValue));
					} else if(field.getType().equals(long.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::longValue));
					} else if(field.getType().equals(float.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::floatValue));
					} else if(field.getType().equals(double.class)) {
						fieldsPanel.add(numericFieldToSpinner(field, object, Number::doubleValue));
					} else if(field.getType().equals(boolean.class)) {
						field.setAccessible(true);
						JCheckBox checkBox = new JCheckBox();
						try {
							checkBox.getModel().setSelected(field.getBoolean(object));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						checkBox.addActionListener(actionEvent -> {
							try {
								field.set(object, checkBox.isSelected());
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						});
						fieldsPanel.add(checkBox);
					} else if(field.getType().equals(Vec3f.class)) {
						JPanel panel = new JPanel(new GridLayout(1, 3, 1, 1));
						try {
							Vec3f vec = (Vec3f) field.get(object);
							JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(vec.x(), null, null, 0.01f));
							JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(vec.y(), null, null, 0.01f));
							JSpinner zSpinner = new JSpinner(new SpinnerNumberModel(vec.z(), null, null, 0.01f));
							xSpinner.setMaximumSize(new Dimension(xSpinner.getPreferredSize().width, 20));
							ySpinner.setMaximumSize(new Dimension(ySpinner.getPreferredSize().width, 20));
							zSpinner.setMaximumSize(new Dimension(ySpinner.getPreferredSize().width, 20));
							ChangeListener changeListener = changeEvent -> {
								try {
									field.set(object, new Vec3f(((Number) xSpinner.getValue()).floatValue(), ((Number) ySpinner.getValue()).floatValue(), ((Number) zSpinner.getValue()).floatValue()));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							};
							xSpinner.addChangeListener(changeListener);
							ySpinner.addChangeListener(changeListener);
							zSpinner.addChangeListener(changeListener);
							panel.add(xSpinner);
							panel.add(ySpinner);
							panel.add(zSpinner);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						fieldsPanel.add(panel);
					} else {
						fieldsPanel.add(new JPanel());
					}
				}
			}
		}
		fieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldsPanel.getPreferredSize().height));
		return fieldsPanel;
	}

	public void clearEntity() {
		this.viewport.removeAll();
		this.viewport.validate();
		this.viewport.repaint();
	}

	private static JSpinner numericFieldToSpinner(Field field, Object object, Function<Number, Number> function) {
		JSpinner spinner = new JSpinner();
		if(field.isAnnotationPresent(EditorRange.class)) {
			EditorRange range = field.getAnnotation(EditorRange.class);
			spinner.setModel(new SpinnerNumberModel(Math.max(range.min(), 0.0), range.min(), range.max(), range.step()));
		} else {
			spinner.setModel(new SpinnerNumberModel(0.0f, null, null, 1.0f));
		}
		spinner.setMaximumSize(new Dimension(spinner.getPreferredSize().width, 20));
		if(field.isAnnotationPresent(EditorDegrees.class)) {
			try {
				spinner.setValue(Math.toDegrees(((Number) field.get(object)).doubleValue()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			spinner.addChangeListener(changeEvent -> {
				try {
					field.set(object, function.apply(Math.toRadians((Double) spinner.getValue())));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		} else {
			try {
				spinner.setValue(field.get(object));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			spinner.addChangeListener(changeEvent -> {
				try {
					field.set(object, function.apply((Number) spinner.getValue()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
		return spinner;
	}
}
