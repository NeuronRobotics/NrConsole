package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class SchedulerGui extends JPanel{
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
	/**
	 * 
	 */
	//private DyIO d = new DyIO();
	private static final long serialVersionUID = -2532174391435417313L;
	public SchedulerGui(){
		setName("DyIO Scheduler");
		slider.setMajorTickSpacing(1000);
		slider.setPaintTicks(true);
		setBounds(100);
		setValue(0);
		
		play.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				if(st == null){
					int setpoint;
					try{
						setpoint = (int)(1000*Double.parseDouble(length.getText()));
					}catch (NumberFormatException n){
						setpoint=1000;
					}
					setTrackLegnth(setpoint);
					st = new SchedulerThread(setpoint);
					st.start();
					play.setText("Pause");
					
				}else{
					st.kill();
					st=null;
					play.setText("Play");
				}
			}
			
		});
		
		add(length);
		add(time);
		add (slider);
		add(play);
		add(loop);
		
	}
	
	private void setTrackLegnth(int ms){
		length.setText(new Double(((double)ms)/1000.0).toString());
	}
	
	public boolean setConnection(BowlerAbstractConnection connection) {
		DyIORegestry.setConnection(connection);
		return DyIORegestry.get().ping()!=null;
	}
	private void setValue(long  val){
		//System.out.println("Setting value: "+val);
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
			setBounds((int)(ms));
			slider.setValue((int) StartOffset);
			//setValue(0);
		}
		public void run(){
			//System.out.println("Starting timer");
			do{
				long start = System.currentTimeMillis();
				StartOffset = slider.getValue();
				if(mp3==null){
					while( (((double)(System.currentTimeMillis()-start))<(time-StartOffset)) && run){
						long offset = ((System.currentTimeMillis()-start))+StartOffset;
						setValue(offset);
						ThreadUtil.wait(100);
					}
				}else{
					mp3.setCurrentTime((int) (StartOffset*1000));
				}
				if(run)
					setValue(0);
			}while(isLooping() && run);
			
			play.setText("Play");
			st=null;
		}
		public void kill(){
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
        	mp3File=fc.getSelectedFile();
        	mp3 = new MP3(mp3File.getAbsolutePath());
        	setTrackLegnth(mp3.getTrackLength());
        }
	}
	public static void main(String[] args) {
		 JFrame frame = new JFrame();
		 SchedulerGui sg =new SchedulerGui();
		 sg.setConnection(new SerialConnection("COM14"));
		 frame .add(sg);
		 frame.setSize(new Dimension(1024,768));
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.setVisible(true);
	}
}
