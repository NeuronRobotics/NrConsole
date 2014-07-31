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
import com.jme3.scene.VertexBuffer;
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
	GCodes codes;
	private ChaseCamera chaseCam;
	Node obj;
	Matrix3f rotateUp;
	public MachineSimDisplay(JPanel _panel){
		
		panel = _panel;
	}
	
	public void loadGCode(GCodes _codes){
		shapes.clear();
		codes = _codes;
		for (int i = 1; i < _codes.size(); i++) {
			GCodePosition code = _codes.get(i);
			if (_codes.isPrintMove(code)){
				boxBuilder(code);
			}
			else{
				lineBuilder(code);
			}
			
		}
		System.out.println("Num of Codes: " + _codes.size());
		System.out.println("Num of Shapes: " + shapes.size());
		hasChanged = true;
	}
	
	public void boxBuilder(GCodePosition _code){
		GCodePosition prevCode =codes.getPrevCode(_code);
		if (prevCode != null){
			
			double x1 =  prevCode.getX();
			double y1 =  prevCode.getY();
			double z1 =  prevCode.getZ();
			
			double x2 =  _code.getX();
			double y2 =  _code.getY();
			double z2 =  _code.getZ();
			if ((x1 > 200) || (y1 > 200) || (z1 > 200)){
				System.out.println("The Ones: (" + x1 + ","+ y1 + "," + z1 + ")");
			}
			if ((x2 > 200) || (y2 > 200) || (z2 > 200)){
				System.out.println("The Twos: (" + x2 + ","+ y2 + "," + z2 + ")");
			}
			
		float centerX = (float) ((x1 + x2)/2);
		float centerY = (float) ((y1 + y2)/2);
		float centerZ = (float) ((z1 +z2)/2);
		
		float extentX = (float) codes.getMoveLength(_code);
		
		extentX = extentX/2;
		float extentY = (float) ((codes.getMoveVolExtWidth(_code))/2);
		float extentZ = (float) ((codes.getLayerHeight(_code))/2);
	
		
		
		Vector3f zeroCenter = new Vector3f(0,0,0);
		
		Vector3f newCenter = new Vector3f(centerX, centerY, centerZ);
		Vector3f start = new Vector3f((float)x1,(float)y1,(float)z1);
		Vector3f end = new Vector3f((float)x2,(float)y2,(float)z2);
		Vector3f dir = end.subtract(start).normalizeLocal();
		
		
		Vector3f init = new Vector3f(1,0,0);
		Matrix3f direction = new Matrix3f();
		direction.fromStartEndVectors(init, dir);		
		Box b = new Box();
		b.updateGeometry(zeroCenter, extentX, extentY, extentZ);
       
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape      
        
        geom.setLocalRotation(direction);
        geom.setLocalTranslation(newCenter);
        
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        \
        
        if (codes.isGoodExtrusion(_code)){
        	mat.setColor("Color", ColorRGBA.Green);
        }
        else{
        	mat.setColor("Color", ColorRGBA.Red);
        }
        if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
		//	System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
			mat.setColor("Color", ColorRGBA.Yellow);
		}
        geom.setMaterial(mat);                   // set the cube's material
        shapes.add(geom);
		}
       
	}
	
	public void lineBuilder(GCodePosition _code){
		GCodePosition prevCode =codes.getPrevCode(_code);
		if (prevCode != null){
			
			double x1 =  prevCode.getX();
			double y1 =  prevCode.getY();
			double z1 =  prevCode.getZ();
			
			double x2 =  _code.getX();
			double y2 =  _code.getY();
			double z2 =  _code.getZ();
			
			
		
		Vector3f start = new Vector3f((float)x1,(float)y1,(float)z1);
		Vector3f end = new Vector3f((float)x2,(float)y2,(float)z2);
		
       Line l = new Line(start, end);
        Geometry geom = new Geometry("Line", l);  // create line geometry from the shape      
        
        
        
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        \
        
       mat.setColor("Color", ColorRGBA.Blue);
       
       /* if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
			System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
			mat.setColor("Color", ColorRGBA.Green);
		}
		*/
        geom.setMaterial(mat);                   // set the cube's material
        shapes.add(geom);
		}
       
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
        
        //chaseCam.setDefaultHorizontalRotation((float) ((Math.PI/2)));
        //chaseCam.setDefaultVerticalRotation((float) (-1*(Math.PI/4)));
        //chaseCam.setUpVector(new Vector3f(0,0,-1));
        
       // chaseCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
        Node nodeLight = new Node();
        nodeLight.move(0, 200, 0);
        Light mainLight;
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-1,1).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        nodeLight.addLight(sun);
        
        obj = new Node();
        Vector3f init = new Vector3f(0,1,0);
        Vector3f end = new Vector3f(0,0,1);
        rotateUp = new Matrix3f();
        rotateUp.fromStartEndVectors(init, end);
        //obj.setLocalRotation(rotateUp);
        rootNode.attachChild(obj);
        //getFlyByCamera().setDragToRotate(true);
        
        
        
	}
	@Override
	 public void simpleUpdate(float tpf){
		if (hasChanged == true){
			hasChanged = false;
			System.out.println("Number of lines: " + shapes.size());
			obj.detachAllChildren();
			for (int i = 0; i < shapes.size(); i++) {
				obj.attachChild(shapes.get(i));
			}
			System.out.println("How many children: " + rootNode.getChildren().size());
		}
		System.out.println("Vertical: " + chaseCam.getVerticalRotation() + "Horizontal: " + chaseCam.getHorizontalRotation());
	 }
	
	protected class SwingApplicationStarter implements Runnable {
		@Override
		public void run() {
		
		
		}
		}
}
