import play.*;
import play.libs.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import models.*;

import com.avaje.ebean.*;
import com.avaje.ebean.text.StringParser;
import com.avaje.ebean.text.csv.CsvReader;

import au.com.bytecode.opencsv.CSVReader;

public class Global extends GlobalSettings {
	public void onStart(Application app) {
		System.out.println("[Global.onStart] Starting application...");
		InitialData.insert(app);
	}

	static class InitialData {
		public static void insert(Application app) {
			
			if(Ebean.find(Dag.class).findRowCount() == 0) {
				// insert some Dag'en
				insertDagen();
			}
			if (Ebean.find(Kind.class).findRowCount() == 0) {
				// insert some Kind'eren
				insertKinderen();
				insertAanwezigheden();
			}
		}

		private static void insertAanwezigheden() {
			try {
			    CSVReader reader = new CSVReader(new FileReader("conf/aanwezigheden.csv"));
			    String [] line;
			    while ((line = reader.readNext()) != null) {
			    	Long id = Long.parseLong(line[0]);
			    	
			    	Kind kind = Kind.findById(id);
			    	if(kind == null)
			    		continue;
			    	
			    	for(int i = 1; i < 6; i++)
			    	{
			    		if(line[i].equals("1")){
							int dagDeel = i + 6;
			    			Dag dag = Dag.findByDate(new SimpleDateFormat("dd/MM/yyyy").parse(dagDeel + "/04/2014"));
			    			dag.voormiddagAanwezigheden.add(kind);
			    			kind.voormiddagen.add(dag);
			    			kind.save();
			    		}
			    	}
			    }
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}

			
		}

		private static void insertDagen() {
			// Monday, April 07, 2014;Tuesday, April 08, 2014;Wednesday, April 09, 2014;Thursday, April 10, 2014;Friday, April 11, 2014
			for(int i = 0; i < 5; i++) {
				Dag dag = new Dag();
				int dagDeel = i + 7;
				try {
					dag.dag = new SimpleDateFormat("dd/MM/yyyy").parse(dagDeel + "/04/2014");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dag.save();
			}
			
		}

		private static void insertKinderen() {
			try {
				// Volgnummer van het attest;;Naam;Voornaam;Straat en
				// huisnummer;;Postcode en gemeente;Geboortedatum MDY;Monday,
				// April 07, 2014;Tuesday, April 08, 2014;Wednesday, April 09,
				// 2014;Thursday, April 10, 2014;Friday, April 11, 2014
				File f = new File("conf/kinderen.csv");

				FileReader reader = new FileReader(f);

				CsvReader<Kind> csvReader = Ebean.createCsvReader(Kind.class);

				csvReader.setPersistBatchSize(20);

				csvReader.addProperty("id");
				// ignore the next property
				csvReader.addIgnore();
				csvReader.addProperty("achternaam",
						new CapitalizeStringParser());
				csvReader.addProperty("voornaam", new CapitalizeStringParser());
				csvReader.addProperty("straatEnNummer", new CapitalizeStringParser());
				// ignore the next property
				csvReader.addIgnore();
				csvReader.addProperty("gemeente", new CapitalizeStringParser());
				csvReader.addDateTime("geboortedatum", "MM/dd/yyyy");
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

		static class CapitalizeStringParser implements StringParser {
			/**
			 * @param value
			 *            A string
			 * @return A string that begins with a capital letter
			 */
			@Override
			public Object parse(String value) {
				return capitalizeFirstLetter(value);
			}

			public static String capitalizeFirstLetter(String original) {
				if (original.length() == 0)
					return original;
				return original.substring(0, 1).toUpperCase()
						+ original.substring(1);
			}

		}
	}
}
