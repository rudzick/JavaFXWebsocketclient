package utils;

import org.w3c.dom.Document;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

public class BrowserUtils {

	
	public static void webDocLoadCompleted(final WebEngine webEngine)  {
		webEngine.getLoadWorker().stateProperty()
		.addListener(new ChangeListener<State>() {
			public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, State oldState, State newState) {

				if (newState == Worker.State.SUCCEEDED) {

					System.out.println("called");
					Document doc = webEngine.getDocument();
					System.out.println(doc);
				}

		}
	});
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

}
