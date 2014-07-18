package com.neuronrobotics.nrconsole.plugin.BowlerConfig;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

import net.miginfocom.swing.MigLayout;

import javax.swing.JList;


public class BowlerConfigPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BowlerBoardDevice delt;
	private NRPrinter printer;
	private List<String> axesNames;
	private JTextField tfName;
	private JTextField tfNumAxes;

	public BowlerConfigPanel(){
		setLayout(new MigLayout("", "[grow][grow]", "[][][grow]"));
		
		JLabel lblName = new JLabel("Device Name:");
		add(lblName, "cell 0 0,alignx trailing");
		
		tfName = new JTextField();
		tfName.setEditable(false);
		add(tfName, "cell 1 0,growx");
		tfName.setColumns(10);
		tfName.setText("Printer");
		
		JLabel lblNumberOfAxes = new JLabel("Number of Axes:");
		add(lblNumberOfAxes, "cell 0 1,alignx trailing");
		
		tfNumAxes = new JTextField();
		tfNumAxes.setEditable(false);
		add(tfNumAxes, "cell 1 1,growx");
		tfNumAxes.setColumns(10);
		tfNumAxes.setText(String.valueOf(printer.getNumberOfLinks()));
		
		JList<String> listAxes = new JList<String>();
		add(listAxes, "cell 0 2,grow");
		for (LinkConfiguration link : printer.getLinkConfigurations()) {
			axesNames.add(0, link.getName());
		}
		listAxes.setListData(axesNames.toArray(new String[axesNames.size()]));
	}
	
	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.delt = delt;
		this.printer = printer;
		
	}

	

	
	


}
