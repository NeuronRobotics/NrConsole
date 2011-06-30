package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class PIDVelocityWidget extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5325751144652371482L;
	PIDControlWidget widget;
	private JTextField vel=new JTextField();
	private JTextField time=new JTextField();
	JButton go = new JButton("Run Velocity");
	
	public PIDVelocityWidget(PIDControlWidget w){
		widget=w;
		setLayout(new MigLayout());
		go.addActionListener(this);
		vel.addActionListener(this);
		time.addActionListener(this);
		vel.setText("100");
		time.setText("1.0");
		add(new JLabel("Velocity Control"),"wrap");
		add(vel);add(new JLabel("ticks per second"),"wrap");
		add(time);add(new JLabel("seconds"),"wrap");
		add(go,"wrap");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.err.println("Go button pressed");
		try{
			double t = Double.parseDouble(time.getText());
			int v = Integer.parseInt(vel.getText());
			widget.SetPIDVel(v, t);
		}catch (Exception ex){
			vel.setText("100");
			time.setText("1.0");
			ex.printStackTrace();
		}
	}
}
