package com.neuronrobotics.nrconsole;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
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
import javax.swing.Scrollable;
import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.config.SDKBuildInfo;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;


public class NRConsoleWindow extends JFrame implements IPluginUpdateListener {
	private ArrayList<JPanel> panels=new ArrayList<JPanel>();
	private static final String name = "Neuron Robotics Console ";
//	public static int panelHight = 700;
//	public static int panelWidth = 1095;
	private static final long serialVersionUID = 1L;
	private JPanel scroller = new  JPanel();
	private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	private JTabbedPane modePane = new JTabbedPane();
	private JPanel active=null;
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
		//System.out.println("Window size "+d);
		return d;
	}
	private JPanel logoPanel = new JPanel();
	
	public NRConsoleWindow() {
		super(getConsoleVersion());
		instance=this;
		scroller.add(logoPanel);
		
		scrollPanel.setViewportView(scroller);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		add(scrollPanel);
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
		logoPanel.setLayout(new GridLayout(3, 2));
		logoPanel.add(new JLabel(logo),"[0,2]");
		setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage()); 
		updateUI();
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
			scroller.setPreferredSize(new Dimension(	manager.getMinimumWidth(),
														manager.getMinimumHeight()
													)
									  );
		scroller.setSize(getWindowSize());
		scroller.invalidate();
		scroller.repaint();
		scroller.setVisible(true);
	}
	private void updateUI(){
		//setSize(new Dimension(panelWidth+53,panelHight+105));
		if (manager !=null){
			setSize((manager.getMinimumWidth()+53),805);
			modePane.setSize(	manager.getMinimumWidth(),
								manager.getMinimumHeight()
							);	
		}else {
			setSize((1095+53),805);
		}
		
		updateScroller();

		invalidate();
		repaint();
		
	}
	
	public void setDeviceManager(PluginManager deviceManager) {
		manager=deviceManager;
		scroller.removeAll();
		modePane.removeAll();
		panels=new ArrayList<JPanel>();
		for(JPanel p: deviceManager.getPanels()){
			modePane.addTab(p.getName(), p);
			panels.add(p);
		}
		deviceManager.addIPluginUpdateListener(this);
		scroller.add(modePane);
		updateUI();
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

}
