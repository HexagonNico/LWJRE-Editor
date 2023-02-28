package gamma.engine.editor.inspector;

import gamma.engine.core.scene.Component;
import gamma.engine.core.scene.Entity;
import vecmatlib.vector.Vec3f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.HashMap;

public class InspectorPanel extends JScrollPane {

	private static final HashMap<Class<?>, ComponentFunction> COMPONENT_FUNCTIONS = new HashMap<>();

	static {
		COMPONENT_FUNCTIONS.put(float.class, (field, component, fieldsPanel) -> {
			JFormattedTextField textField = new JFormattedTextField(new DecimalFormat());
			field.setAccessible(true);
			textField.setValue(field.get(component));
			textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, 20));
			textField.addActionListener(actionEvent -> {
				try {
					// TODO: This only works when you press 'enter'
					field.set(component, Float.parseFloat(textField.getText()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			fieldsPanel.add(textField);
		});
		COMPONENT_FUNCTIONS.put(byte.class, (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
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
		COMPONENT_FUNCTIONS.put(short.class, (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
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
		COMPONENT_FUNCTIONS.put(int.class, (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
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
		COMPONENT_FUNCTIONS.put(long.class, (field, component, fieldsPanel) -> {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Long.MIN_VALUE, Long.MAX_VALUE, 1));
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
			JFormattedTextField xTextField = new JFormattedTextField(new DecimalFormat());
			JFormattedTextField yTextField = new JFormattedTextField(new DecimalFormat());
			JFormattedTextField zTextField = new JFormattedTextField(new DecimalFormat());
			field.setAccessible(true);
			xTextField.setValue(vector.x());
			yTextField.setValue(vector.y());
			zTextField.setValue(vector.z());
			panel.add(xTextField);
			panel.add(yTextField);
			panel.add(zTextField);
			panel.setMaximumSize(new Dimension(panel.getPreferredSize().width, 20));
			ActionListener actionListener = actionEvent -> {
				try {
					field.set(component, new Vec3f(Float.parseFloat(xTextField.getText()), Float.parseFloat(yTextField.getText()), Float.parseFloat(zTextField.getText())));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
			xTextField.addActionListener(actionListener);
			yTextField.addActionListener(actionListener);
			zTextField.addActionListener(actionListener);
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
