package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lwjgl.openal.AL;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;
import com.neuronrobotics.sdk.util.ThreadUtil;
@SuppressWarnings("unused")

/**
 * 
 * @author technocopia05
 *
 */
public class NRConsole {
	
	private NRConsoleWindow nrcFrame = null;

	private PluginManager manager=new PluginManager();//This is the class that manages the plugins and their associated tabs in the gui
	private MenuBar nrcMenubar = new MenuBar(manager);//This is the menu bar for the main gui window
	private showManager shower = new showManager ();//This is some kludgey bullshit to manage the paint and update of the gui. //TODO take this out
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
			if(args.length != 0)
				new NRConsole(true);
			else
				new NRConsole(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public NRConsole(boolean debug) {
		self = this;
		nrcFrame = new NRConsoleWindow();
		nrcFrame.setJMenuBar(nrcMenubar);
		nrcMenubar.setMenues(null);
		
		nrcFrame.setLocationRelativeTo(null);
		nrcFrame.setVisible(true);
		nrcFrame.setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage()); 
		
		shower.start();
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
	}
	
	
	/**
	 * This private class is some kludgey bullshit
	 * TODO: take this out
	 * @author technocopia05
	 *
	 */
	private class showManager extends Thread implements IPluginUpdateListener{
		public void run(){
			try{
				nrcMenubar.connect();
			}catch (Exception ex){
				ex.printStackTrace();
			}
			while(true){	
				if(nrcMenubar.isReady()){
					
					onPluginListUpdate(manager);
					manager.addIPluginUpdateListener(this);
					 
					while(nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
					
				}else{
					manager.removeIPluginUpdateListener(this);
					nrcMenubar.setMenues(null);
					nrcFrame.displayLogo(manager);
					nrcFrame.invalidate();
					nrcFrame.setVisible(true);
					
					while(!nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
					
				}
			}
		}

		@Override
		public void onPluginListUpdate(PluginManager manager) {
			//System.out.println("NRConsole is refreshing");
			if(nrcMenubar.isReady()){
				nrcMenubar.setMenues(manager.getMenueItems());
				nrcFrame.setDeviceManager(manager);
				nrcFrame.invalidate();
				nrcFrame.setVisible(true);
			}
		}
	}
}
