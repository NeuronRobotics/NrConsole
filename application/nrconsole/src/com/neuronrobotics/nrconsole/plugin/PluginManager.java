package com.neuronrobotics.nrconsole.plugin;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.BowlerCam.NRConsoleBowlerCameraPlugin;
import com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay.NRConsoleBowlerRPCDisplayPlugin;
import com.neuronrobotics.nrconsole.plugin.DeviceConfig.NRConsoleDeviceConfigPlugin;
import com.neuronrobotics.nrconsole.plugin.DyIO.NRConsoleDyIOPlugin;
import com.neuronrobotics.nrconsole.plugin.JobExec.NRConsoleJobExecPlugin;
import com.neuronrobotics.nrconsole.plugin.PID.NRConsolePIDPlugin;
import com.neuronrobotics.nrconsole.plugin.bootloader.NRConsoleBootloaderPlugin;
import com.neuronrobotics.nrconsole.plugin.cartesian.CartesianController;
import com.neuronrobotics.nrconsole.plugin.scripting.NRConsoleScriptingPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class PluginManager {
	private ArrayList<INRConsoleTabedPanelPlugin> plugins = new ArrayList<INRConsoleTabedPanelPlugin>();
	private GenericDevice gen;
	private BowlerAbstractConnection connection;
	private ArrayList<String >names=null;
	private int width=1095;
	private int height=700;
	private boolean virtual = false;
	private JFrame frame;
	public PluginManager(JFrame frame){
		this.setFrame(frame);
		disconnect();

	}
	
	public void removeNRConsoleTabedPanelPlugin(INRConsoleTabedPanelPlugin p){
		removeNRConsoleTabedPanelPlugin(p.getClass().toString());
	}
	
	public void removeNRConsoleTabedPanelPlugin(String p){
		
		for(int i=0;i<plugins.size();i++){
			INRConsoleTabedPanelPlugin pl= plugins.get(i);
			if(pl.getClass().toString().contains(p)) {
				//System.err.println("removing: "+p);
				plugins.remove(pl);
			}
		}
	}
	
	/**
	 * This static method is for plugins to add themselves to the list of tabed-paned plugins
	 * @param p a plugin instance to add to the list
	 */
	public void addNRConsoleTabedPanelPlugin(INRConsoleTabedPanelPlugin p){
		if (!plugins.contains(p)){
			for(INRConsoleTabedPanelPlugin pl:plugins){
				if(pl.getClass().toString().contains(p.getClass().toString())) {
					//System.err.println("in list, not adding: "+p.getClass());
					return;
				}
			}
			Dimension d= p.getMinimumWimdowDimentions();
			if(d!=null){
				if(d.getWidth()>getMinimumWidth())
					setMinimumWidth((int) d.getWidth());
				if(d.getHeight()>getMinimumHeight())
					setMinimumHeight((int) d.getHeight());
				//p.getTabPane().setSize(d);
			}
			//System.out.println("Adding: "+p.getClass());
			plugins.add(p);
			updateNamespaces();
			firePluginUpdate();
			//System.out.println("Adding "+p.getClass());
		}
	}
	public boolean disconnect(){
		for(INRConsoleTabedPanelPlugin pl:plugins){
			pl.setActive(false);
		}

		if(connection != null) {
			connection.disconnect();
		}

		return true;
	}
	public void updateNamespaces(){
		for (int i=0;i<plugins.size();i++){
			INRConsoleTabedPanelPlugin p = plugins.get(i);
			p.setActive(false);
			if(getNameSpaces()!=null)
				p.isMyNamespace(getNameSpaces());
		}
	}
	public boolean connect(IConnectionEventListener listener) throws Exception{
		disconnect();
		
		plugins = new ArrayList<INRConsoleTabedPanelPlugin>();
		// HACK this should load using OSGI
		// Once instantiated they add themselves to the static list of plugins
		new NRConsoleJobExecPlugin(this);
		new NRConsoleDeviceConfigPlugin(this);
		new CartesianController(this);
		
		new NRConsoleDyIOPlugin(this);
		new NRConsoleScriptingPlugin(this);
		
		new NRConsolePIDPlugin(this);
		new NRConsoleBowlerCameraPlugin(this);
		new NRConsoleBootloaderPlugin(this);
		new NRConsoleBowlerRPCDisplayPlugin(this);
		connection = ConnectionDialog.promptConnection();
		if(connection == null) {
			return false;
		}


		Log.error("Switching to v4 parser");
		BowlerDatagram.setUseBowlerV4(true);
		
		gen = new GenericDevice(connection);
		try{
			if(!gen.connect()) {
				throw new InvalidConnectionException("Connection is invalid");
			}
			if(!gen.ping(true)){
				throw new InvalidConnectionException("Communication failed");
			}
		} catch(Exception e) {
			//connection.disconnect();
			ThreadUtil.wait(1000);
			BowlerDatagram.setUseBowlerV4(false);
			if(!gen.connect()) {
				throw new InvalidConnectionException("Connection is invalid");
			}
			if(!gen.ping()){
				connection = null;
				throw new InvalidConnectionException("Communication failed");
			}
			throw e;
		}
		connection.addConnectionEventListener(listener);
		
		setNameSpaces(gen.getNamespaces());
		updateNamespaces();
		for (int i=0;i<plugins.size();i++){
			 INRConsoleTabedPanelPlugin p = plugins.get(i);
			 if(p.isAcvive()){
				 p.setConnection(connection);
			 }
		}
		return true;
	}
	public ArrayList<JMenu> getMenueItems(){
		ArrayList<JMenu> items = new ArrayList<JMenu>() ;
		for (INRConsoleTabedPanelPlugin plugs:plugins){
			if(plugs.isAcvive()){
				ArrayList<JMenu> m = plugs.getMenueItems();
				if(m != null){
					for (JMenu i: m){
						items.add(i);
					}
				}
			}
		}
		return items;
	}
	public ArrayList<JMenu> getMenueItems(JPanel panel){
		ArrayList<JMenu> items = new ArrayList<JMenu>() ;
		for (INRConsoleTabedPanelPlugin plugs:plugins){
			if(plugs.getTabPane() == panel && plugs.isAcvive()){
				ArrayList<JMenu> m = plugs.getMenueItems();
				if(m != null){
					for (JMenu i: m){
						i.setVisible(true);
						items.add(i);
					}
				}
			}
		}
		return items;
	}
	public ArrayList<JPanel> getPanels(){
		 ArrayList<JPanel> back =  new ArrayList<JPanel>();
		 updateNamespaces();
		 for (INRConsoleTabedPanelPlugin p:plugins){
			 if(p.isAcvive()){
				 //System.out.println("Displaying: "+p.getClass());
				 if(p.getTabPane()!=null){
					 p.getTabPane().setBorder(BorderFactory.createLoweredBevelBorder());
					 p.getTabPane().setSize(getMinimumDimention());
					 p.getTabPane().setVisible(true);
					 back.add(p.getTabPane());
				 }
			 }else {
				 //System.out.println("\t\tInactive: "+p.getClass());
			 }
		 }
		 //System.out.println("Displaying: "+back);
		 return back;	
	}
	private Dimension getMinimumDimention() {
		// TODO Auto-generated method stub
		return new Dimension(	getMinimumWidth(),
								getMinimumHeight()
							);
	}
	public boolean ping() {
		try {
			return gen.isAvailable();
		}catch(Exception e) {
			return false;
		}
	}

	private ArrayList<IPluginUpdateListener> puListeners = new ArrayList<IPluginUpdateListener>();
	public void addIPluginUpdateListener(IPluginUpdateListener l) {
		if(puListeners.contains(l))
			return;
		puListeners.add(l);
	}
	public void removeIPluginUpdateListener(IPluginUpdateListener l) {
		if(puListeners.contains(l))
			puListeners.remove(l);
	}
	public void firePluginUpdate(){
		updateNamespaces();
		//System.out.println(this.getClass()+"is refreshing");
		for(int i=0;i<puListeners.size();i++){
			puListeners.get(i).onPluginListUpdate(this);
		}
	}
	public boolean isConnected() {
		if(connection==null)
			return false;
		return connection.isConnected();
	}
	public void setNameSpaces(ArrayList<String > names) {
		this.names = names;
	}
	public ArrayList<String > getNameSpaces() {
		return names;
	}
	public void setMinimumWidth(int width) {
		this.width = width;
	}
	public int getMinimumWidth() {
		return width;
	}
	public void setMinimumHeight(int height) {
		this.height = height;
	}
	public int getMinimumHeight() {
		return height;
	}

	/**
	 * Find the PID plugin and start it
	 * Searched through the available plugins to find it
	 * Start the plugin using a virtual device 
	 */
	public void connectVirtualPID() {
		for(int i=0;i<plugins.size();i++){
			INRConsoleTabedPanelPlugin pl= plugins.get(i);
			if(pl.getClass().toString().contains("NRConsolePIDPlugin")) {
				NRConsolePIDPlugin pid = (NRConsolePIDPlugin)pl;
				pid.startVirtual();
				setNameSpaces(pid.getPidDevice().getNamespaces());
				updateNamespaces();
			}
		}
	}

	public BowlerAbstractConnection getConnection() {
		return connection;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}
