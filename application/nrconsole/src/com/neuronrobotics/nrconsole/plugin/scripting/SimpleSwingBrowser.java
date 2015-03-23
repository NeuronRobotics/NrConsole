package com.neuronrobotics.nrconsole.plugin.scripting;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import static javafx.concurrent.Worker.State.FAILED;


public class SimpleSwingBrowser extends JPanel {
	private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JLabel lblStatus = new JLabel();


    private final JButton btnGo = new JButton("Go");
    private final JButton forward = new JButton("Fwd");
    private final JButton back = new JButton("Back");
    private final JButton btnHome = new JButton("Home");
    private final JButton btnNewGist = new JButton("New Gist");
    private final JTextField txtURL = new JTextField(100);
    private final JProgressBar progressBar = new JProgressBar();
    private WebView view;
    
    public SimpleSwingBrowser() {
        super();
        setLayout(new MigLayout());
        initComponents();
    }

    
    private void initComponents() {
        createScene();
 
        ActionListener al = new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                loadURL(txtURL.getText());
            }
        };
 
        btnGo.addActionListener(al);
        txtURL.addActionListener(al);
        btnHome.addActionListener(e -> {
        	loadURL("http://neuronrobotics.github.io/Java-Code-Library/Digital-Input-Example-Simple/");
		});
        btnNewGist.addActionListener(e -> {
        	loadURL("https://gist.github.com/");
  		});
        back.addActionListener(e -> {
        	goBack();
  		});
        forward.addActionListener(e -> {
        	goForward();
  		});
        
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
  
        JPanel topBar = new JPanel(new MigLayout());
        topBar.add(back);
        topBar.add(forward);
        topBar.add(btnHome);
        topBar.add(btnNewGist);
        topBar.add(txtURL );
        topBar.add(btnGo);
 
        JPanel statusBar = new JPanel(new MigLayout());
        statusBar.add(progressBar);
        statusBar.add(lblStatus);
        
 
        add(topBar, "wrap");
        add(jfxPanel, "wrap");
        add(statusBar, "wrap");
        

//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        pack();

    }
 
    private void createScene() {
 
        Platform.runLater(new Runnable() {


			@Override 
            public void run() {
 
                view = new WebView();
                engine = view.getEngine();
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                SimpleSwingBrowser.this.setName(newValue);
                            }
                        });
                    }
                });
 
                engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override 
                    public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });
 
                engine.locationProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                txtURL.setText(newValue);
                            }
                        });
                    }
                });
 
                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
                                progressBar.setValue(newValue.intValue());
                            }
                        });
                    }
                });

                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {
 
                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override public void run() {
                                            JOptionPane.showMessageDialog(
                                                    null,
                                                    (value != null) ?
                                                    engine.getLocation() + "\n" + value.getMessage() :
                                                    engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });
                
                
                Platform.runLater(() -> {
                	view.setPrefWidth(1168);
                	view.setPrefHeight(768);
                	jfxPanel.setScene(new Scene(view));
//                	view.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//                        @Override
//                        public void handle(MouseEvent mouse) {
//
//                        }
//                    });
				});
                
            }
        });
    }
    public String goBack()
    {    
      final WebHistory history=engine.getHistory();
      ObservableList<WebHistory.Entry> entryList=history.getEntries();
      int currentIndex=history.getCurrentIndex();
//      Out("currentIndex = "+currentIndex);
//      Out(entryList.toString().replace("],","]\n"));

      Platform.runLater(new Runnable() { public void run() { history.go(-1); } });
      return entryList.get(currentIndex>0?currentIndex-1:currentIndex).getUrl();
    }

    public String goForward()
    {    
      final WebHistory history=engine.getHistory();
      ObservableList<WebHistory.Entry> entryList=history.getEntries();
      int currentIndex=history.getCurrentIndex();
//      Out("currentIndex = "+currentIndex);
//      Out(entryList.toString().replace("],","]\n"));

      Platform.runLater(new Runnable() { public void run() { history.go(1); } });
      return entryList.get(currentIndex<entryList.size()-1?currentIndex+1:currentIndex).getUrl();
    }
    public void loadHTML(final String html) {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                engine.loadContent(html);
                txtURL.setText("");

            }
        });
    }
    
    public URL getCurrentURL(){
    	try {
			return new URL(txtURL.getText());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			return new URL("gist.github.com");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                String tmp = toURL(url);
 
                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }
 
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toString();
        } catch (MalformedURLException exception) {
                return null;
        }
    }
    
    public String urlToGist(String in) {
		String domain = in.split("//")[1];
		String [] tokens = domain.split("/");
		if (tokens[0].toLowerCase().contains("gist.github.com") && tokens.length>=2){
			String id = tokens[2].split("#")[0];
			System.out.println("Gist URL Detected "+id);
			return id;
		}
		
		return null;
	}
    private String returnFirstGist(String html){
    	//System.out.println(html);
    	String slug = html.split("//gist.github.com/")[1];
    	String js=		slug.split(".js")[0];
    	String id  = js.split("/")[1];
    	
    	return id;
    }

	public String getCurrentGist() {
		String gist = urlToGist(txtURL.getText());
		if (gist==null){
		
			try {
				System.out.println("Non Gist URL Detected");
				String html;
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				StringWriter sw = new StringWriter();
				t.transform(new DOMSource(engine.getDocument()), new StreamResult(sw));
				html = sw.getBuffer().toString();
				return returnFirstGist(html);
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return gist;
	}

  
}
