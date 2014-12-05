package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.GettingStartedPanel;
import com.neuronrobotics.sdk.common.BowlerDocumentationFactory;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoWidget extends ControlWidget implements ChangeListener, ActionListener, IServoPositionUpdateListener {
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
			sc.addIServoPositionUpdateListener(this);
		}catch (Exception e){
			return;
		}
		saveValue = sc.getConfiguration();
		setLayout(new MigLayout());

		sliderUI.setMaximum(0);
		sliderUI.setMaximum(255);
		sliderUI.setMajorTickSpacing(15);
		sliderUI.setPaintTicks(true);
		
		//Button to launch info page for Servo Panel
		JButton helpButton = new JButton("Help");
		
		//Label for Servo Panel
		JLabel helpLabel = new JLabel("Servo Panel");
		add(helpLabel, "split 2, span 2, align left");
		add(helpButton, "gapleft 200, wrap, align right");
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					GettingStartedPanel.openPage(BowlerDocumentationFactory.getDocumentationURL(sc));
				} catch (Exception exceptE) {}
			}
		});
		
		//Help button formating
		helpButton.setFont((helpButton.getFont()).deriveFont(8f));
		helpButton.setBackground(Color.green);
		
		//Servo Panel label formating
		helpLabel.setHorizontalTextPosition(JLabel.LEFT);
		helpLabel.setForeground(Color.GRAY);
		
		JPanel pan = new JPanel(new MigLayout()); 
		pan.add(sliderUI);
		pan.add(valueUI);
		pan.add(liveUpdate, "wrap");
		pan.add(save);
		add(pan);
		
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
		try{
			pollValue();
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		if(sc.getChannel().getDevice().getCachedMode()){
			sc.getChannel().getDevice().setCachedMode(false);
		}
		
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

	@Override
	public void onServoPositionUpdate(ServoChannel srv, int position,double time) {
		if(srv == sc){
			Log.warning("Changing the servo from async "+srv.getChannel().getChannelNumber()+" to val: "+position);
			sliderUI.removeChangeListener(this);
			//sliderUI.setValue(position);
			valueUI.setText(formatValue(position& 0x000000ff));
			sliderUI.addChangeListener(this);
		}
	}
}
