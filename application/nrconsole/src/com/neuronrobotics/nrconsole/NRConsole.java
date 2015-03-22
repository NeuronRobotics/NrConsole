package com.neuronrobotics.nrconsole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.SwingUtilities;

import org.lwjgl.openal.AL;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.bootloader.core.Hexml;
import com.neuronrobotics.nrconsole.plugin.bootloader.core.NRBoot;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;
import com.neuronrobotics.sdk.util.ThreadUtil;
@SuppressWarnings("unused")

/**
 * 
 * @author technocopia05
 *
 */
public class NRConsole {
	
	private NRConsoleWindow nrcFrame =  new NRConsoleWindow();

	private PluginManager manager=new PluginManager(nrcFrame);//This is the class that manages the plugins and their associated tabs in the gui
	private MenuBar nrcMenubar = new MenuBar(manager);//This is the menu bar for the main gui window
	private static NRConsole self; //Used to reference this class within this class to get around the "static" qualifier. //TODO take this out
	
	/**
	 * Main function for GUI.
	 * Creates a new GUI with the NRConsole constructor,
	 * giving it a boolean that indicates if any command
	 * line arguments are present (false if any command line
	 * arguments are present).
	 * @param args
	 */
	public static void main(String [] args) {
		try {
			if(args.length != 0){
				if(args.length ==1)
					new NRConsole(true);
				else{
					Integer xmlIndex=null;
					Integer portIndex=null;
					for(int i=0;i<args.length;i++){
						if(args[i].contains("xml")){
							xmlIndex=i;
						}
						if(args[i].contains("port")){
							portIndex=i;
						}
					}
					if (xmlIndex!=null && portIndex!=null){
						System.out.println("Running "+args[portIndex]+" with "+args[xmlIndex]);
						SerialConnection con;
						NRBoot blApp;
						try{
							con = new SerialConnection(args[portIndex].split("=")[1]);
							con.ping(new MACAddress());
							blApp = new NRBoot(con);
						}catch (Exception e){
							con = (SerialConnection) ConnectionDialog.promptConnection();
							con.ping(new MACAddress());
							blApp = new NRBoot(con);
						}
						
						
						Hexml hex = new Hexml(new File(args[xmlIndex].split("=")[1]));
						blApp.loadCores(hex.getCores());
						
						while(blApp.isLoadDone() == false) {
							try {Thread.sleep(1000);} catch (InterruptedException e) {}
							System.out.println("Progress: "+blApp.getProgressValue());
						}
						System.exit(0);
					}
					System.err.println("Unknown "+args);
					System.exit(1);
				}
			}else{
				new NRConsole(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public NRConsole(boolean debug) {
		self = this;

		nrcFrame.setJMenuBar(nrcMenubar);
		nrcMenubar.setMenues(null);
		nrcFrame.displayLogo(manager);
		nrcFrame.setLocationRelativeTo(null);
		nrcFrame.setVisible(true);
		nrcFrame.setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage()); 

		if(debug){
			Log.enableDebugPrint();
		}
		

		nrcFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent winEvt) {
				manager.disconnect();
				try{
					AL.destroy();//Used to clean up any audio streams before exit. Will throw an error if not used ("AL lib: (EE) alc_cleanup: 1 device not closed")
				}catch(Error e){
					// if no audio loaded, still exit clean
				}
				System.out.println("Exit clean");
		        System.exit(0); 
		    }
		});
		
		manager.addIPluginUpdateListener(new IPluginUpdateListener() {
			
			@Override
			public void onPluginListUpdate(PluginManager m) {
				if(manager != m)
					return;
				System.out.println("NRConsole is refreshing");
				if(nrcMenubar.isReady()){
					System.out.println("Connection ready");
					SwingUtilities.invokeLater(() -> {
						nrcMenubar.setMenues(manager.getMenueItems());
						nrcFrame.setDeviceManager(manager);
						nrcFrame.invalidate();
						nrcFrame.setVisible(true);
					});

				}else{
					System.out.println("No connection");
					SwingUtilities.invokeLater(() -> {
						nrcMenubar.setMenues(null);
						nrcFrame.displayLogo(manager);
						nrcFrame.invalidate();
						nrcFrame.setVisible(true);
					});
				}
			}
		});
		
		nrcMenubar.connect();
		
	}

}
