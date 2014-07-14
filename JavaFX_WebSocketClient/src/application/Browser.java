package application;

import java.net.URL;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {
	
	final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
	public Browser() { 
		URL url = getClass().getResource("/html/test3.html");
		webEngine.load(url.toExternalForm());
		
		System.out.println("Webseite geladen");
		//add the web view to the scene
        getChildren().add(browser);	
        
        System.out.println("-->"+webEngine.documentProperty());
        utils.BrowserUtils.webDocLoadCompleted(webEngine);
	}
	
	public WebView getBrowser() {
		return browser;
	}

	public WebEngine getWebEngine() {
		return webEngine;
	}
	
	@Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 950;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 700;
    }

}
