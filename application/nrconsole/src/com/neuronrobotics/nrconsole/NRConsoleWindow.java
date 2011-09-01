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

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.config.SDKBuildInfo;


public class NRConsoleWindow extends JFrame {
	private ArrayList<JPanel> panels=new ArrayList<JPanel>();
	private static final String name = "Neuron Robotics Console ";
	public static int panelHight = 700;
	public static int panelWidth = 1095;
	private static final long serialVersionUID = 1L;
	private JPanel scroller = new  JPanel();
	private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	private JTabbedPane modePane = new JTabbedPane();
	private JPanel active=null;
	private static NRConsoleWindow instance = null;
	public ImageIcon logo = new ImageIcon(NRConsole.class.getResource("images/logo.png"));
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
		displayLogo();
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
				scroller.setPreferredSize(new Dimension(panelWidth-53,1000));
				scroller.setVisible(true);
				modePane.setSize(getWindowSize());
			}			
		});
		logoPanel.setLayout(new GridLayout(3, 2));
		logoPanel.add(new JLabel(logo),"[0,2]");
	}
	
	
	public void repaint(){		
		logoPanel.repaint();
		for(JPanel p: panels){
			p.repaint();
		}
		super.repaint();
	}
	private void updateUI(){
		//setSize(new Dimension(panelWidth+53,panelHight+105));
		//scroller.setSize(getWindowSize());
		scroller.setPreferredSize(new Dimension(panelWidth-53,1000));
		//setLocationRelativeTo(null);
		scroller.invalidate();
		scroller.repaint();
		scroller.setVisible(true);
		invalidate();
		repaint();
		
	}
	
	public void setDeviceManager(PluginManager deviceManager) {
		panelHight = 700;
		panelWidth = 1095;
		scroller.removeAll();
		modePane.removeAll();
		panels=new ArrayList<JPanel>();
		for(JPanel p: deviceManager.getPanels()){
			modePane.addTab(p.getName(), p);
			panels.add(p);
		}

		scroller.add(modePane);
		updateUI();
	}

	public void displayLogo() {
		scroller.removeAll();
		
		
		scroller.add(logoPanel);
		updateUI();
	}

	public static String getConsoleVersion() {
		return name+SDKBuildInfo.getVersion();
	}

}
