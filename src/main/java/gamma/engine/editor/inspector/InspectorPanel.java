package gamma.engine.editor.inspector;

import gamma.engine.core.annotations.EditorFilePath;
import gamma.engine.core.annotations.EditorFloat;
import gamma.engine.core.annotations.EditorVariable;
import gamma.engine.core.scene.Entity;
import gamma.engine.core.utils.EditorRepresent;
import vecmatlib.vector.Vec3f;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

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
			Component fieldsGui = guiRepresentation(component);
			this.viewport.add(fieldsGui);
		});
		this.viewport.validate();
		this.viewport.repaint();
	}

	private static Component guiRepresentation(Object object) {
		JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 4, 4));
		fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		for(Field field : object.getClass().getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
				JLabel fieldNameLabel = new JLabel(field.getName());
				fieldNameLabel.setFont(fieldNameLabel.getFont().deriveFont(Font.PLAIN));
				fieldsPanel.add(fieldNameLabel);
				if(field.isAnnotationPresent(EditorFloat.class) && field.getType().equals(float.class)) {
					EditorFloat annotation = field.getAnnotation(EditorFloat.class);
					NumberRepresent represent = new NumberRepresent(annotation.minValue(), annotation.minValue(), annotation.maxValue(), annotation.stepSize());
					fieldsPanel.add(represent.represent(field, object));
				} else if(field.isAnnotationPresent(EditorFilePath.class) && field.getType().equals(String.class)) {
					// TODO: Handle other annotations as well
				} else if(EditorRepresent.class.isAssignableFrom(field.getType())) {
					try {
						EditorRepresent represent = (EditorRepresent) field.get(object);
						fieldsPanel.add(represent.guiRepresent(field, object));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else if(field.isAnnotationPresent(EditorVariable.class) && REPRESENTS.containsKey(field.getType())) {
					fieldsPanel.add(REPRESENTS.get(field.getType()).represent(field, object));
				} else {
					fieldsPanel.add(new JPanel());
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

	private static final HashMap<Class<?>, GuiRepresent> REPRESENTS = new HashMap<>();

	static {
		REPRESENTS.put(byte.class, new NumberRepresent((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1));
		REPRESENTS.put(short.class, new NumberRepresent((short) 0, Short.MIN_VALUE, Short.MAX_VALUE, (short) 1));
		REPRESENTS.put(int.class, new NumberRepresent(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		REPRESENTS.put(long.class, new NumberRepresent(0L, Long.MIN_VALUE, Long.MAX_VALUE, 1L));
		REPRESENTS.put(float.class, new NumberRepresent(0.0f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
		REPRESENTS.put(double.class, new NumberRepresent(0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01));
		REPRESENTS.put(Vec3f.class, new Vec3Represent());
		REPRESENTS.put(boolean.class, new CheckBoxRepresent());
		REPRESENTS.put(String.class, new TextRepresent());
	}

	private interface GuiRepresent {

		Component represent(Field field, Object owner);
	}

	private record TextRepresent() implements GuiRepresent {

		@Override
		public Component represent(Field field, Object owner) {
			JTextField textField = new JTextField();
			try {
				field.setAccessible(true);
				textField.setText(field.get(owner).toString());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, 20));
			textField.addActionListener(actionEvent -> {
				try {
					field.set(owner, textField.getText());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			return textField;
		}
	}

	private record NumberRepresent(Number value, Comparable<?> minValue, Comparable<?> maxValue, Number stepSize) implements GuiRepresent {

		@Override
		public Component represent(Field field, Object owner) {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, this.minValue, this.maxValue, stepSize));
			field.setAccessible(true);
			try {
				spinner.setValue(field.get(owner));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			spinner.setMaximumSize(new Dimension(spinner.getPreferredSize().width, 20));
			spinner.addChangeListener(changeEvent -> {
				try {
					field.set(owner, spinner.getValue());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			return spinner;
		}
	}

	private record CheckBoxRepresent() implements GuiRepresent {

		@Override
		public Component represent(Field field, Object owner) {
			field.setAccessible(true);
			JCheckBox checkBox = new JCheckBox();
			try {
				checkBox.getModel().setSelected(field.getBoolean(owner));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			checkBox.addActionListener(actionEvent -> {
				try {
					field.set(owner, checkBox.isSelected());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			return checkBox;
		}
	}

	private record Vec3Represent() implements GuiRepresent {

		@Override
		public Component represent(Field field, Object owner) {
			JPanel panel = new JPanel(new GridLayout(1, 3, 1, 1));
			try {
				field.setAccessible(true);
				Vec3f vector = (Vec3f) field.get(owner);
				// TODO: This thing sometimes gives double, sometimes gives float
				JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(vector.x(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
				JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(vector.y(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
				JSpinner zSpinner = new JSpinner(new SpinnerNumberModel(vector.z(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
				panel.add(xSpinner);
				panel.add(ySpinner);
				panel.add(zSpinner);
				ChangeListener changeListener = changeEvent -> {
					try {
						float x = ((Double) xSpinner.getValue()).floatValue();
						float y = ((Double) ySpinner.getValue()).floatValue();
						float z = ((Double) zSpinner.getValue()).floatValue();
						field.set(owner, new Vec3f(x, y, z));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				};
				xSpinner.addChangeListener(changeListener);
				ySpinner.addChangeListener(changeListener);
				zSpinner.addChangeListener(changeListener);
				panel.setMaximumSize(new Dimension(panel.getPreferredSize().width, 20));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return panel;
		}
	}
}
