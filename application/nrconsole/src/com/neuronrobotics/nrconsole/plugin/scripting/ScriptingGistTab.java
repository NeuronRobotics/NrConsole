package com.neuronrobotics.nrconsole.plugin.scripting;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.util.ThreadUtil;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ScriptingGistTab extends Tab {
	
	private String DEFAULT_URL = "http://gist.github.com/";
	private PluginManager pm;
	private DyIO dyio;
	private ScriptingGistTab myTab;
	private TabPane tabPane = null;
	boolean loaded=false;
	boolean initialized=false;
	private WebView webView;
	private WebEngine webEngine;
	private VBox vBox;
	private Button goButton;
	private TextField urlField;
	
	
	
	public ScriptingGistTab(String title,DyIO dyio, PluginManager pm, String Url) throws IOException, InterruptedException{
		this.dyio = dyio;
		this.pm = pm;
		myTab = this;
		if(title==null)
			myTab.setText("               ");
		else
			myTab.setText(title);
		Log.debug("Loading Gist Tab: "+Url);
		webView = new WebView();
		webEngine = webView.getEngine();
		
		if(Url!=null)
			DEFAULT_URL=Url;
		webEngine.load(DEFAULT_URL);
		
		loaded=false;
		webEngine.getLoadWorker().workDoneProperty().addListener((ChangeListener<Number>) (observableValue, oldValue, newValue) -> Platform.runLater(() -> {
		    if(!(newValue.intValue()<100)){
		    	if(!initialized){
		    		initialized=true;
		    		try {
						finishLoadingComponents();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
		    	}
		    	loaded=true;
		    	
		    }else
		    	loaded=false;
		}));
		urlField = new TextField(DEFAULT_URL);
		webEngine.locationProperty().addListener((ChangeListener<String>) (observable1, oldValue, newValue) -> urlField.setText(newValue));
		goButton = new Button("Go");
		goButton.setDefaultButton(true);
	

		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<Object>() {
					public void changed(ObservableValue<?> observable,
							Object oldValue, Object newValue) {
						State oldState = (State)oldValue;
						State newState = (State)newValue;
						if (State.SUCCEEDED == newValue) {
							System.out
							.println("Success");
						}
					}
				});


		// Layout logic
		HBox hBox = new HBox(5);
		hBox.getChildren().setAll(urlField, goButton);
		HBox.setHgrow(urlField, Priority.ALWAYS);

		vBox = new VBox(5);
		vBox.getChildren().setAll(hBox, webView);
		VBox.setVgrow(webView, Priority.ALWAYS);

		myTab.setContent(vBox);
	}
	
	private void finishLoadingComponents() throws IOException, InterruptedException{
		// After loading the URL, create teh script engine
		final ScriptingEngine scripting =new ScriptingEngine(dyio, pm, null ,DEFAULT_URL, webEngine);
		
		//Action definition for the Button Go.
		EventHandler<ActionEvent> goAction = event -> {
			String addr = urlField.getText().startsWith("http://") 
					? urlField.getText() 
					: "http://" + urlField.getText();
			if(tabPane==null || addr.contains("neuronrobotics.guithub.io")){
				Log.debug("Loading "+addr);
				webEngine.load(	addr);
					
				try {
					scripting.loadCodeFromGist(addr,webEngine);	
					myTab.setText(getDomainName(urlField.getText()));
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{

				try {
					final ScriptingGistTab tab = new ScriptingGistTab(null,dyio,  pm , addr);
					final ObservableList<Tab> tabs = tabPane.getTabs();
					tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
					tabs.add(tabs.size() - 1, tab);
					tabPane.getSelectionModel().select(tab);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};

		urlField.setOnAction(goAction);
		goButton.setOnAction(goAction);

		vBox.getChildren().add(scripting);
	}
	

	public static String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    String domain = uri.getHost();
	    return domain.startsWith("www.") ? domain.substring(4) : domain;
	}


	public void setOpenInNewTab(TabPane tabPane) {
		this.tabPane = tabPane;
	}
	
}
