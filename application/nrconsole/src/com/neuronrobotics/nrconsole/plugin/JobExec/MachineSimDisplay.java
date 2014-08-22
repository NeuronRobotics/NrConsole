package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit.CutAction;

import jme3test.games.CubeField;
import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
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
	Node obj;
	Matrix3f rotateUp;
	private int layersToShow;
	private int lastShownIndex;
	private Material matGood;
	private Material matBad;
	private Material matFail;
	private Material matLine;
	private boolean loading= false;
	private BitmapText loadingText = null;
	private Vector3f scaleRate = new Vector3f(.5f,.5f,.5f);
	
	
	private boolean showGood = true;
	private boolean showTroubled = true;
	private boolean showDangerous = true;
	private boolean showNonExtrude = true;
	private PrintObject printObj;
	private List<PrintTestListener> listeners = new ArrayList<PrintTestListener>();
	public MachineSimDisplay(JPanel _panel){		
		panel = _panel;
		printObj = new PrintObject(this);
	}
	
	public void configure(Vector3f _printVolume, Vector3f _printOrigin){
		printObj.configure(_printVolume, _printOrigin);
		
	}
	public void configureRect(float _printX, float _printY, float _printZ){
		printObj.configureRect(_printX, _printY, _printZ);
	}
	public void configureCylinder(float _printR, float _printZ){
		printObj.configureCylinder(_printR, _printZ);
	}
	public void addListener(PrintTestListener toAdd) {
        listeners.add(toAdd);
    }
	public void loadingGCode(){
		loading = true;
	}
	
	public boolean isPrintAllowed(){
		for (Geometry geo : shapes) {
			if (geo.getMaterial() == getMatFail()){
				return false;
			}
		}
		return true;
	}
	public boolean isPrintWarned(){
		for (Geometry geo : shapes) {
			if (geo.getMaterial() == getMatProblem()){
				return true;
			}
		}
		return false;
	}
	
	

	
	
	
	public Material getMatGood(){
		if (matGood == null){
			matGood = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matGood.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matGood.setColor("Ambient", ColorRGBA.Green);   // ... color of this object
        	matGood.setColor("Diffuse", ColorRGBA.Green);   // ... color of light being reflected
		}
		return matGood;
		
	}
	public Material getMatProblem(){
		if (matBad == null){
			matBad = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matBad.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matBad.setColor("Ambient", ColorRGBA.Orange);   // ... color of this object
        	matBad.setColor("Diffuse", ColorRGBA.Orange);   // ... color of light being reflected
		}
		return matBad;
	}
	public Material getMatLine(){
		if (matLine == null){
			matLine = new Material(assetManager,
			          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        \
	        
		       matLine.setColor("Color", ColorRGBA.Blue); 
		}
		return matLine;
	}
	public Material getMatFail(){
		if (matFail == null){
			matFail = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matFail.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matFail.setColor("Ambient", ColorRGBA.Red);   // ... color of this object
        	matFail.setColor("Diffuse", ColorRGBA.Red);   // ... color of light being reflected
		}
		return matFail;
	}
	
	public int getLayersToShow(){
		return layersToShow;
	}
	public void setLayersToShow(int numLayers){
		
		shapes.clear();
		shapes = printObj.getBatchedLayers(numLayers);
		layersToShow = numLayers;
		hasChanged = true;
		
	}
	
	
	public int loadGCode(GCodes _codes){
		
		
		shapes.clear();
		
		
		printObj = new PrintObject(_codes, this);
		printObj.processGCodes();
		shapes = printObj.getBatchedObject();
		System.out.println("Num of Codes: " + _codes.size());
		System.out.println("Num of Shapes: " + shapes.size());
		lastShownIndex = shapes.size();
		
		hasChanged = true;
		return printObj.getNumLayers();
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
		
    }
	
	@Override
	public void simpleInitApp() {
		
		    
        flyCam.setEnabled(false);
        
       BoundingBox bb = printObj.getVolBB();
        
        
        chaseCam = new ChaseCamera(getCamera(),rootNode, getInputManager());
        chaseCam.setHideCursorOnRotate(true);
        chaseCam.setDefaultDistance(353);
        chaseCam.setMaxDistance(100000);
        chaseCam.setEnabled(true);
        
        Vector3f viewOff = new Vector3f(bb.getCenter().getX(),0,bb.getCenter().getY()*-1);
        
        chaseCam.setLookAtOffset(viewOff);
        chaseCam.setMaxVerticalRotation((float) (Math.PI/2));
        chaseCam.setMinVerticalRotation(0);
        chaseCam.setInvertVerticalAxis(true);
       
        chaseCam.setDefaultHorizontalRotation(2);
        chaseCam.setDefaultVerticalRotation(0.824f);
        //chaseCam.setUpVector(new Vector3f(0,0,-1));
        
        chaseCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
              
              Vector3f max = bb.getMax(null);
              System.out.println(max.toString());
              Vector3f min = bb.getMin(null);
              System.out.println(min.toString());
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.White);
        lamp_light.setRadius(700f);
        lamp_light.setPosition(new Vector3f(min.getX(),max.getZ(),min.getY()*-1));
        rootNode.addLight(lamp_light);
        
        PointLight lamp_light1 = new PointLight();
        lamp_light1.setColor(ColorRGBA.White);
        lamp_light1.setRadius(700f);
        lamp_light1.setPosition(new Vector3f(max.getX(), max.getZ(),min.getY()*-1));
        rootNode.addLight(lamp_light1);
        
        PointLight lamp_light2 = new PointLight();
        lamp_light2.setColor(ColorRGBA.White);
        lamp_light2.setRadius(700f);
        lamp_light2.setPosition(new Vector3f(min.getX(),max.getZ(),max.getY()*-1));
        rootNode.addLight(lamp_light2);
        
        PointLight lamp_light3 = new PointLight();
        lamp_light3.setColor(ColorRGBA.White);
        lamp_light3.setRadius(700f);
        lamp_light3.setPosition(new Vector3f(max.getX(),max.getZ(),max.getY()*-1));
        
        rootNode.addLight(lamp_light3);
        
        obj = new Node();
        
        rootNode.attachChild(obj);
        
        
        
        //Quad base = new Quad(200, 200);
        
        Geometry base = new Geometry("Base", printObj.getPrintBase());
        //geom.setLocalTranslation(new Vector3f(-250,-250,0));
        Material mat = new Material(assetManager,  // Create new material and...
        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
        		
        mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	mat.setColor("Ambient", ColorRGBA.DarkGray);   // ... color of this object
    	mat.setColor("Diffuse", ColorRGBA.DarkGray);   // ... color of light being reflected
        		
     
    	base.setMaterial(mat);
    	rootNode.attachChild(base);
    	
        Vector3f init = new Vector3f(0,1,0);
        Vector3f end = new Vector3f(0,0,-1);
        rotateUp = new Matrix3f();
        rotateUp.fromStartEndVectors(init, end);
        rootNode.setLocalRotation(rotateUp);
       
        
       
        loadAxes(0,0,0);
        
	}
	public void loadAxes(int xOff, int yOff, int zOff){
		Vector3f zero = new Vector3f(xOff,yOff,zOff);
		Vector3f xV = new Vector3f(25 + xOff,yOff,zOff);
		Vector3f yV = new Vector3f(xOff,25 + yOff,zOff);
		Vector3f zV = new Vector3f(xOff,yOff,25 + zOff);
		Line x = new Line(zero, xV);
		Line y = new Line(zero, yV);
		Line z = new Line(zero, zV);
		Geometry geomX = new Geometry("X Axis", x);
		Geometry geomY = new Geometry("Y Axis", y);
		Geometry geomZ = new Geometry("Z Axis", z);
		geomX.setMaterial(getUnshadedColor(ColorRGBA.Green));
		geomY.setMaterial(getUnshadedColor(ColorRGBA.Red));
		geomZ.setMaterial(getUnshadedColor(ColorRGBA.Blue));
		
		rootNode.attachChild(geomX);
		rootNode.attachChild(geomY);
		rootNode.attachChild(geomZ);
	}
	
	public Material getUnshadedColor(ColorRGBA _color){
		Material mat = new Material(assetManager,
		          "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", _color);
		return mat;
	}
	
	private float scaleFactor = 1.0f;
	
	public BitmapText getLoadingText(float tpf){
		if (loadingText == null){
			guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
	        loadingText = new BitmapText(guiFont, false);
	        
	        loadingText.setSize(guiFont.getCharSet().getRenderedSize());
	        loadingText.setText("Loading...");
	        
	        float offsetX = (panel.getWidth()/2) - ((loadingText.getLineWidth()*loadingText.getLocalScale().getX())/2);
			float offsetY = (panel.getHeight()/2) + ((loadingText.getHeight()*loadingText.getLocalScale().getX())/2);
			
			loadingText.setLocalTranslation(offsetX, offsetY, 0);
	        loadingText.setName("Loading Text");
		}
		else{
			if (loadingText.getLocalScale().getX() > 5.0f){
				scaleFactor = -2.0f;
				
			}
			if (loadingText.getLocalScale().getX() < 1.5f){
				scaleFactor = 2.0f;
				
			}
			float scaleInc = scaleFactor * tpf;
			scaleInc += loadingText.getLocalScale().getX();
			
			loadingText.setLocalScale(scaleInc);
			
			
			float offsetX = (panel.getWidth()/2) - ((loadingText.getLineWidth()*loadingText.getLocalScale().getX())/2);
			float offsetY = (panel.getHeight()/2) + ((loadingText.getHeight()*loadingText.getLocalScale().getX())/2);
			
			loadingText.setLocalTranslation(offsetX, offsetY, 0);
			
		}
			
		
		return loadingText;
	}
	private void notifyIllegalPrint(){
		for (PrintTestListener ptl : listeners) {
			ptl.printIsIllegal();
		}
	}
	private void notifyWarnPrint(){
		for (PrintTestListener ptl : listeners) {
			ptl.printIsWarn();
		}
	}
	
	
	@Override
	public void simpleUpdate(float tpf){
		if (loading == true  && hasChanged == false){
			//Do something to let the user know things are happening			
	        guiNode.attachChild(getLoadingText(tpf));
		}
		else{
			
			guiNode.detachChildNamed("Loading Text");
		}
		
		if (hasChanged == true){
			
			hasChanged = false;
			System.out.println("Number of lines: " + shapes.size());
			
						
			
			obj.detachAllChildren();
			
			for (Geometry geom : shapes) {
				if (geom.getMaterial() == getMatGood() && isShowGood()){
					obj.attachChild(geom);
				}
				if (geom.getMaterial() == getMatProblem() && isShowTroubled()){
					obj.attachChild(geom);
				}
				if (geom.getMaterial() == getMatFail() && isShowDangerous()){
					obj.attachChild(geom);
				}
				if (geom.getMaterial() == getMatLine() && isShowNonExtrude()){
					obj.attachChild(geom);
				}
			}
			if (loading == true){// Only run these bits if this is the first time we are loading this file
				if (!isPrintAllowed()){
					notifyIllegalPrint();
				}
				if (isPrintWarned()){
					notifyWarnPrint();
				}
			}
			
			
			System.out.println("Last Index Shown: " +lastShownIndex);
			System.out.println("How many children: " + obj.getChildren().size());
			loading =  false;
		}
		//System.out.println("Vertical: " + chaseCam.getVerticalRotation() + 
			//	"Horizontal: " + chaseCam.getHorizontalRotation()+
				//"Zoom: " + chaseCam.getDistanceToTarget());
	 }

	public boolean isShowGood() {
		return showGood;
	}

	public void setShowGood(boolean showGood) {
		this.showGood = showGood;
		hasChanged = true;
	}

	public boolean isShowTroubled() {
		return showTroubled;
	}

	public void setShowTroubled(boolean showTroubled) {
		
		this.showTroubled = showTroubled;
		hasChanged = true;
	}

	public boolean isShowDangerous() {
		return showDangerous;
	}

	public void setShowDangerous(boolean showDangerous) {
		
		this.showDangerous = showDangerous;
		hasChanged = true;
	}

	public boolean isShowNonExtrude() {
		return showNonExtrude;
	}

	public void setShowNonExtrude(boolean showNonExtrude) {
		this.showNonExtrude = showNonExtrude;
		hasChanged = true;
	}
	
	
}
