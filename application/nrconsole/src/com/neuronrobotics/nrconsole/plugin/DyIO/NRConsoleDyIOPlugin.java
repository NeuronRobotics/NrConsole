package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.graphing.ExcelWriter;
import com.neuronrobotics.graphing.GraphingOptionsDialog;
import com.neuronrobotics.graphing.GraphingWindow;
import com.neuronrobotics.nrconsole.plugin.INRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler.NRConsoleSchedulerPlugin;
import com.neuronrobotics.nrconsole.plugin.DyIO.hexapod.HexapodConfigPanel;
import com.neuronrobotics.nrconsole.plugin.DyIO.hexapod.HexapodNRConsolePulgin;
import com.neuronrobotics.nrconsole.plugin.PID.NRConsolePIDPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOPowerEvent;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.dyio.IDyIOEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEventListener;

public class NRConsoleDyIOPlugin implements INRConsoleTabedPanelPlugin,IChannelPanelListener,IDyIOEventListener , IConnectionEventListener  {
	private GraphingWindow graphingWindow = new GraphingWindow();
	private GraphingOptionsDialog graphingOptionsDialog = new GraphingOptionsDialog(graphingWindow);
	private ExportDataDialog graphingDialog = new ExportDataDialog(this);
	private JMenuItem showGraphMenuItem = new JMenuItem("Show Graph");
	private JMenuItem showHexapodConf = new JMenuItem("Show Hexapod Configuration");
	private JMenuItem showSequencerConf = new JMenuItem("Show Sequencer Configuration");
	private JMenuItem showPidConf = new JMenuItem("Show P.I.D. Configuration");
	private JMenuItem graphOptionsMenuItem = new JMenuItem("Graphing Options");
	private JMenuItem exportData = new JMenuItem("Export Data to File");
	private boolean active=false;
	private DyIOPanel devicePanel = new DyIOPanel();
	private DyIOControlsPanel deviceControls = new DyIOControlsPanel();
	private ArrayList<ChannelManager> channels = new ArrayList<ChannelManager>();
	//private HexapodConfigPanel hex=null;
	private JFrame hexFrame;
	private JPanel wrapper;
	private PluginManager manager;
	public NRConsoleDyIOPlugin(PluginManager pm) {
		manager = pm;
		manager.addNRConsoleTabedPanelPlugin(this);
		DyIORegestry.addConnectionEventListener(this);
		//hex = new HexapodNRConsolePulgin();
	}
	
	
	public JPanel getTabPane() {
		wrapper = new JPanel(new MigLayout()){
			/**
			 * 
			 */
			private static final long serialVersionUID = -5581797073561156394L;

			
			public void repaint(){
				super.repaint();
				getDeviceDisplay().repaint();
				getDeviceControls().repaint();
			}
		};
		wrapper.add(getDeviceDisplay(), "pos 5 5");
		wrapper.add(getDeviceControls(), "pos 560 5");
		wrapper.setName("DyIO");
		wrapper.setBorder(BorderFactory.createLoweredBevelBorder());
		return wrapper;
	}

	
	public boolean isMyNamespace(ArrayList<String> names) {
		for(String s:names){
			if(s.contains("neuronrobotics.dyio.*")){
				active=true;
			}
		}
		return isAcvive();
	}

	private boolean setUp = false;
	public boolean setConnection(BowlerAbstractConnection connection){
		//System.err.println(this.getClass()+" setConnection");
		if(setUp)
			return true;
		//DyIO.disableFWCheck();
		DyIORegestry.setConnection(connection);
		DyIORegestry.get().connect();
		DyIORegestry.get().addDyIOEventListener(this);
		DyIORegestry.get().setMuteResyncOnModeChange(true);
		setupDyIO();
		DyIORegestry.get().setMuteResyncOnModeChange(false);
		DyIORegestry.get().getBatteryVoltage(true);
		manager.removeNRConsoleTabedPanelPlugin("NRConsolePIDPlugin");
		setUp = true;
		return true;
	}
	private void setupDyIO(){
		
		int index = 0;
		ArrayList<DyIOChannel> chans =(ArrayList<DyIOChannel>) DyIORegestry.get().getChannels();
		Log.debug("DyIO state: "+DyIORegestry.get()+ " \nchans: "+chans );
		for(DyIOChannel c : chans) {
			//System.out.println(this.getClass()+" Adding channel: "+index+" as mode: "+c.getMode());
			ChannelManager cm = new ChannelManager(c);
			cm.addListener(this);
			if(index == 0) {
				selectChannel(cm);
			}
			channels.add(cm);
			index++;


		}
		//System.out.println(this.getClass()+" setupDyIO: "+ channels.size());
		devicePanel.addChannels(channels.subList(00, 12), false);
		devicePanel.addChannels(channels.subList(12, 24), true);
		//onModeChange();
	}

	
	public boolean isAcvive() {
		// TODO Auto-generated method stub
		return active;
	}

	
	public ArrayList<JMenu> getMenueItems() {
		JMenu collectionMenu = new JMenu("DyIO");
		collectionMenu.add(showGraphMenuItem);
		collectionMenu.add(exportData);
		collectionMenu.add(showPidConf);
		collectionMenu.add(showSequencerConf);
		collectionMenu.add(showHexapodConf);
		showGraphMenuItem.setMnemonic(KeyEvent.VK_G);
		showGraphMenuItem.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				displayGraphingWindow(true);
			}
		});
		showSequencerConf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				INRConsoleTabedPanelPlugin p = new NRConsoleSchedulerPlugin(manager);
				p.setConnection(DyIORegestry.get().getConnection());
				manager.firePluginUpdate();
				showSequencerConf.setEnabled(false);
			}
		});
		showPidConf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					public void run() {
						NRConsolePIDPlugin p =new NRConsolePIDPlugin(manager);
						p.isMyNamespace(manager.getNameSpaces());
						p.setConnection(DyIORegestry.get().getConnection());
						//new NRConsoleSchedulerPlugin(manager);
						manager.firePluginUpdate();
						showPidConf.setEnabled(false);
					}
				}.start();
			}
		});
		showHexapodConf.addActionListener(new ActionListener() {	
			
			public void actionPerformed(ActionEvent e) {		
				
				INRConsoleTabedPanelPlugin p =new HexapodNRConsolePulgin(manager);
				p.setConnection(DyIORegestry.get().getConnection());
				manager.firePluginUpdate();
				showHexapodConf.setEnabled(false);
				
			}
		});
		exportData.setMnemonic(KeyEvent.VK_E);
		exportData.setEnabled(true);
		exportData.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				displayGraphingDialog();
			}
		});
		ArrayList<JMenu> m = new ArrayList<JMenu>();
		m.add(collectionMenu);
		if(isAcvive())
			return m;
		return null;
	}
	public void addActionListener(ActionListener l) {
		showGraphMenuItem.addActionListener(l);
		graphOptionsMenuItem.addActionListener(l);
		exportData.addActionListener(l);
	}
	public void displayGraphingWindow(boolean visible) {
		graphingWindow.setVisible(visible);
	}
	
	public void displayGraphingDialog() {
		graphingDialog.showDialog();
	}
	
	public void displayGraphingOptionsDialog(boolean b) {
		graphingOptionsDialog.setVisible(b);
	}
	
	public void toggleGraph(ChannelRecorder channelRecorder) {
		graphingWindow.toggleDataChannel(channelRecorder.getDataChannel());
	}

	
	public void onRecordingEvent(ChannelManager source, boolean enabled) {
		toggleGraph(source.getChannelRecorder());
	}
	
	public void selectChannel(ChannelManager cm) {
		cm.getControlPanel();
	}

	public DyIOPanel getDeviceDisplay() {
		return devicePanel;
	}
	
	public DyIOControlsPanel getDeviceControls() {
		return deviceControls;
	}
	
	
	public void onClick(ChannelManager source, int type) {
		switch(type) {
		case SINGLE_CLICK:
			for(ChannelManager cm: channels) {
				cm.setActive(false);
			}
			deviceControls.setChannel(source.getControlPanel());
			break;
		case SHIFT_CLICK:
		case CTRL_CLICK:
			deviceControls.addChannel(source.getControlPanel());
			break;
		}
		source.setActive(true);
	}

	
	public void onModeChange() {
		for(ChannelManager cm : channels) {
			cm.refresh();
		}
	}

	public void recordData() {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(null);
		
		File file = chooser.getSelectedFile();
		chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Workbook", "xls");

        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                //System.out.println(chooser.getFileFilter());

                //if(!filter.accept(file)) {
                	file = new File(file.getAbsolutePath() + ".xls");
                //}
                	
        		ExcelWriter ew = new ExcelWriter();
        		ew.setFile(file);
        		for(ChannelManager cm : channels) {
        			ew.addData(cm.getChannelRecorder().getDataChannel());
        		}
        		ew.cleanup();
        		JOptionPane.showMessageDialog(null, "Successfully exported data as an Excel file", "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception e) {
            	JOptionPane.showMessageDialog(null, "Unable to save file. Please check the file and try again.", "File Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
	}

	
	public void onDyIOEvent(IDyIOEvent e) {
		if(e.getClass() == DyIOPowerEvent.class){
			//System.out.println("Got power event: "+e);
			devicePanel.setPowerEvent(((DyIOPowerEvent)e));
			try{
				for(ChannelManager cm : channels) {
					cm.onDyIOPowerEvent();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}


	@Override
	public void onDisconnect() {
		if(hexFrame!=null)
			hexFrame.setVisible(false);
	}


	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1095,700);
	}

}
