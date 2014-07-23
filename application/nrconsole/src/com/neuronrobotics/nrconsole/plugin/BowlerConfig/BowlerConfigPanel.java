package com.neuronrobotics.nrconsole.plugin.BowlerConfig;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.sun.org.apache.xml.internal.serialize.Printer;

import net.miginfocom.swing.MigLayout;

import javax.swing.JList;

import org.jfree.ui.WizardDialog;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class BowlerConfigPanel extends JPanel implements KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BowlerBoardDevice delt;
	
	private NRPrinter printer;
	private List<String> axesNames = new ArrayList<String>();
	private JTextField tfName;
	private JTextField tfNumAxes;
	private JList<String> listAxes;
	private JTextField tfCurrVal;
	private JTextField tfkP;
	private JTextField tfkI;
	private JTextField tfkD;
	private JTextField tfMin;
	private JTextField tfMax;

	public BowlerConfigPanel(){
		setLayout(new MigLayout("", "[grow][grow][grow]", "[][][grow,center][grow][grow][grow][grow][][][]"));
		
		
		
	}
	
	
	private void initializeGUI(){
		JLabel lblName = new JLabel("Device Name:");
		add(lblName, "cell 0 0,alignx trailing");
		
		tfName = new JTextField();
		tfName.setEditable(false);
		add(tfName, "cell 1 0 2 1,growx");
		tfName.setColumns(10);
		tfName.setText("Printer");
		
		JLabel lblNumberOfAxes = new JLabel("Number of Axes:");
		add(lblNumberOfAxes, "cell 0 1,alignx trailing");
		
		tfNumAxes = new JTextField();
		tfNumAxes.setEditable(false);
		add(tfNumAxes, "cell 1 1 2 1,growx");
		tfNumAxes.setColumns(10);
		tfNumAxes.setText(String.valueOf(printer.getNumberOfLinks()));
		
		listAxes = new JList<String>();
		add(listAxes, "cell 0 2 1 6,grow");
		
		JLabel lblNewLabel = new JLabel("Current Value");
		add(lblNewLabel, "cell 1 2,alignx trailing");
		
		tfCurrVal = new JTextField();
		tfCurrVal.addKeyListener(this);		
		add(tfCurrVal, "cell 2 2,growx");
		tfCurrVal.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("kP");
		add(lblNewLabel_1, "cell 1 3,alignx trailing");
		
		tfkP = new JTextField();
		tfkP.addKeyListener(this);
		add(tfkP, "cell 2 3,growx");
		tfkP.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("kI");
		add(lblNewLabel_2, "cell 1 4,alignx trailing");
		
		tfkI = new JTextField();
		tfkI.addKeyListener(this);
		add(tfkI, "cell 2 4,growx");
		tfkI.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("kD");
		add(lblNewLabel_3, "cell 1 5,alignx trailing");
		
		tfkD = new JTextField();
		tfkD.addKeyListener(this);
		add(tfkD, "cell 2 5,growx");
		tfkD.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Minimum");
		add(lblNewLabel_4, "cell 1 6,alignx trailing");
		
		tfMin = new JTextField();
		tfMin.addKeyListener(this);
		add(tfMin, "cell 2 6,growx");
		tfMin.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Maximum");
		add(lblNewLabel_5, "cell 1 7,alignx trailing");
		
		tfMax = new JTextField();
		tfMax.addKeyListener(this);
		add(tfMax, "cell 2 7,growx");
		tfMax.setColumns(10);
		
		
		
		JButton btnSendNewValues = new JButton("Send New Values");
		btnSendNewValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(btnSendNewValues, "cell 0 8 3 1,growx");
		
		JButton btnGetCurrentValues = new JButton("Get Current Values");
		btnGetCurrentValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LinkConfiguration selectedLink = printer.getLinkConfiguration(listAxes.getSelectedIndex());
				tfkP.setText(String.valueOf(selectedLink.getKP()));
				tfkI.setText(String.valueOf(selectedLink.getKI()));
				tfkD.setText(String.valueOf(selectedLink.getKD()));
				tfMin.setText(String.valueOf(selectedLink.getLowerLimit()));
				tfMax.setText(String.valueOf(selectedLink.getUpperLimit()));
				
				
				
			}
		});
		add(btnGetCurrentValues, "cell 0 9 3 1,growx");
		for (LinkConfiguration link : printer.getLinkConfigurations()) {
			axesNames.add(0, link.getName());
		}
		listAxes.setListData(axesNames.toArray(new String[axesNames.size()]));
	}
	
	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.delt = delt;
		this.printer = printer;
		initializeGUI();
		
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		if (!Character.isDigit(arg0.getKeyChar())){
			arg0.consume();
		}
	}


	/**
	 * @return the model
	 */
	public AbstractKinematicsNR getModel() {
		return model;
	}


	/**
	 * @param model the model to set
	 */
	public void setModel(AbstractKinematicsNR model) {
		this.model = model;
	}
	

	
	


}
