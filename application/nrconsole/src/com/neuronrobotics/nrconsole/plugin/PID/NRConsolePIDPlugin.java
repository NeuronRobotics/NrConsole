package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

public class NRConsolePIDPlugin extends AbstractNRConsoleTabedPanelPlugin {
	private boolean active = false;
	private boolean dypid = false;
	//private DyIO dyio;
	private GenericPIDDevice pid;
	private PIDControlGui gui;
	private BowlerAbstractConnection connection = null;
	//private JButton display = new JButton("Display PID configuration");
	//private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JPanel holder ;
	private static final String [] namespaces = {"bcs.pid.*"};
	public NRConsolePIDPlugin(PluginManager pm){
		super(namespaces,pm);
		getTabPane();
	}
	
	public JPanel getTabPane() {
		if(holder== null){
			holder = new JPanel(new MigLayout());
			holder.setName("P.I.D. Configuration");
		}
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
		holder.add(gui,"wrap");
		holder.invalidate();
		return true;
	}
	
	public ArrayList<JMenu> getMenueItems() {
		return null;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		return new Dimension(1095,1300);
	}

	public void startVirtual() {
		pid = new VirtualGenericPIDDevice(10000);
		isMyNamespace(pid.getNamespaces());
		gui = new PIDControlGui(pid);
		holder.add(gui,"wrap");
		holder.invalidate();
	}

	public GenericPIDDevice getPidDevice() {
		return pid;
	}

}
