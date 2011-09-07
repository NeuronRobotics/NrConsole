package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoWidget extends ControlWidget implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JSlider sliderUI = new JSlider();
	private JLabel valueUI = new JLabel();
	private JCheckBox liveUpdate = new JCheckBox("Live");
	private JButton save = new JButton("Set Default");
	private ServoChannel sc;
	private boolean startup = true;
	private int saveValue = 256;
	public ServoWidget(ChannelManager channel, DyIOChannelMode mode) {
		super(channel);
		setRecordable(true);
		try{
			sc = new ServoChannel(getChannel());
		}catch (Exception e){
			return;
		}
		
		setLayout(new MigLayout());

		sliderUI.setMaximum(0);
		sliderUI.setMaximum(255);
		sliderUI.setMajorTickSpacing(15);
		sliderUI.setPaintTicks(true);
		
		
		add(sliderUI);
		add(valueUI);
		add(liveUpdate, "wrap");
		add(save);
		
		setValue(getChannel().getValue());
		liveUpdate.setSelected(true);
		
		sliderUI.addChangeListener(this);
		save.addActionListener(this);
		startup = false;
	}
	
	private String formatValue(int value) {
		return String.format("%03d", value);
	}

	private void setValue(int value) {
		if(value < 0) {
			value = 0;
		}
		
		if(value > 255) {
			value = 255;
		}
		
		pollValue();
		recordValue(value);
		sliderUI.setValue(value);
		valueUI.setText(formatValue(value));
	}

	
	public void stateChanged(ChangeEvent e) {
		valueUI.setText(formatValue(sliderUI.getValue()));
		
		if(!liveUpdate.isSelected() && sliderUI.getValueIsAdjusting()) {
			return;
		}
		
		pollValue();
		
		if(sliderUI.getValue() !=saveValue )
			save.setEnabled(true);
		else
			save.setEnabled(false);
		if( startup == false ) {
			sc.SetPosition(sliderUI.getValue());
			recordValue(sliderUI.getValue());
		}
	}

	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save){
			saveValue=sliderUI.getValue();
			sc.SavePosition(saveValue);
			save.setEnabled(false);
		}
	}
	
	
	public void pollValue() {
		recordValue(sliderUI.getValue());
	}

	
	public DyIOAbstractPeripheral getPerpheral() {
		// TODO Auto-generated method stub
		return null;
	}
}
