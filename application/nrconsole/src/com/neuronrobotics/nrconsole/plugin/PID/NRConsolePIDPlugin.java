package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.NRConsoleWindow;
import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.INRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;

public class NRConsolePIDPlugin extends AbstractNRConsoleTabedPanelPlugin {
	private boolean active = false;
	private boolean dypid = false;
	//private DyIO dyio;
	private GenericPIDDevice pid;
	private PIDControlGui gui;
	private JPanel panel = new JPanel(new MigLayout());
	private BowlerAbstractConnection connection = null;
	//private JButton display = new JButton("Display PID configuration");
	//private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JPanel holder ;
	private static final String [] namespaces = {"bcs.pid.*"};
	public NRConsolePIDPlugin(PluginManager pm){
		super(namespaces,pm);
//		panel.add(display,"wrap");
//		display.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				display.setVisible(false);
//				if(!dypid){
//					pid = new  GenericPIDDevice(connection);
//					pid.connect();
//					gui = new PIDControlGui(pid);
//				}else{
//					if(connection != null)
//						DyIORegestry.setConnection(connection);
//					gui = new PIDControlGui();
//				}
//				panel.add(gui,"wrap");
//				panel.invalidate();
//			}
//		});
		
		
		//panel.setPreferredSize(new Dimension(NRConsoleWindow.panelWidth-100,NRConsoleWindow.panelHight-100));
		//panel.setSize(new Dimension(NRConsoleWindow.panelWidth-100,NRConsoleWindow.panelHight-100));
//		scrollPanel.setViewportView(panel);
//		scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		
		getTabPane().setName("P.I.D. Configuration");
		getTabPane().add(panel);
	}
	
	public JPanel getTabPane() {
		if(holder== null)
			holder = new JPanel();
		return holder;
	}
	private boolean setNameSpaces = false;
	@Override
	public boolean isMyNamespace(ArrayList<String> names) {
		if(super.isMyNamespace(names)) {
			for(String s:names){
				if(s.contains("neuronrobotics.dyio.*")){
					dypid = true;
				}
			}
			setNameSpaces = true;
		}
		return isAcvive();
	}

	
	public boolean setConnection(BowlerAbstractConnection conn) {
		if(!setNameSpaces)
			throw new RuntimeException("Namespaces not set before connection");
		this.connection = conn;
		if(!dypid){
			pid = new  GenericPIDDevice(connection);
			pid.connect();
			gui = new PIDControlGui(pid);
		}else{
			if(connection != null)
				DyIORegestry.setConnection(connection);
			gui = new PIDControlGui();
		}
		panel.add(gui,"wrap");
		panel.invalidate();
		return true;
	}
	
	public ArrayList<JMenu> getMenueItems() {
		return null;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		return new Dimension(1095,1000);
	}

}
