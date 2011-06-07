package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenu;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.INRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;

public class NRConsolePIDPlugin implements INRConsoleTabedPanelPlugin {
	private boolean active = false;
	private boolean dypid = false;
	//private DyIO dyio;
	private GenericPIDDevice pid;
	private PIDControlGui gui;
	private JPanel panel = new JPanel(new MigLayout());
	private BowlerAbstractConnection connection = null;
	private JButton display = new JButton("Display PID configuration");
	public NRConsolePIDPlugin(){
		PluginManager.addNRConsoleTabedPanelPlugin(this);
		panel.add(display,"wrap");
		panel.setName("P.I.D. Configuration");
		display.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.setVisible(false);
				if(!dypid){
					pid = new  GenericPIDDevice(connection);
					pid.connect();
					gui = new PIDControlGui(pid);
				}else{
					DyIORegestry.setConnection(connection);
					gui = new PIDControlGui();
				}
				panel.add(gui,"wrap");
				panel.invalidate();
			}
		});
	}
	
	public JPanel getTabPane() {
		
		return panel;
	}


	
	public boolean isMyNamespace(ArrayList<String> names) {
		for(String s:names){
			if(s.contains("neuronrobotics.dyio.*")){
				dypid = true;
			}
			if(s.contains("bcs.pid.*")){
				active= true;
			}
		}
		return isAcvive();
	}

	
	public boolean setConnection(BowlerAbstractConnection conn) {
		this.connection = conn;

		
		return true;
	}

	
	public boolean isAcvive() {
//		if(active)
//			System.out.println(this.getClass()+" is active");
		return active;
	}

	
	public ArrayList<JMenu> getMenueItems() {
		// TODO Auto-generated method stub
		return null;
	}

}
