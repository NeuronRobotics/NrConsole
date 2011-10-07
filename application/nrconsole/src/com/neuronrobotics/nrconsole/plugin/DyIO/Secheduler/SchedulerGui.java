package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class SchedulerGui extends JPanel{

	/**
	 * 
	 */
	//private DyIO d = new DyIO();
	private static final long serialVersionUID = -2532174391435417313L;
	public SchedulerGui(){
		setName("DyIO Scheduler");
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		CoreScheduler cs = new CoreScheduler();
		SchedulerControlBar cb = new SchedulerControlBar(cs);
		
		cb.setAudioFile(new File("track.mp3"));
		
		JPanel addBar = new JPanel(new MigLayout());
		JButton addChannel = new JButton("Add new channel...");
		addChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		addBar.add(addChannel);
		
		add(cb,"wrap");
		add(addBar,"wrap");
		
	}
	
	public boolean setConnection(BowlerAbstractConnection connection) {
		DyIORegestry.setConnection(connection);
		return DyIORegestry.get().ping()!=null;
	}
	
	
	
	public static void main(String[] args) {
		 JFrame frame = new JFrame();
		 SchedulerGui sg =new SchedulerGui();
		 //sg.setConnection(new SerialConnection("/dev/DyIO0"));
		 frame .add(sg);
		 frame.setSize(new Dimension(1024,768));
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.setVisible(true);
	}
}
