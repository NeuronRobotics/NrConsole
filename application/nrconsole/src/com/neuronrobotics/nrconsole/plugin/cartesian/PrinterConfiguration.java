package com.neuronrobotics.nrconsole.plugin.cartesian;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.StateBasedControllerConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;

public class PrinterConfiguration extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1890177802795201269L;
	private SampleGuiNR gui = new SampleGuiNR();
	private StateBasedControllerConfiguration state;
	private JTextField kp = new JTextField(10);
	private JTextField ki = new JTextField(10);
	private JTextField kd = new JTextField(10);
	private JTextField vkp = new JTextField(10);
	private JTextField vkd = new JTextField(10);
	private JTextField mmPos = new JTextField(10);
	private JTextField maxVel = new JTextField(10);
	private JTextField baseRad = new JTextField(10);
	private JTextField EErad = new JTextField(10);
	private JTextField maxz = new JTextField(10);
	private JTextField minz = new JTextField(10);
	private JTextField rodlen = new JTextField(10);
	private JButton update=new JButton("Update");
	private NRPrinter printer;
	
	public PrinterConfiguration(){
		setLayout(new MigLayout());
		add(new JLabel("kP"));add(kp,"wrap");
		add(new JLabel("kI"));add(ki,"wrap");
		add(new JLabel("kD"));add(kd,"wrap");
		add(new JLabel("VkP"));add(vkp,"wrap");
		add(new JLabel("VkD"));add(vkd,"wrap");
		add(new JLabel("Resolution (mm)"));add(mmPos,"wrap");
		add(new JLabel("Maximum Velocity (mm/s)"));add(maxVel,"wrap");
		add(new JLabel("Base Radius(mm)"));add(baseRad,"wrap");
		add(new JLabel("End Effector Radius(mm)"));add(EErad,"wrap");
		add(new JLabel("Maximum Z(mm)"));add(maxz,"wrap");
		add(new JLabel("Minimum Z(mm)"));add(minz,"wrap");
		add(new JLabel("Rod Length(mm)"));add(rodlen,"wrap");
		add(update,"wrap");
		add(gui,"wrap");
		
		update.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				state.setkP(Double.parseDouble(kp.getText()));
				state.setkI(Double.parseDouble(ki.getText()));
				state.setkD(Double.parseDouble(kd.getText()));
				state.setvKP(Double.parseDouble(vkp.getText()));
				state.setvKD(Double.parseDouble(vkd.getText()));
				state.setMmPositionResolution(Double.parseDouble(mmPos.getText()));
				state.setMaximumMMperSec(Double.parseDouble(maxVel.getText()));
				state.setBaseRadius(Double.parseDouble(baseRad.getText()));
				state.setEndEffectorRadius(Double.parseDouble(EErad.getText()));
				state.setMaxZ(Double.parseDouble(maxz.getText()));
				state.setMinZ(Double.parseDouble(minz.getText()));
				state.setRodLength(Double.parseDouble(rodlen.getText()));
				printer.setStateBasedControllerConfiguration(state);
			}
		});
	}
	
	public void setKinematicsModel(NRPrinter printer) {
		this.printer = printer;
		gui.setKinematicsModel(printer);
		state = printer.getStateBasedControllerConfiguration();
		kp.setText(new Double(state.getkP()).toString());
		ki.setText(new Double(state.getkI()).toString());
		kd.setText(new Double(state.getkD()).toString());
		vkp.setText(new Double(state.getvKP()).toString());
		vkd.setText(new Double(state.getvKP()).toString());
		mmPos.setText(new Double(state.getMmPositionResolution()).toString());
		maxVel.setText(new Double(state.getMaximumMMperSec()).toString());
		baseRad.setText(new Double(state.getBaseRadius()).toString());
		EErad.setText(new Double(state.getEndEffectorRadius()).toString());
		maxz.setText(new Double(state.getMaxZ()).toString());
		minz.setText(new Double(state.getMinZ()).toString());
		rodlen.setText(new Double(state.getRodLength()).toString());
		
	}
}
