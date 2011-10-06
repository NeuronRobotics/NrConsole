package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

import com.neuronrobotics.sdk.util.ThreadUtil;

import net.miginfocom.swing.MigLayout;

public class SchedulerControlBar extends JPanel {
	private SchedulerThread st=null;// = new SchedulerThread();
	private JSlider slider = new JSlider();
	private JButton play = new JButton("Play");
	private JCheckBox loop = new JCheckBox("Loop");
	private JLabel time = new JLabel("Seconds: ");
	private JTextField length = new JTextField("60.0");
	private JButton selectSong = new JButton("Select Audio Track");
	private JLabel trackName = new JLabel("none");
	private MP3 mp3;
	private File mp3File=null;
	private ChangeListener sliderListener;
	/**
	 * long 
	 */
	private static final long serialVersionUID = -5636481366169943501L;
	public SchedulerControlBar() {
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		
		setName("DyIO Scheduler");
		slider.setMajorTickSpacing(1000);
		slider.setPaintTicks(true);
		setBounds(100);
		setCurrentTime(0);
		sliderListener = new ChangeListener() {
			private boolean wasAdjusting = false;
			@Override
			public void stateChanged(ChangeEvent e) {
				slider.removeChangeListener(sliderListener);
				if(slider.getValueIsAdjusting()) {
					if(isPlaying()) {
						wasAdjusting=true;
						pause();
					}
				}else {
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
				if(st == null){
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
	
	private boolean isPlaying() {
		return st!=null;
	}
	
	private void play() {
		int setpoint;
		try{
			setpoint = (int)(1000*Double.parseDouble(length.getText()));
		}catch (NumberFormatException n){
			setpoint=1000;
		}
		
		st = new SchedulerThread(setpoint);
		st.start();
		play.setText("Pause");
	}
	private void pause() {
		st.kill();
		st=null;
		play.setText("Play");
	}
	
	private void setTrackLegnth(int ms){
		length.setText(new Double(((double)ms)/1000.0).toString());
		setBounds(ms);
	}
	

	private void setCurrentTime(long  val){

		try{
			slider.setValue((int) (val));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void setBounds(double top){
		slider.setMaximum(0);
		slider.setMaximum((int) (top));
	}
	private boolean isLooping(){
		return loop.isSelected();
	}
	private class SchedulerThread extends Thread{
		private double time;
		private boolean run = true;
		long StartOffset;
		public SchedulerThread(double ms){
			time = ms;
			StartOffset = slider.getValue();
			//System.out.println("Slider value of init="+StartOffset);
			setBounds((int)(ms));
			slider.setValue((int) StartOffset);
			setTrackLegnth((int) ms);
			if(mp3!=null) {
				mp3.setCurrentTime((int) (StartOffset));
			}
		}
		public void run(){
			//System.out.println("Starting timer");
			do{
				long start = System.currentTimeMillis();
				StartOffset = slider.getValue();
				//System.out.println("Initial slider value = "+StartOffset);
				if(mp3==null){
					while( (((double)(System.currentTimeMillis()-start))<(time-StartOffset)) && run){
						long offset = ((System.currentTimeMillis()-start))+StartOffset;
						setCurrentTime(offset);
						ThreadUtil.wait(100);
					}
				}else{
					mp3.play();
					while(mp3.isPlaying() && run) {
						setCurrentTime(mp3.getCurrentTime());
						ThreadUtil.wait(100);
					}
				}
				if(run && isLooping())
					setCurrentTime(0);
			}while(isLooping() && run);
			
			play.setText("Play");
			st=null;
		}
		public void kill(){
			if(mp3!=null) {
				mp3.pause();
			}
			run = false;
		}
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
		mp3File=f;
    	mp3 = new MP3(mp3File.getAbsolutePath());
    	setTrackLegnth(mp3.getTrackLength());
    	trackName.setText(mp3File.getName());
	}
}
