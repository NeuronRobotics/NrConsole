package com.neuronrobotics.nrconsole.plugin.scripting;

import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.dyio.DyIO;

public class GistTabbedBrowser extends JFXPanel implements DefaultURL{

	private static final String HOME_URL = "http://neuronrobotics.github.io/Java-Code-Library/Digital-Input-Example-Simple/";
	/**
	 * 
	 */
	private static final long serialVersionUID = -2686618188618431477L;
	private DyIO dyIO;
	private PluginManager pm;
	private TabPane tabPane;


	public GistTabbedBrowser(DyIO dyIO, PluginManager pm) {
		this.dyIO = dyIO;
		this.pm = pm;
		if(pm==null)
			return;
		Platform.runLater(()-> {
				initFX(this);
				pm.getFrame().addComponentListener(new java.awt.event.ComponentAdapter() {
		            public void componentResized(java.awt.event.ComponentEvent e) {
		        		//Preferred Size of TabPane.
		            	Platform.runLater(()-> {
		            		tabPane.setPrefSize(pm.getFrame().getWidth()-50, pm.getFrame().getHeight()-100);
		            	});
		            }
		        });
		});
	}


	private void initFX(JFXPanel fxPanel) {
		// This method is invoked on JavaFX thread
		Scene scene = createScene();
		fxPanel.setScene(scene);
	}
	
	//Custom function for creation of New Tabs.
	private Tab createFileTab(final TabPane tabPane, final String title) {

		Tab tab = new Tab(title);
		Label aboutLabel = new Label();
		aboutLabel.setText("\n\n\t\t365: A Program for a day.\n\n\t\t\tWelcome to JavaFX Custom Browser. " +
				"\n\t\t\tThis is a custom browser created for demo purpose only." +
		"\n\t\t\tTo start browsing, click on New Tab.");
		aboutLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		tab.setContent(aboutLabel);

		final ObservableList<Tab> tabs = tabPane.getTabs();
		//tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
		tabs.add(tabs.size() - 1, tab);
		tabPane.getSelectionModel().select(tab);
		
		tab.setClosable(false);
		return tab;
	}

	//Custom function for creation of New Tabs.
	private Tab createAndSelectNewTab(final TabPane tabPane, final String title) {

		ScriptingGistTab tab;
		try {
			tab = new ScriptingGistTab(title,dyIO,  pm , getHomeUrl());
			final ObservableList<Tab> tabs = tabPane.getTabs();
			//tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
			tabs.add(tabs.size() - 1, tab);
			tabPane.getSelectionModel().select(tab);
			tab.setOpenInNewTab(tabPane);
			tab.setClosable(false);
			return tab;
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	private Tab createTab() throws IOException, InterruptedException{
		final ScriptingGistTab tab = new ScriptingGistTab(null,dyIO,  pm , null);

		return tab;
	}


	public Scene createScene() {

		final Group root = new Group();

		BorderPane borderPane = new BorderPane();
		tabPane = new TabPane();

		//Preferred Size of TabPane.
		tabPane.setPrefSize(1365, 1024);

		//Placement of TabPane.
		tabPane.setSide(Side.TOP);

		/* To disable closing of tabs.
		 * tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);*/

		final Tab newtab = new Tab();
		newtab.setText("New Gist");
		newtab.setClosable(false);

		//Addition of New Tab to the tabpane.
		tabPane.getTabs().addAll(newtab);

		createAndSelectNewTab(tabPane, "About NrConsole");

		//Function to add and display new tabs with default URL display.
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable,
					Tab oldSelectedTab, Tab newSelectedTab) {
				if (newSelectedTab == newtab) {

					try {
						final Tab tab = createTab();
						final ObservableList<Tab> tabs = tabPane.getTabs();
						tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
						tabs.add(tabs.size() - 1, tab);
						tabPane.getSelectionModel().select(tab);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	


				}
			}
		});

		borderPane.setCenter(tabPane);
		
		
		root.getChildren().add(borderPane);


		return new Scene(root);
	}


	public static String getHomeUrl() {
		return HOME_URL;
	}

}