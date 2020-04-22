/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import it.polito.tdp.meteo.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;


public class FXMLController {
	private Model model;
	

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Integer> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;
	
	
	/**
	 * Il metodo permette di visualizzare la sequenza di città ottima.
	 * @param event
	 */

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	int count=1;
    	txtResult.clear();
    	
		int m = boxMese.getValue() ;
		
		List<Citta> best = model.trovaSequenza(m) ;
		txtResult.appendText("Sequenza ottima per il mese " +m+":\n");
		for (Citta s: best) {
			txtResult.appendText("GIORNO " + count+":  " + s + "\n");
			count++;
		}
		


    }
    
    /**
     * Il metodo permette di visualizzare l'umidità media per ogni città.
     * @param event
     */

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	txtResult.clear();
    	int mese = boxMese.getValue();
		txtResult.appendText(model.getUmiditaMedia(mese));
    }
    
    
   	
	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model=model;
	}
	

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
		
		// popola la boxMese con i 12 mesi dell'anno
		for(int mese = 1; mese <= 12 ; mese ++)
			boxMese.getItems().add(mese) ;
		
		boxMese.setValue(1);
	}
}

