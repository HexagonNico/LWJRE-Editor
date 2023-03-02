package gamma.engine.editor.inspector;

import gamma.engine.core.scene.Component;
import gamma.engine.core.scene.Entity;
import vecmatlib.vector.Vec3f;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class InspectorPanel extends JScrollPane {

	private static final HashMap<Class<?>, ComponentFunction> COMPONENT_FUNCTIONS = new HashMap<>();

	static {
		COMPONENT_FUNCTIONS.put(float.class, (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0f, null, null, 0.01f));
			field.setAccessible(true);
			spinner.setValue(field.get(component));
			spinner.setMaximumSize(new Dimension(spinner.getPreferredSize().width, 20));
			spinner.addChangeListener(changeEvent -> {
				try {
					field.set(component, spinner.getValue());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			fieldsPanel.add(spinner);
		});
		ComponentFunction integer = (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, null, null, 1));
			field.setAccessible(true);
			spinner.setValue(field.get(component));
			spinner.setMaximumSize(new Dimension(spinner.getPreferredSize().width, 20));
			spinner.addChangeListener(changeEvent -> {
				try {
					field.set(component, spinner.getValue());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			fieldsPanel.add(spinner);
		};
		COMPONENT_FUNCTIONS.put(byte.class, integer);
		COMPONENT_FUNCTIONS.put(short.class, integer);
		COMPONENT_FUNCTIONS.put(int.class, integer);
		COMPONENT_FUNCTIONS.put(long.class, integer);
		COMPONENT_FUNCTIONS.put(boolean.class, (field, component, fieldsPanel) -> {
			field.setAccessible(true);
			JCheckBox checkBox = new JCheckBox("", field.getBoolean(component));
			checkBox.addActionListener(actionEvent -> {
				try {
					field.set(component, checkBox.isSelected());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			fieldsPanel.add(checkBox);
		});
		COMPONENT_FUNCTIONS.put(Vec3f.class, (field, component, fieldsPanel) -> {
			JPanel panel = new JPanel(new GridLayout(1, 3, 1, 1));
			Vec3f vector = (Vec3f) field.get(component);
			JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0.0f, null, null, 0.01f));
			JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0.0f, null, null, 0.01f));
			JSpinner zSpinner = new JSpinner(new SpinnerNumberModel(0.0f, null, null, 0.01f));
			field.setAccessible(true);
			xSpinner.setValue(vector.x());
			ySpinner.setValue(vector.y());
			zSpinner.setValue(vector.z());
			panel.add(xSpinner);
			panel.add(ySpinner);
			panel.add(zSpinner);
			panel.setMaximumSize(new Dimension(panel.getPreferredSize().width, 20));
			ChangeListener changeListener = changeEvent -> {
				try {
					field.set(component, new Vec3f((float) xSpinner.getValue(), (float) ySpinner.getValue(), (float) zSpinner.getValue()));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
			xSpinner.addChangeListener(changeListener);
			ySpinner.addChangeListener(changeListener);
			zSpinner.addChangeListener(changeListener);
			fieldsPanel.add(panel);
		});
	}

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
			JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 4, 4));
			fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
			for(Field field : component.getClass().getDeclaredFields()) {
				if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
					JLabel label = new JLabel(field.getName());
					label.setFont(label.getFont().deriveFont(Font.PLAIN));
					fieldsPanel.add(label);
					try {
						if(COMPONENT_FUNCTIONS.containsKey(field.getType())) {
							COMPONENT_FUNCTIONS.get(field.getType()).apply(field, component, fieldsPanel);
						} else {
							fieldsPanel.add(new JPanel());
						}
					} catch(IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			fieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldsPanel.getPreferredSize().height));
			this.viewport.add(fieldsPanel);
		});
		this.viewport.validate();
		this.viewport.repaint();
	}

	public void clearEntity() {
		this.viewport.removeAll();
		this.viewport.validate();
		this.viewport.repaint();
	}

	private interface ComponentFunction {

		void apply(Field field, Component component, JPanel fieldsPanel) throws IllegalAccessException;
	}
}
