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
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

public class ScriptingGistTab extends Tab {
	
	private String Current_URL = "http://gist.github.com/";
	private PluginManager pm;
	private DyIO dyio;
	private ScriptingGistTab myTab;
	private TabPane tabPane = null;
	boolean loaded=false;
	boolean initialized=false;
	private WebView webView;
	private WebEngine webEngine;
	private VBox vBox;
	private Button goButton = new Button("Go");
	private Button homeButton = new Button("Home");
	private Button backButton = new Button("<");
	private Button forwardButton = new Button(">");
	
	private TextField urlField;
	//private String currentAddress;
	private ScriptingEngine scripting;
	
	
	
	public ScriptingGistTab(String title,DyIO dyio, PluginManager pm, String Url,TabPane tabPane) throws IOException, InterruptedException{
		this.dyio = dyio;
		this.pm = pm;
		this.tabPane = tabPane;
		myTab = this;
		if(pm== null)
			return;
		pm.getFrame().addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
        		//Preferred Size of TabPane.
            	Platform.runLater(()-> {
            		webView.setPrefSize(pm.getFrame().getWidth()-50, pm.getFrame().getHeight()-100);
            	});
            }
        });
		if(title==null)
			myTab.setText("               ");
		else
			myTab.setText(title);
		Log.debug("Loading Gist Tab: "+Url);
		webView = new WebView();
		webEngine = webView.getEngine();
		
		if(Url!=null)
			Current_URL=Url;
		webEngine.load(Current_URL);
		
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
		    	try {
					scripting.loadCodeFromGist(Current_URL, webEngine);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }else
		    	loaded=false;
		}));
		urlField = new TextField(Current_URL);
		webEngine.locationProperty().addListener((ChangeListener<String>) (observable1, oldValue, newValue) ->{
			urlField.setText(newValue);
			
		});
		
		goButton.setDefaultButton(true);
	
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<Object>() {
					public void changed(ObservableValue<?> observable,
							Object oldValue, Object newValue) {
						State oldState = (State)oldValue;
						State newState = (State)newValue;
						if (State.SUCCEEDED == newValue) {
							Current_URL = urlField.getText().startsWith("http://")|| urlField.getText().startsWith("https://")
									? urlField.getText() 
									: "http://" + urlField.getText();
									
							Log.debug("Navagating "+Current_URL);	
							if( processNewTab(urlField.getText())){
								goBack();
							}
						}
					}
				});
		backButton.setOnAction(arg0 -> {
			goBack();
		});
		forwardButton.setOnAction(arg0 -> {
			// TODO Auto-generated method stub
			goForward();
		});
		homeButton.setOnAction(arg0 -> {
			// TODO Auto-generated method stub
			webEngine.load(GistTabbedBrowser.getHomeUrl());
		});

		// Layout logic
		HBox hBox = new HBox(5);
		hBox.getChildren().setAll(backButton,forwardButton,homeButton,goButton,urlField);
		HBox.setHgrow(urlField, Priority.ALWAYS);

		vBox = new VBox(5);
		vBox.getChildren().setAll(hBox, webView);
		VBox.setVgrow(webView, Priority.ALWAYS);

		myTab.setContent(vBox);
	}
	
	private boolean processNewTab(String url){
		Current_URL = urlField.getText().startsWith("http://") || urlField.getText().startsWith("https://")
				? urlField.getText() 
				: "http://" + urlField.getText();
		if(tabPane!=null ){
			if(!Current_URL.contains("neuronrobotics.github.io")){
				try {
					Log.debug("Non demo page found, opening new tab "+Current_URL);
					final ScriptingGistTab tab = new ScriptingGistTab(null,dyio,  pm , Current_URL,null);
					final ObservableList<Tab> tabs = tabPane.getTabs();
					tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
					tabs.add(tabs.size() - 1, tab);
					tabPane.getSelectionModel().select(tab);
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			if(scripting!=null){
				try{
					myTab.setText(scripting.getFileName());
				}catch(java.lang.NullPointerException ex){
					try {
						scripting.loadCodeFromGist(Current_URL, webEngine);
						myTab.setText(scripting.getFileName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
		}
		return false;
	}
	
	
	
	
	
	private void finishLoadingComponents() throws IOException, InterruptedException{
		scripting = new ScriptingEngine(dyio, pm, null ,Current_URL, webEngine);
		
		//Action definition for the Button Go.
		EventHandler<ActionEvent> goAction = event -> {
			if( !processNewTab(urlField.getText())){
				Log.debug("Loading "+Current_URL);
				webEngine.load(	Current_URL);
			}
		};

		urlField.setOnAction(goAction);
		goButton.setOnAction(goAction);

		vBox.getChildren().add(scripting);
		if(tabPane==null){
			try{
				myTab.setText(scripting.getFileName());
			}catch(java.lang.NullPointerException ex){
				ex.printStackTrace();
			}
		}
	}
	
    public String goBack()
    {    
      final WebHistory history=webEngine.getHistory();
      ObservableList<WebHistory.Entry> entryList=history.getEntries();
      int currentIndex=history.getCurrentIndex();
//      Out("currentIndex = "+currentIndex);
//      Out(entryList.toString().replace("],","]\n"));

      Platform.runLater(() ->{
    	  try{
    		  history.go(-1);
    	  }catch(Exception e){}
      });
      return entryList.get(currentIndex>0?currentIndex-1:currentIndex).getUrl();
    }

    public String goForward()
    {    
      final WebHistory history=webEngine.getHistory();
      ObservableList<WebHistory.Entry> entryList=history.getEntries();
      int currentIndex=history.getCurrentIndex();
//      Out("currentIndex = "+currentIndex);
//      Out(entryList.toString().replace("],","]\n"));

      Platform.runLater(() -> history.go(1));
      return entryList.get(currentIndex<entryList.size()-1?currentIndex+1:currentIndex).getUrl();
    }
	

	public static String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    String domain = uri.getHost();
	    return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	
}
