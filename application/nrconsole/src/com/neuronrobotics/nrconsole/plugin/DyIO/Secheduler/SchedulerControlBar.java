package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

public class SchedulerControlBar extends JPanel implements ISchedulerListener {
	
	private JSlider slider = new JSlider();
	private JButton play = new JButton("Play ");
	private JCheckBox loop = new JCheckBox("Loop");
	private JLabel time = new JLabel("Seconds");

	private JTextField length = new JTextField(4);
	private JButton selectSong = new JButton("Select Audio Track");
	private JLabel trackName = new JLabel("none");
	private CoreScheduler cs;
	private File mp3File=null;
	private ChangeListener sliderListener;
	/**
	 * long 
	 */
	private static final long serialVersionUID = -5636481366169943501L;
	public SchedulerControlBar(CoreScheduler core) {
		core.addISchedulerListener(this);
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		
		
		slider.setMajorTickSpacing(1000);
		slider.setPaintTicks(true);
		setTrackLegnth(60000);
		setCurrentTime(0);
		cs =core;
		sliderListener = new ChangeListener() {
			private boolean wasAdjusting = false;
			@Override
			public void stateChanged(ChangeEvent e) {
				slider.removeChangeListener(sliderListener);
				if(slider.getValueIsAdjusting()) {
					if(cs.isPlaying()) {
						wasAdjusting=true;
						pause();
					}
				}else {
					setCurrentTime(slider.getValue());
					if(wasAdjusting) {
						wasAdjusting = false;
						play();
					}
				}
				slider.addChangeListener(sliderListener);
			}
		};
		slider.addChangeListener(sliderListener);
		
		play.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				if(!cs.isPlaying()){
					play();
				}else{
					pause();
				}
			}
			
		});
		
		selectSong.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getFile();
			}
		});
		
		loop.setSelected(false);
		loop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cs.setLooping(loop.isSelected());
			}
		});
		
		JPanel mp3Bar = new JPanel(new MigLayout());
		mp3Bar.add(selectSong);
		mp3Bar.add(new JLabel("Current Track:"));
		mp3Bar.add(trackName);
		
		JPanel trackBar = new JPanel(new MigLayout());
		trackBar.add(length);
		trackBar.add(time);
		trackBar.add (slider);
		trackBar.add(play);
		trackBar.add(loop);
		

		setBorder(BorderFactory.createRaisedBevelBorder());
		add(mp3Bar,"wrap");
		add(trackBar,"wrap");
	}
	
	private void play() {
		int start =slider.getValue(); 

		int setpoint;
		try{
			setpoint = (int)(1000*Double.parseDouble(length.getText()));
		}catch (NumberFormatException n){
			setpoint=1000;
		}
		setTrackLegnth(setpoint);
		cs.play(setpoint, start);
		play.setText("Pause");
	}
	private void pause() {
		cs.pause();
		play.setText("Play ");
	}
	
	private void setTrackLegnth(int ms){
		length.setText(new Double(((double)ms)/1000.0).toString());
		setBounds(ms);
	}
	

	private void setCurrentTime(long  val){
		//System.out.println("Setting current time="+val);
		try{
			slider.setValue((int) (val));
		}catch(Exception e){
			e.printStackTrace();
		}
		double cTime = ((double)val)/1000;
		time.setText("Seconds: "+new DecimalFormat("000.00").format(cTime));
		//System.out.println("Setting current time="+val+" slider="+slider.getValue());
	}
	private void setBounds(double top){
		slider.setMaximum(0);
		slider.setMaximum((int) (top));
	}

	
	private class mp3Filter extends FileFilter{
		
		public String getDescription() {
			return "MP3 Audio File (mp3)";
		}
		public boolean accept(File f) {
			if(f.isDirectory()) {
				return true;
			}
			String path = f.getAbsolutePath().toLowerCase();
			if ((path.endsWith("mp3") && (path.charAt(path.length() - 3)) == '.')) {
				return true;
			}
			return f.getName().matches(".+\\.mp3$");
		}
	}
	private void getFile() {
		JFileChooser fc =new JFileChooser();
    	File dir1 = new File (".");
    	if(mp3File!=null){
    		fc.setSelectedFile(mp3File);
    	}else{
    		fc.setCurrentDirectory(dir1);
    	}
    	fc.setFileFilter(new  mp3Filter());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	setAudioFile(fc.getSelectedFile());
        }
	}
	public void setAudioFile(File f) {
		cs.setAudioFile(f);
    	setTrackLegnth(cs.getTrackLength());
    	trackName.setText(f.getName());
    	length.setEditable(false);
    	setCurrentTime(0);
	}

	@Override
	public void onTimeUpdate(double ms) {
		setCurrentTime((long) ms);
	}

	@Override
	public void isStopped() {
		play.setText("Play");
	}

	@Override
	public void setIntervalTime(int msInterval, int totalTime) {
		// TODO Auto-generated method stub
		
	}

}
