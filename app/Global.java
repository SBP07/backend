import play.*;
import play.libs.*;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import models.*;

import com.avaje.ebean.*;
import com.avaje.ebean.text.csv.CsvReader;

public class Global extends GlobalSettings {
	public void onStart(Application app) {

		InitialData.insert(app);
	}

	static class InitialData {
		public static void insert(Application app) {
			if (Ebean.find(Kind.class).findRowCount() == 0) {
				// insert some Kind'eren
				insertKinderen();

			}
		}

		private static void insertKinderen() {
			 try {
				// Volgnummer van het attest;;Naam;Voornaam;Straat en huisnummer;;Postcode en gemeente;Geboortedatum MDY;Monday, April 07, 2014;Tuesday, April 08, 2014;Wednesday, April 09, 2014;Thursday, April 10, 2014;Friday, April 11, 2014
			     File f = new File("conf/kinderen.csv");
			 
			     FileReader reader = new FileReader(f);
			 
			     CsvReader<Kind> csvReader = Ebean.createCsvReader(Kind.class);
			 
			     csvReader.setPersistBatchSize(20);
			     
			     csvReader.addProperty("id");
			     // ignore the next property
			     csvReader.addIgnore();
			     csvReader.addProperty("achternaam");
			     csvReader.addProperty("voornaam");
			     csvReader.addProperty("straatEnNummer");
			     // ignore the next property
			     csvReader.addIgnore();
			     csvReader.addProperty("gemeente");
			     csvReader.addDateTime("geboortedatum", "dd/MM/yyyy");
			     csvReader.addIgnore();
			     csvReader.addIgnore();
			     csvReader.addIgnore();
			     csvReader.addIgnore();
			     csvReader.addIgnore();
			 
			     csvReader.process(reader);
			 
			 } catch (Exception e) {
			     throw new RuntimeException(e);
			 }
		}
	}
}
