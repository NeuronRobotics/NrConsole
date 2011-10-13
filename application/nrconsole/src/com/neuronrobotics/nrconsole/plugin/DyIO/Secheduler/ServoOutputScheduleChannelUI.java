package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neuronrobotics.nrconsole.util.IntegerComboBox;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

import net.miginfocom.swing.MigLayout;

public class ServoOutputScheduleChannelUI extends JPanel implements IServoPositionUpdateListener,ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7112414698561768276L;
	private ServoOutputScheduleChannel channel;
	private JCheckBox record = new JCheckBox("Record");
	private JButton startRecording = new JButton("Start Recording");
	private JButton startTest = new JButton("Start Test");
	private JPanel recordConfig = new JPanel();
	IntegerComboBox availible;
	private JTextField scale = new JTextField(5);
	private JTextField zero = new JTextField(5);
	
	private double currentScale=.25;
	private int currentZero = 512;
	public ServoOutputScheduleChannelUI(ServoOutputScheduleChannel chan){
		
		chan.addIServoPositionUpdateListener(this);
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
		startTest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(getChannel().isTesting()) {
					stopTest();
				}else
					startTest();
			}
		});
		
		recordConfig.add(availible);
		recordConfig.add(startRecording);
		recordConfig.add(startTest);
		recordConfig.setVisible(false);
		
		JPanel config = new JPanel(new MigLayout());
		config.add(new JLabel("Input Scale:"));
		config.add(scale,"wrap");
		config.add(new JLabel("Output Center:"));
		config.add(zero,"wrap");
		scale.addActionListener(this);
		zero.addActionListener(this);
		
		recordConfig.add(config);
		
		add(new JLabel("Output Channel: "+getChannel().getChannelNumber()));
		add(record);
		add(recordConfig);
		
		record.setSelected(getChannel().isRecording());
		try{
			availible.setSelectedInteger(getChannel().getInputChannelNumber());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		recordConfig.setVisible(record.isSelected());
		zero.setText(new Integer(getChannel().getInputCenter()).toString());
		scale.setText(new Double(getChannel().getInputScale()).toString());
		setScaleingInfo();
	}
	private void setScaleingInfo() {
		currentZero = Integer.parseInt(zero.getText());
		currentScale = Double.parseDouble(scale.getText());
		getChannel().setInputScale(getInputScale());
		getChannel().setInputCenter(getInputZero());
	}
	private int getInputZero() {
		return currentZero;
	}
	private double getInputScale() {
		return currentScale;
	}
	private void startTest() {
		startTest.setText("Stop  Test");
		setScaleingInfo();
		getChannel().startTest();
	}
	private void stopTest() {
		startTest.setText("Start Test");
		setScaleingInfo();
		getChannel().stopTest();
	}
	private void pause(){
		getChannel().stopTest();
		getChannel().pauseRecording();
		setScaleingInfo();
		startRecording.setText("Start Recording");
	}
	private void resume(){
		getChannel().stopTest();
		setScaleingInfo();
		getChannel().setAnalogInputChannelNumber(availible.getSelectedInteger());
		getChannel().startRecording();
		startRecording.setText("Pause  Recording");
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


	@Override
	public void onServoPositionUpdate(ServoChannel srv, int position,double time) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 setScaleingInfo();
	}
	
}
