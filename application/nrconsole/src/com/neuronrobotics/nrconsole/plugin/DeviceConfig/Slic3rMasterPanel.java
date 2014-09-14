package com.neuronrobotics.nrconsole.plugin.DeviceConfig;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import net.miginfocom.swing.MigLayout;

import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Slic3rMasterPanel extends SettingsPanel {
	private JScrollPane scrollPane;
	private JPanel panel;
	private JRadioButton rdbtnShowAllSettings;
	private JPanel panel_1;

	private Slic3rAll pnlAll = new Slic3rAll(this);
	private Slic3rPrinter pnlPrinter = new Slic3rPrinter(this);
	private JRadioButton rdbtnShowOnlyPrinter;
	private JRadioButton rdbtnShowOnlyPrint;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Create the panel.
	 */
	public Slic3rMasterPanel() {
		
		initComponents();
		whichPanel();

	}
	
	
	
	
	private SettingsPanel whichPanel(){
		if (rdbtnShowAllSettings.isSelected()){
			removeListeners();
			addListener(pnlAll);
			return pnlAll;
		}
		else if (rdbtnShowOnlyPrinter.isSelected()){
			removeListeners();
			addListener(pnlPrinter);
			return pnlPrinter;
		}
		else{
			removeListeners();
			addListener(pnlAll);
			return pnlAll;
		}
			
	}
	private void changePanels(){
		scrollPane.setViewportView(whichPanel());
	}
	
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setColumnHeaderView(getPanel());
			changePanels();
		}
		return scrollPane;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.add(getRdbtnShowOnlyPrinter());
			panel.add(getRdbtnShowOnlyPrint());
			panel.add(getRdbtnShowAllSettings());
		}
		return panel;
	}
	private JRadioButton getRdbtnShowAllSettings() {
		if (rdbtnShowAllSettings == null) {
			rdbtnShowAllSettings = new JRadioButton("Show All Settings");
			rdbtnShowAllSettings.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					changePanels();
				}
			});
			buttonGroup.add(rdbtnShowAllSettings);
		}
		return rdbtnShowAllSettings;
	}
	
		
	@Override
	public String getPanelName() {
		
		return "Slic3r Settings";
	}

	@Override
	public void initComponents() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		add(getScrollPane(), "cell 0 0,grow");
		
	}
	private JRadioButton getRdbtnShowOnlyPrinter() {
		if (rdbtnShowOnlyPrinter == null) {
			rdbtnShowOnlyPrinter = new JRadioButton("Show Only Printer Settings");
			rdbtnShowOnlyPrinter.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					changePanels();
				}
			});
			buttonGroup.add(rdbtnShowOnlyPrinter);
		}
		return rdbtnShowOnlyPrinter;
	}
	private JRadioButton getRdbtnShowOnlyPrint() {
		if (rdbtnShowOnlyPrint == null) {
			rdbtnShowOnlyPrint = new JRadioButton("Show Only Print Job Settings");
			rdbtnShowOnlyPrint.setEnabled(false);
			rdbtnShowOnlyPrint.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					changePanels();
				}
			});
			buttonGroup.add(rdbtnShowOnlyPrint);
		}
		return rdbtnShowOnlyPrint;
	}
}
