package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.util.IntegerComboBox;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class SchedulerGui extends JPanel{

	/**
	 * 
	 */
	//private DyIO d = new DyIO();
	private static final long serialVersionUID = -2532174391435417313L;
	JPanel channelBar = new JPanel(new MigLayout());
	private IntegerComboBox availibleChans = new IntegerComboBox();
	private IntegerComboBox usedChans = new IntegerComboBox();
	private ArrayList< ServoOutputScheduleChannelUI> outputs = new ArrayList< ServoOutputScheduleChannelUI>();
	CoreScheduler cs;
	public SchedulerGui(){
		setName("DyIO Sequencer");
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		cs = new CoreScheduler(DyIORegestry.get());
		SchedulerControlBar cb = new SchedulerControlBar(cs);
		
		//cb.setAudioFile(new File("track.mp3"));
		
		JPanel addBar = new JPanel(new MigLayout());
		JButton addChannel = new JButton("Add new channel");
		addChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					int selected = availibleChans.getSelectedInteger();
					ServoOutputScheduleChannelUI sosc= 	new ServoOutputScheduleChannelUI(
														cs.addServoChannel(selected));
					
					outputs.add(sosc);
					channelBar.add(sosc,"wrap");
					availibleChans.removeInteger(selected);
					usedChans.addInteger(selected);
				}catch (Exception ex){
					JOptionPane.showMessageDialog(null, "Failed to select channel, "+ex.getMessage(), "Bowler ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		for(int i=0;i<24;i++){
			availibleChans.addInteger(i);
		}
		addBar.add(addChannel);
		addBar.add(availibleChans);
		
		JButton removeChannel = new JButton("Remove channel");
		removeChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					try{
						int selected = usedChans.getSelectedInteger();
						availibleChans.addInteger(selected);
						for(int i=0;i<outputs.size();i++){
							ServoOutputScheduleChannelUI s = outputs.get(i);
							if(s.getChannelNumber()==selected){
								cs.removeServoOutputScheduleChannel(s.getChannel());
								outputs.remove(s);
								channelBar.remove(s);
								usedChans.removeInteger(selected);
								return;
							}
						}
						
					}catch (Exception ex){
						JOptionPane.showMessageDialog(null, "Failed to select channel, "+ex.getMessage(), "Bowler ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}catch (Exception ex){
					JOptionPane.showMessageDialog(null, "Failed to select channel, "+ex.getMessage(), "Bowler ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		addBar.add(removeChannel);
		addBar.add(usedChans);
		
		channelBar.setBorder(BorderFactory.createRaisedBevelBorder());
		
		add(cb,"wrap");
		add(addBar,"wrap");
		add(channelBar,"wrap");
	}
	
	protected SchedulerGui getGui() {
		return this;
	}

	public boolean setConnection(BowlerAbstractConnection connection) {
		DyIORegestry.setConnection(connection);
		return DyIORegestry.get().ping()!=null;
	}
	
	
	
	public static void main(String[] args) {
		 JFrame frame = new JFrame();
		 SchedulerGui sg =new SchedulerGui();
		 sg.setConnection(new SerialConnection("/dev/DyIO0"));
		 //sg.setConnection(new SerialConnection("COM14"));
		 frame .add(sg);
		 frame.setSize(new Dimension(1024,768));
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.setVisible(true);
	}
}
