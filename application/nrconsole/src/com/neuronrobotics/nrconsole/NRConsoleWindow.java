package com.neuronrobotics.nrconsole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.config.SDKBuildInfo;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;


public class NRConsoleWindow extends JFrame implements IPluginUpdateListener {
	private ArrayList<JPanel> panels=new ArrayList<JPanel>();
	private static final String name = "Neuron Robotics Console ";

	private static final long serialVersionUID = 1L;
	private JPanel scroller = new  JPanel();
	private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	private JTabbedPane modePane = new JTabbedPane();

	private static NRConsoleWindow instance = null;
	public ImageIcon logo = new ImageIcon(NRConsole.class.getResource("images/logo.png"));
	private PluginManager manager;
	
	public static Dimension getNRWindowSize(){
		if(instance!= null)
			return instance.getWindowSize();
		return  new Dimension(400,400);
	}
	
	public Dimension getWindowSize(){
		Dimension d = new Dimension(getWidth(),getHeight());
		return d;
	}
	
	private JPanel logoPanel = new JPanel();
	
	public NRConsoleWindow() {
		super(getConsoleVersion());
		instance=this;
		scroller.setLayout(new BorderLayout(0, 0));
		scroller.add(logoPanel);
		
		scrollPanel.setViewportView(scroller);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		getContentPane().add(scrollPanel);
		getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			
			public void ancestorMoved(HierarchyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void ancestorResized(HierarchyEvent arg0) {
				//System.out.println("Resized: "+getWindowSize());
				scroller.setSize(getWindowSize());
				updateScroller();
				modePane.setSize(getWindowSize());
			}			
		});
		modePane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					modePane.setMinimumSize(modePane.getSelectedComponent().getMinimumSize());
					updateUI();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				
			}
		});
		logoPanel.setLayout(new GridLayout(3, 2));
		logoPanel.add(new JLabel(logo),"[0,2]");
		setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage()); 
		updateUI();
		setSize(1400, 1124);
	}
	
	
	public void repaint(){
		logoPanel.repaint();
		for(JPanel p: panels){
			p.repaint();
		}
		super.repaint();
	}
	private void updateScroller(){
		if(manager!=null)
			scroller.setPreferredSize(getCurrentPanelMinSize());
		scroller.setSize(getWindowSize());
		scroller.invalidate();
		scroller.repaint();
		scroller.setVisible(true);
	}
	private void updateUI(){
//		//setSize(new Dimension(panelWidth+53,panelHight+105));
//		if (manager !=null){
//			//setSize((getCurrentPanelMinSize().width+53),805);
//			modePane.setSize(getCurrentPanelMinSize());	
//		}else {
			
		//}
		
		updateScroller();

		invalidate();
		
	}
	
	public void setDeviceManager(PluginManager dm) {
		manager=dm;
		scroller.removeAll();
		modePane.removeAll();
		panels=new ArrayList<JPanel>();


		manager.addIPluginUpdateListener(this);
		manager.updateNamespaces();
		
		Log.warning("Start Adding plugins ");
		for(JPanel p: manager.getPanels()){
			SwingUtilities.invokeLater(() -> {
				Log.warning("Adding : " + p.getName());
				modePane.addTab(p.getName(), p);
				panels.add(p);
				Log.warning("Done : " + p.getName());
				updateUI();
			});
		}
		SwingUtilities.invokeLater(() -> {
			scroller.add(modePane);
			Log.warning("Done adding plugins ");
			SwingUtilities.invokeLater(() -> {
				updateUI();
			});
		});

	}

	public void displayLogo(PluginManager deviceManager) {
		scroller.removeAll();
		deviceManager.removeIPluginUpdateListener(this);
		scroller.add(logoPanel);
		updateUI();
	}

	public static String getConsoleVersion() {
		return name+SDKBuildInfo.getVersion();
	}
	@Override
	public void onPluginListUpdate(PluginManager manager) {
		//System.out.println(this.getClass()+" is refreshing");
		setDeviceManager(manager);
	}
	
	public Dimension getCurrentPanelMinSize(){
		if(getCurrentPanel()!=null){
			if (getCurrentPanel().getMinimumSize() != null){
				return getCurrentPanel().getMinimumSize();
			}
		}
		return new Dimension(manager.getMinimumWidth(), manager.getMinimumHeight());
		
	}
	public Component getCurrentPanel(){
		return modePane.getSelectedComponent();
	}
}
