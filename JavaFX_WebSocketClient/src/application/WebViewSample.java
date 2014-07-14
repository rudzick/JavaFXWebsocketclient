package application;

import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.w3c.dom.Element;

import wsclient.WebSocketClientRunner;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import java.net.URI;
import java.net.URISyntaxException;



public class WebViewSample extends Application {
	private Scene scene;
	final Browser browser = new Browser();
	private LonLat lonlat = new LonLat();
	 
	
	@Override
	public void start(Stage stage) throws Exception {
		
		URI uri;
		try {
			uri = new URI("ws://www.rudzick.com:8080/Pr_JSFWebsocket/echo");
			WebSocketClientRunner websocketclientrunner = new WebSocketClientRunner(uri,browser);
			lonlat.addObserver(websocketclientrunner);
			websocketclientrunner.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// create scene
        stage.setTitle("OpenLayers Beispiel");
        
        BorderPane root = new BorderPane();
        scene = new Scene(root, 1024, 968, Color.web("#666970"));


        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button update_btn = new Button("Zurücksetzen");
        Button update_btn2 = new Button("Zentriere auf Position");
        
        Label labellat = new Label("Breite:");
        labellat.setTextFill(Color.web("#eeeeee"));
        Label labellon = new Label("Länge:");
        labellon.setTextFill(Color.web("#eeeeee"));
        Label labelzoom = new Label("zoom:");
        labelzoom.setTextFill(Color.web("#eeeeee"));
        final TextField tflat = new TextField ("52.5234051");
        final TextField tflon = new TextField ("13.4113999");
        final TextField tfzoom = new TextField ("11");
                
        hbox.getChildren().addAll(update_btn, labellat, tflat, labellon, tflon, labelzoom, tfzoom, update_btn2);

        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(15, 12, 15, 12));
        hbox2.setSpacing(10);
        hbox2.setMaxHeight(40);
        hbox2.setStyle("-fx-background-color: #336699;");
        Label labeladdress = new Label("Adresse:");
        labeladdress.setTextFill(Color.web("#eeeeee"));
        final TextField tfaddress = new TextField ("Willy-Brandt-Straße 1, Berlin");
        hbox2.setAlignment(Pos.TOP_CENTER);
        Button address_btn = new Button("Los!");
        
        hbox2.getChildren().addAll(labeladdress, tfaddress, address_btn);
        
		root.setTop(hbox);
		root.setCenter(hbox2);
		root.setBottom(browser);
		
		  
		// Reset-Button
		update_btn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent arg0) {

				Element p = (Element) browser.getWebEngine()
						.executeScript("document.getElementById('ueberschr')");
				System.out.println(p);
				browser.getWebEngine().executeScript("reset()");

			}
		});
		
		// Zentriere auf LonLat
		update_btn2.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent arg0) {
				String m_osmzoom = "11";
				String m_longitude = "13.4113999";
				String m_latitude = "52.5234051";
				
				String lat = m_latitude;
				String lon = m_longitude;
				String zoom = m_osmzoom;
						
				lat = evalTextField(tflat);
				lon = evalTextField(tflon);
				zoom = evalTextField(tfzoom);
				
			    System.out.println(lat);
				System.out.println(lon);
				System.out.println(zoom);
				
				browser.getWebEngine().executeScript("jumpTo("+ lon +","+ lat + "," + zoom + ")");			
				
			}
			
			
		});
		
		// Button für Google Geocoder
		address_btn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent arg0) {
				
				
				
				String m_osmzoom = "11";
				String m_longitude = "13.4113999";
				String m_latitude = "52.5234051";
				
				String lat = m_latitude;
				String lon = m_longitude;
				String zoom = m_osmzoom;
						
				String address ="";
				
				zoom = evalTextField(tfzoom);
				address = evalTextField(tfaddress);
				
				
				
				final Geocoder geocoder = new Geocoder();

				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("de").getGeocoderRequest();
				GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
				List<GeocoderResult> results = geocoderResponse.getResults();
				lat = results.get(0).getGeometry().getLocation().getLat().toString();
				lon = results.get(0).getGeometry().getLocation().getLng().toString();
				
				zoom = "17";
				
				System.out.println(lat);
				System.out.println(lon);
				System.out.println(zoom);
				
				lonlat.setLonLat(lon,lat);
			}
			
			
			
			
		});
        
        stage.setScene(scene);
      
        stage.show();
        
        
		
	}
	
	
	private String evalTextField(TextField tf) {
		String val ="";
		if ((tf.getText() != null && !tf.getText().isEmpty())) {
            val = tf.getText();
        } else {
            tf.setText("Bitte gültigen Wert eingeben.");
        }
		return val;
	}
	
	public static void main(String[] args){
		// UTF8
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("client.encoding.override", "UTF-8");
		
        launch(args);
    }
	
	

}
