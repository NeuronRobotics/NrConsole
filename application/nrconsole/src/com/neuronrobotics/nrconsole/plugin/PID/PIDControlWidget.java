package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class PIDControlWidget extends JPanel implements IPIDEventListener,ActionListener {
	private static final long serialVersionUID = 3L;

	private JTextField kp=new JTextField(10);
	private JTextField ki=new JTextField(10);
	private JTextField kd=new JTextField(10);
	private JCheckBox  inverted =new JCheckBox("Invert control");
	JButton  pidSet = new JButton("Configure");
	JButton  pidStop = new JButton("Stop");
	private JTextField setpoint=new JTextField(new Double(0).toString(),5);
	private JButton  setSetpoint = new JButton("Set Setpoint");
	private JButton  zero = new JButton("Zero PID");
	private JLabel   currentPos = new JLabel("0");
	private AdvancedPIDWidget advanced =null;
	
	private JPanel pidRunning = new JPanel(new MigLayout());
	
	private PIDGraph graph;
	
	private boolean set = false;
	
	private PIDControlGui tab;

	private int group;
	private PIDConfiguration pidconfig; 
	private int setpointValue;
	private int positionValue;
	public PIDControlWidget(int group, int startValue, PIDControlGui tab,PIDConfiguration conf) {
		setBorder(BorderFactory.createRaisedBevelBorder());
		tab.getPidDevice().addPIDEventListener(this);
		currentPos.setText(new Integer(startValue).toString());
		setpointValue=startValue;
		setPositionValue(startValue);
		setLayout(new MigLayout());
		setGui(tab);
		setGroup(group);
		pidconfig=conf;
	    inverted.setSelected(true);
	    
		pidSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				double p=0,i=0,d=0;
				try{
					p=Double.parseDouble(kp.getText());
				}catch(Exception e){
					kp.setText(new Double(1).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					i=Double.parseDouble(ki.getText());
				}catch(Exception e){
					ki.setText(new Double(0).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					d=Double.parseDouble(kd.getText());
				}catch(Exception e){
					kd.setText(new Double(0).toString());
					JOptionPane.showMessageDialog(null, "Bad PID values, resetting.", "PID Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				setPID(p, i, d);
				int cur = GetPIDPosition();
				setSetpoint(cur);
				setPositionValue(cur);
				pidRunning.setVisible(true);
			}
		});

		pidStop.setEnabled(false);
		pidStop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				stopPID();
			}
		});
		zero.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				ResetPIDChannel();
				int val = GetPIDPosition();
				setSetpoint(val);
				currentPos.setText(new Integer(val).toString());
			}
		});
		
		setpoint.setText(new Integer(startValue).toString());
		setpoint.addActionListener(this);
		setSetpoint.addActionListener(this);
		
		
		populatePID();
	    
		JPanel constants = new JPanel(new MigLayout());
		constants.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    constants.setMinimumSize(new Dimension(300, 50));
	    constants.add(new JLabel("PID Gain Constants"),"wrap");
		constants.add(new JLabel("proportional (Kp)"));
	    constants.add(kp,"wrap");
	    constants.add(new JLabel("integral (Ki)"));
	    constants.add(ki,"wrap");
	    constants.add(new JLabel("derivitive (Kd)"));
	    constants.add(kd,"wrap");
	    constants.add(pidSet);
	    constants.add(inverted);
	    
	    pidRunning.add(new JLabel("PID Running for group "+((int)getGroup())),"wrap");
	    pidRunning.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	    //pidRunning.add(pidSet);
	    pidRunning.add(pidStop,"wrap");
	    //pidRunning.add(inverted);
	    pidRunning.add(zero,"wrap");
	    pidRunning.add(setSetpoint);
	    pidRunning.add(setpoint,"wrap");
	    pidRunning.add(new JLabel("Current Position = "));
	    pidRunning.add(currentPos,"wrap");
	    
	    pidRunning.add(advanced,"wrap");
	    pidRunning.add(new PIDVelocityWidget(this));
	    
	    
	    JPanel uiPanel = new JPanel(new MigLayout());
	    if(getGui().isDyPID()) {
	    	uiPanel.add(new DyPIDControlWidget(this),"wrap");		
		}
	    uiPanel.add(constants,"wrap");
	    
	    JPanel config = new JPanel();
	    config.add(uiPanel);
	    config.add(pidRunning);
		
		
		graph = new PIDGraph(group);
		
		add(config,"wrap");
		add(graph,"wrap");
		
		repaint();
		Updater up = new Updater();
		up.start();
		pidRunning.setVisible(false);
	}
	
	private void populatePID() {
		advanced = new  AdvancedPIDWidget(this);
	    advanced.setEnabled(false);
		PIDConfiguration conf = getPIDConfiguration();
		kp.setText(new Double(conf.getKP()).toString());
		ki.setText(new Double(conf.getKI()).toString());
		kd.setText(new Double(conf.getKD()).toString());
		inverted.setSelected(conf.isInverted());
		if(conf.isEnabled()){
			pidStop.setEnabled(true);
			advanced.setEnabled(true);
		}
	}
	

	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}
	public void setGui(PIDControlGui tab) {
		this.tab = tab;
	}
	public PIDControlGui getGui() {
		return tab;
	}
	void stopPID(){
		pidStop.setEnabled(false);
		pidconfig.setEnabled(false);
		ConfigurePIDController(pidconfig);
		advanced.setEnabled(false);
		pidRunning.setVisible(false);
	}
	private void setPID(double p,double i,double d){
		setSet(true);
		pidStop.setEnabled(true);
		pidconfig.setEnabled(true);
		pidconfig.setInverted(inverted.isSelected());
		pidconfig.setAsync(true);
		pidconfig.setKP(p);
		pidconfig.setKI(i);
		pidconfig.setKD(d);
		ConfigurePIDController(pidconfig);
		advanced.setEnabled(true);
	}
	public void setSet(boolean set) {
		this.set = set;
	}
	public boolean isReady() {
		return set;
	}
	
	
	public String toString() {
		return "GROUP # "+(int)getGroup();
	}

	
	public void onPIDEvent(PIDEvent e) {
		if(e.getGroup()==getGroup()){
			//System.out.println("From PID control widget: "+e);
			
			setPositionValue(e.getValue());
			
		}
	}
	public void setSetpoint(int setPoint){
		SetPIDSetPoint(setPoint,0);
		setpointValue=setPoint;
		setpoint.setText(new Integer(setPoint).toString());
		graphVals();
		pidStop.setEnabled(true);
	}
	private class Updater extends Thread{
		long lastSet;
		long lastPos;
		public void run() {
			while(true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				if(lastSet != setpointValue || lastPos !=getPositionValue() ) {
					graphVals();
					lastSet = setpointValue ;
					lastPos = getPositionValue();
				}
			}
		}
	}
	private void graphVals() {
		if(graph!=null)
			graph.addEvent(setpointValue,getPositionValue());
	}

	public void setPositionValue(int positionValue) {
		currentPos.setText(new Integer(positionValue).toString());
		this.positionValue = positionValue;
		graphVals();
	}
	public int getSetPoint() {
		return Integer.parseInt(setpoint.getText());
	}
	public int getPositionValue() {
		return positionValue;
	}
	
	private void ResetPIDChannel(){
		try{
			getGui().getPidDevice().ResetPIDChannel(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint reset failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	private int GetPIDPosition(){
		try{
			return getGui().getPidDevice().GetPIDPosition(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint get failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return 0;
		}
	}
	private PIDConfiguration getPIDConfiguration(){
		try{
			return getGui().getPidDevice().getPIDConfiguration(getGroup());
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Configuration get failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return new PIDConfiguration();
		}
	}
	private void ConfigurePIDController(PIDConfiguration pidconfig){
		try{
			getGui().getPidDevice().ConfigurePIDController(pidconfig);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Configuration Set failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	private void SetPIDSetPoint(int setPoint,int velocity){
		try{
			getGui().getPidDevice().SetPIDSetPoint(getGroup(), setPoint,velocity);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Setpoint set failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
			return;
		}
	}
	
	public void SetPIDVel(int velocity,double seconds){
		try{
			System.err.println("Running Velocity Update: "+velocity+" ticks/sec  "+seconds+" seconds ");
			Log.enableDebugPrint(true);
			getGui().getPidDevice().SetPIDInterpolatedVelocity(getGroup(),velocity,seconds);
			Log.enableDebugPrint(false);
		}catch(Exception e){
			new Thread(){
				public void run(){
					JOptionPane.showMessageDialog(null, "Velocity set failed on group #"+getGroup(), "pid ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}.start();
			
            e.printStackTrace();
			return;
		}
	}
	
	public void onPIDReset(int group, int currentValue) {
		// TODO Auto-generated method stub
		
	}

	
	public void onPIDLimitEvent(PIDLimitEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			setSetpoint(getSetPoint());
		}catch(Exception e){
			setpoint.setText(new Integer(0).toString());
			return;
		}
	}
}
