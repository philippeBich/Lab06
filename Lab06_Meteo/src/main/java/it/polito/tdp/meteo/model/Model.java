package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;


public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> leCitta;
	private List<Citta> best;
	
	/**
	 * Tutte le città  presenti nel database. La lista viene letta al momento della
	 * costruzione del Model.
	 * 
	 * @return lista delle città  presenti
	 */
	public List<Citta> getLeCitta() {
		return leCitta;
	}

	public Model() {
		MeteoDAO dao = new MeteoDAO();
		this.leCitta = dao.getAllCitta();
	}

	
	/**
	 * Il metodo restituisce una stringa contenente l'umidità media per ogni città per il mese desiderato
	 * @param mese - mese desiderato
	 * @return 
	 */
	public String getUmiditaMedia(int mese) {
		MeteoDAO dao = new MeteoDAO();
		String result="";
		
		for(Citta c: leCitta) {
			Double u = dao.getAvgRilevamentiLocalitaMese(mese, c.getNome()) ;
			result+=String.format("Umidità media a %s:  %.2f\n", c.getNome(), u);
		}
		return result;
	}
	
	
	
	/**
	 * Il metodo prepara la ricorsione e fornisce la sequenza migliore (e valida) 
	 * di città visitabili nel mese desiderato.
	 * @param mese - mese desiderato
	 * @return - una lista contenente la sequenza di città da visitare giorno per giorno
	 */
	public List<Citta> trovaSequenza(int mese) {
		List<Citta> parziale = new ArrayList<>();
		this.best = null;

		MeteoDAO dao = new MeteoDAO();

		for (Citta c : leCitta) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		cerca(parziale, 0);
		return best;
	}
	
	
	
	/**
	 * Procedura ricorsiva per il calcolo delle città  ottimali.
	 * 
	 * @param parziale
	 *            soluzione parziale in via di costruzione
	 * @param livello
	 *            livello della ricorsione, cioè il giorno a cui si sta cercando di
	 *            definire la città 
	 */
	private void cerca(List<Citta> parziale, int livello) {

		if (livello == NUMERO_GIORNI_TOTALI) {
			Double costo = calcolaCosto(parziale);
			
			if (best == null || costo < calcolaCosto(best)) {
				best = new ArrayList<>(parziale);
			}
			
			return;
		}


		for (Citta prova : leCitta) {

			if (aggiuntaValida(prova, parziale)) {

				parziale.add(prova);
				cerca(parziale, livello + 1);
				parziale.remove(parziale.size() - 1);

			}
		}

	}
	
	
	/**
	 * Calcola il costo di una determinata soluzione (totale)
	 * 
	 * <p>
	 * Attenzione: questa funzione assume che i dati siano <b>tutti</b> presenti nel
	 * database, ma nel nostro esempio ciò non accade (in alcuni giorni il dato è
	 * mancante, per cui il risultato calcolato sarà  errato).
	 * 
	 * @param parziale
	 *            la soluzione (totale) proposta
	 * @return il valore del costo, che tiene conto delle umidità  nei 15 giorni e
	 *         del costo di cambio città
	 */
	
	private Double calcolaCosto(List<Citta> parziale) {
		double costo = 0.0;
		
		for (int giorno = 1; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			Citta c = parziale.get(giorno - 1);
			double umid = c.getRilevamenti().get(giorno - 1).getUmidita();
			costo += umid;
		}

		// a cui sommo 100 * numero di volte in cui cambio città 
		for (int giorno = 2; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			if (!parziale.get(giorno - 1).equals(parziale.get(giorno - 2))) {
				costo += COST;
			}
		}

		return costo;
	}
	
	
	
	
	
	
	/**
	 * Verifica se, data la soluzione {@code parziale} già  definita, sia lecito
	 * aggiungere la città  {@code prova}, rispettando i vincoli sui numeri giorni
	 * minimi e massimi di permanenza.
	 * 
	 * @param prova
	 *            la città  che sto cercando di aggiungere
	 * @param parziale
	 *            la sequenza di città  già  composta
	 * @return {@code true} se {@code prova} è lecita, {@code false} se invece viola
	 *         qualche vincolo (e quindi non è lecita)
	 */
	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {

		int conta = 0;
		
		for (Citta precedente : parziale) {
			if (precedente.equals(prova)) conta++;
		}
		
		if (conta >= NUMERO_GIORNI_CITTA_MAX) return false;

		
		// Almeno tre giorni consecutivi
		if (parziale.size() == 0) return true;
		
		if (parziale.size() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) { 
			return parziale.get(parziale.size() - 1).equals(prova);
		}
		
		//size >= NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN
		
		if (parziale.get(parziale.size() - 1).equals(prova)) return true;
		
		if (parziale.get(parziale.size() - 1).equals(parziale.get(parziale.size() - 2))
				&& parziale.get(parziale.size() - 2).equals(parziale.get(parziale.size() - 3)))
			return true;

		return false;
	}
	

}
