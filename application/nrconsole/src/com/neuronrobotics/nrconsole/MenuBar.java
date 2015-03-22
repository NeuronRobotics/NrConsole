package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.lwjgl.openal.AL;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.GettingStartedPanel;
import com.neuronrobotics.nrconsole.util.NRConsoleDocumentationFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class MenuBar extends JMenuBar implements IConnectionEventListener {
	private static final long serialVersionUID = 1L;
	private PluginManager manager;//Manages the display/update of plugins
	private boolean ready=false;//Is changed to true when the menus are initialized/set up
	
	private MenuBar self = this;//used for NRConsoleDocumentationFactory.getDocumentationURL input
	//JMenus and their associated JMenuItems
	
	//The "File" menu
	JMenu fileMenu;
	private JMenuItem quitMenuItem = new JMenuItem("Quit");
	
	//The "About" menu
	JMenu aboutMenu;
	private JMenuItem aboutMenuItem = new JMenuItem("About NRConsole");
	
	//The "Connection" menu
	JMenu connectionMenu;
	private JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
	private JMenuItem setConnectionMenuItem = new JMenuItem("Set Connection");
	private JMenuItem virtualPidMenuItem = new JMenuItem("Virtual PID");
	
	//The "About" frame and panel
	private JFrame aboutFrame;
	private JPanel aboutPanel; 
	
	/**
	 * Constructor for the menu bar
	 * @param console
	 */
	public MenuBar(PluginManager console) {
			
		this.manager = console;//Add the PluginManager
		
		aboutPanel = new JPanel(new MigLayout()); //Create the about panel
		
		initJMenuItems();//Initialize the JMenuItems
		
		//Create the menus and add their associated items
		
		fileMenu = new JMenu("File");//Create the "File" menu
		fileMenu.add(quitMenuItem);//Add the "Quit" menu item
		
		
		connectionMenu= new JMenu("Connection");//Create the "connection"  menu
		connectionMenu.add(setConnectionMenuItem);//add the "Set Connection" menu item
		connectionMenu.add(disconnectMenuItem);//add the "Disconnect" menu item
		connectionMenu.add(virtualPidMenuItem);//add the "Virtual PID" menu item
		
		aboutMenu = new JMenu("About");//Create the "About" menu
		aboutMenu.add(aboutMenuItem);//Add the "About NRConsole" menu item
		
		//Add the finished menus to the menu bar
	    add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	}
	
	/**
	 * This function removes all menus from the menu bar,
	 * and then adds the standard menus (file, connection, about)
	 * and any jmenus passed in as an array list
	 * 
	 * @param menues an array list of JMenu containing all JMenus to be added
	 * to the menu bar besides the original set (file, connection, about)
	 */
	public void setMenues(ArrayList<JMenu> menues){
		removeAll();
		add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	    
	    if(menues != null) {
		    for(JMenu m:menues){
		    	if (m != null)
		    		add(m);
		    }
	    }
	}

	
	/**
	 * Set up the JMenuItems
	 * 		"quitMenuItem","aboutMenuItem","disconnectMenuItem",
	 *  	"virtualPidMenuItem", "connectionMenuItem",
	 * 		 "kinematicsLabMenuItem"
	 *  ...by creating and adding the appropriate action listeners and setting mnemonics
	 *  (note that kinematicsLabMenuItem, virtualPidMenuItem, and aboutMenuItem do NOT have mnemonics).
	 *  
	 *  Also disables the disconnectMenuItem by default.
	 *  
	 *  Requires:
	 *  	-> that the "aboutPanel" JPanel be instantiated.
	 *  	-> the PluginManager "manager" be instantiated/set.
	 *  
	 */
	private void initJMenuItems() {
		
		//Set up quitMenuItem
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);//Set the mnemonic to the "q" key (in standard US keyboard layout)
		quitMenuItem.addActionListener(new ActionListener() { 
			
			public void actionPerformed(ActionEvent e) {
				disconnect();
				AL.destroy();//Used to clean up any audio streams before exit. Will throw an error if not used ("AL lib: (EE) alc_cleanup: 1 device not closed")
				System.exit(0);
			}
		});
		
		//Set up aboutMenuItem//clear any partial connection
		aboutMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//Try to open the specified URL 
				//(using the Desktop variable of the GettingStartedPanel)
				//If it fails, will generate, pack and show the aboutFrame containing the aboutPanel.
				//The aboutPanel contains a jlabel displaying the current console version number
				try {
					GettingStartedPanel.openPage(NRConsoleDocumentationFactory.getDocumentationURL(self));
				} catch (Exception e1) {
				    aboutPanel.add(new JLabel(NRConsoleWindow.getConsoleVersion()),"wrap");//Set up the about panel
					aboutFrame = new JFrame(aboutPanel.getName());
					aboutFrame.add(aboutPanel);
					aboutFrame.setLocationRelativeTo(null); 
					aboutFrame.pack();
					aboutFrame.setVisible(true);
				}
			}
		});
		
		//Set up disconnectMenuItem
		disconnectMenuItem.setMnemonic(KeyEvent.VK_D);//Set the mnemonic to the "d" key (in standard US keyboard layout)
		disconnectMenuItem.setEnabled(false);//Set the disconnectMenuItem to be disabled (grayed out) by default
		disconnectMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();	
			}
		});
		
		//Set up virtualPidMenuItem
		virtualPidMenuItem.addActionListener(new ActionListener() {
			
			//On activation, will use the PluginManager "manager" to "connect" to a virtual PID device.
			//Will also signal that a plugin has been updated and set the "ready" variable to true
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				manager.connectVirtualPID();
				manager.firePluginUpdate();
				ready = true;
			}
		});
		
		//Set up connectionMenuItem
		setConnectionMenuItem.setMnemonic(KeyEvent.VK_C);//Set the mnemonic to the "c" key (in standard US keyboard layout)
		setConnectionMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		//All JMenuItems set up. We are done.
	}
	
	/**
	 * This is called when the application first starts up,
	 * or the "connect" menu item is selected
	 */
	void connect(){
		disconnect();//clear any active connection
		try {
			connectionMenu.setEnabled(false);//disable the connection menu to avoid two connections being set up at the same time
			if(manager.connect(this)) {//Use the PluginManager "manager" to connect the connection to the plugins (returns true upon success). This takes a while.
				ready = true;//Set ready to true to indicate a successful connection protocol has completed (device is connected, all plugins are successfully connected to device)

			}else {
				disconnect();//clear any partial connection
				onDisconnect(manager.getConnection());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			disconnect();//clear any partial connection
			onDisconnect(manager.getConnection());
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to connect to device, "+ex.getMessage(), "Bowler ERROR", JOptionPane.ERROR_MESSAGE);
		}//clear any partial connection
		
		connectionMenu.setEnabled(true);//Re-enable connection menu to allow access to menu items
		manager.firePluginUpdate();
	}
	
	/**
	 * This function is used to disconnect from the device.
	 * It will use the PluginManager "manager" to call setActive(false) for all plugins
	 * and then disconnect from the device.
	 */
	public void disconnect() {
		ready = false;//Set ready to true to indicate a successful disconnect
		try {
			manager.disconnect();
		}catch(Exception ex) {
			System.err.println("NRCONSOLE disconnection error print:");
			ex.printStackTrace();
		}
		ThreadUtil.wait(75);
		manager.firePluginUpdate();
	}
	
	/**
	 * Used to access the "ready" boolean from outside this class
	 * @return ready A boolean indicating if a device is successfully connected 
	 * (pluginManager.connect has completed)
	 */
	public boolean isReady(){
		return ready ;
	}
	
	/**
	 * This is called by the stack whenever a device is disconnected
	 * (device was physically removed, or another thread called device.disconnect)
	 */
	@Override
	public void onDisconnect(BowlerAbstractConnection source) {
		disconnectMenuItem.setEnabled(false);
		connectionMenu.setEnabled(true);
		setMenues(null);//Remove all menus beside the default set (file, connection, about)
		ready = false;	
	}
	
	/**
	 * This is called by the stack whenever device.connect is called by another thread
	 */
	@Override
	public void onConnect(BowlerAbstractConnection source) {
		disconnectMenuItem.setEnabled(true);
		connectionMenu.setEnabled(true);
	}
}
