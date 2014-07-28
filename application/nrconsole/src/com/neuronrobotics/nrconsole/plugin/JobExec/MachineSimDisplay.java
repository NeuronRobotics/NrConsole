package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;
import com.jogamp.newt.Display;

public class MachineSimDisplay extends SimpleApplication{
	JmeCanvasContext ctx;
	JPanel panel;
	private ArrayList<Geometry> shapes = new ArrayList<Geometry>();
	private boolean hasChanged = false;
	private ChaseCamera chaseCam;
	public MachineSimDisplay(JPanel _panel){
		
		panel = _panel;
	}
	
	public void loadGCode(GCodes _codes){
		shapes.clear();
		for (int i = 1; i < _codes.size(); i++) {
			GCodePosition code = _codes.get(i);
			GCodePosition prevCode = _codes.get(i-1);
			lineBuilder(prevCode.getX(), code.getX(),
						prevCode.getY(), code.getY(),
						prevCode.getZ(), code.getZ(),
						_codes.getLayerHeight(code));
		}
		hasChanged = true;
	}
	
	public void lineBuilder(double x1, double x2, double y1, double y2, double z1, double z2, double layerHeight){
		float centerX = (float) ((x1 + x2)/2);
		float centerY = (float) ((y1 + y2)/2);
		float centerZ = (float) ((z1 + z2)/2);
		float extentX = (float) (Math.sqrt((Math.pow((x2-x1),2))+(Math.pow((y2-y1),2))+(Math.pow((z2-z1),2))));
		float extentY = (float) (0.5);
		float extentZ = (float) (layerHeight/2);
		Vector3f center = new Vector3f(centerX, centerY, centerZ);
		Vector3f start = new Vector3f((float)x1,(float)y1,(float)z1).normalize();
		Vector3f end = new Vector3f((float)x2,(float)y2,(float)z2).normalize();
		Vector3f dir = end.subtract(start).normalize();
		Vector3f init = new Vector3f(1,1,1);
		Matrix3f direction = new Matrix3f();
		direction.fromStartEndVectors(init, dir);
		Quaternion quat = new Quaternion();
		quat.fromRotationMatrix(direction);
		Line l = new Line(start, end);
		
		Box b = new Box(extentX, extentY, extentZ);
		
       //b.updateGeometry(center, extentX, extentY, extentZ);
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        geom.getLocalRotation().lookAt(dir, new Vector3f(0,1,0));
        //geom.setLocalRotation( direction);
        geom.move(center);
        //geom.setLocalTranslation(center);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);                   // set the cube's material
        shapes.add(geom);
       // rootNode.attachChild(geom);
	}
	
	@Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        /*if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }*/
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        panel.setMinimumSize(new Dimension(100,100));
        createCanvas(); // create canvas!
		ctx = (JmeCanvasContext) getContext();
		ctx.setSystemListener(this);
		panel.add(ctx.getCanvas());
		//SwingUtilities.invokeLater(new SwingApplicationStarter ());
    }
	
	@Override
	public void simpleInitApp() {
		
		    
        flyCam.setEnabled(false);
        
       
        
        
        
        chaseCam = new ChaseCamera(getCamera(),rootNode, getInputManager());
        chaseCam.setDefaultDistance(200);
        chaseCam.setMaxDistance(100000);
        chaseCam.setEnabled(true);
        Vector3f viewOff = new Vector3f(100,100,0);
        chaseCam.setLookAtOffset(viewOff);
        chaseCam.setMaxVerticalRotation((float) Math.PI);
        chaseCam.setMinVerticalRotation(((float) Math.PI)*-1);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setDefaultHorizontalRotation((float) ((Math.PI/2)));
        chaseCam.setDefaultVerticalRotation((float) (-1*(Math.PI/4)));
        //chaseCam.setUpVector(new Vector3f(0,0,1));
       // chaseCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
        Node nodeLight = new Node();
        nodeLight.move(0, 200, 0);
        Light mainLight;
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-1,1).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        nodeLight.addLight(sun);
        
        //getFlyByCamera().setDragToRotate(true);
        
        
        
	}
	@Override
	 public void simpleUpdate(float tpf){
		if (hasChanged == true){
			hasChanged = false;
			System.out.println("Number of lines: " + shapes.size());
			rootNode.detachAllChildren();
			for (int i = 0; i < shapes.size(); i++) {
				rootNode.attachChild(shapes.get(i));
			}
			System.out.println("How many children: " + rootNode.getChildren().size());
		}
		//System.out.println("Vertical: " + chaseCam.getVerticalRotation() + "Horizontal: " + chaseCam.getHorizontalRotation());
	 }
	
	protected class SwingApplicationStarter implements Runnable {
		@Override
		public void run() {
		
		
		}
		}
}
