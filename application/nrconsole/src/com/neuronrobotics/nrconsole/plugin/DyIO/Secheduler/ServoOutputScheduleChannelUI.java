package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.util.IntegerComboBox;

import net.miginfocom.swing.MigLayout;

public class ServoOutputScheduleChannelUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7112414698561768276L;
	private ServoOutputScheduleChannel channel;
	private JCheckBox record = new JCheckBox("Record");
	private JButton startRecording = new JButton("Start Recording");
	private JPanel recordConfig = new JPanel();
	IntegerComboBox availible;
	public ServoOutputScheduleChannelUI(ServoOutputScheduleChannel chan){
		setChannel(chan);
		setLayout(new MigLayout());
		availible=new IntegerComboBox();
		for(int i=8;i<16;i++){
			availible.addInteger(i);
		}
		setBorder(BorderFactory.createLoweredBevelBorder());
		record.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(record.isSelected()){
					recordConfig.setVisible(true);
				}else{
					recordConfig.setVisible(false);
					pause();
				}
			}
		});
		
		startRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!getChannel().isRecording()){
					resume();
				}
				else{
					pause();
				}
			}
		});
		
		recordConfig.add(availible);
		recordConfig.add(startRecording);
		recordConfig.setVisible(false);
		add(new JLabel("Output Channel: "+getChannel().getChannelNumber()));
		add(record);
		add(recordConfig);
	}
	
	private void pause(){
		getChannel().pauseRecording();
		startRecording.setText("Start Recording");
	}
	private void resume(){
		getChannel().startRecording(availible.getSelectedInteger(), 512, .25);
		startRecording.setText("Pause Recording");
		availible.setEditable(false);
	}
	
	public int getChannelNumber() {
		// TODO Auto-generated method stub
		return getChannel().getChannelNumber();
	}
	public void setChannel(ServoOutputScheduleChannel channel) {
		this.channel = channel;
	}
	public ServoOutputScheduleChannel getChannel() {
		return channel;
	}
	
}
