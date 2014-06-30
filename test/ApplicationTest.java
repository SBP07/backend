import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.boolex.GEventEvaluator;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import models.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    /*@Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }*/
    
    @Test
    public void saveKind() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
		    	Kind kind = new Kind();
		    	kind.voornaam = "Milan";
		    	kind.achternaam = "Balcaen";
		    	
		    	kind.save();
		    	
		    	Long id = kind.id;
		    	
		    	Kind found = Kind.findById(id);
		    	assertThat(kind.voornaam).isEqualTo(found.voornaam);
		    	assertThat(kind.achternaam).isEqualTo(found.achternaam);				
			}
		});

    }    
    @Test
    public void findByDate() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
		    	try {
		    		
					Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse("02/11/1999");
			    	Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse("27/12/2009");
			    	Date date3 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");
			    	
			    	Dag dag1 = new Dag();
			    	Dag dag2 = new Dag();
			    	Dag dag3 = new Dag();

			    	dag1.dag = date1;
			    	dag2.dag = date2;
			    	dag3.dag = date3;
			    	
			    	dag1.save();
			    	dag2.save();
			    	dag3.save();
			    	
			    	assertThat(Dag.findByDate(date1)).isEqualTo(dag1);
			    	assertThat(Dag.findByDate(date2)).isEqualTo(dag2);
			    	assertThat(Dag.findByDate(date3)).isEqualTo(dag3);
			    	
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	
		    	
			}
		});

    }
    
    @Test
    public void dagToString() throws ParseException {
    	Dag dag1 = new Dag();
    	dag1.dag = new SimpleDateFormat("dd/MM/yyyy").parse("02/11/1999");
    	assertThat(dag1.toString()).isEqualTo("02/11/1999");
    	
    	Dag dag2 = new Dag();
    	dag2.dag = new SimpleDateFormat("dd/MM/yyyy").parse("27/12/2009");
    	assertThat(dag2.toString()).isEqualTo("27/12/2009");
    	
    	Dag dag3 = new Dag();
    	dag3.dag = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");
    	assertThat(dag3.toString()).isEqualTo("01/01/2002");
    }
    
    @Test
    public void aanwezighedenKind() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
		    	Kind kind = new Kind();
		    	kind.voornaam = "Milan";
		    	kind.achternaam = "Balcaen";
		    	
		    	Date date1 = new Date();
		    	Date date2 = new Date();
		    	Date date3 = new Date();
		    	
		    	date1.setTime(941500800L);
		    	date2.setTime(1067731200L);
		    	date3.setTime(1069286400L);

		    	Dag dag1 = new Dag();
		    	Dag dag2 = new Dag();
		    	Dag dag3 = new Dag();

		    	dag1.dag = date1;
		    	dag2.dag = date2;
		    	dag3.dag = date3;

		    	kind.registerVMAttendance(dag1);
		    	kind.registerVMAttendance(dag2);
		    	kind.registerVMAttendance(dag3);
		    	
		    	assertThat(kind).isNotNull();
		    	assertThat(kind.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();

		    	kind.save();
		    	kind.saveManyToManyAssociations("voormiddagen");
		    	
		    	Long id = kind.id;
		    	
		    	Kind found = Kind.findById(id);
		    	assertThat(found).isNotNull();
		    	assertThat(found).isNotNull();
		    	assertThat(found.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();
		    	assertThat(found.voormiddagen).contains(kind.voormiddagen.get(0));
		    	assertThat(kind.achternaam).isEqualTo(found.achternaam);

		    	assertThat(kind.voormiddagen.get(0)).isEqualTo(dag1);
		    	assertThat(kind.voormiddagen.get(1)).isEqualTo(dag2);
		    	assertThat(kind.voormiddagen.get(2)).isEqualTo(dag3);

		    	assertThat(kind.voormiddagen.size()).isEqualTo(3);
			}
    		
    	});
    }
    
    @Test
    public void aanwezighedenKindCascade() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
		    	Kind kind = new Kind();
		    	kind.voornaam = "Milan";
		    	kind.achternaam = "Balcaen";
		    	
		    	Date date1 = new Date();
		    	Date date2 = new Date();
		    	Date date3 = new Date();
		    	
		    	date1.setTime(941500800L);
		    	date2.setTime(1067731200L);
		    	date3.setTime(1069286400L);

		    	Dag dag1 = new Dag();
		    	Dag dag2 = new Dag();
		    	Dag dag3 = new Dag();

		    	dag1.dag = date1;
		    	dag2.dag = date2;
		    	dag3.dag = date3;

		    	kind.registerVMAttendance(dag1);
		    	kind.registerVMAttendance(dag2);
		    	kind.registerVMAttendance(dag3);
		    	
		    	assertThat(kind).isNotNull();
		    	assertThat(kind.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();

		    	kind.save();
		    			    	
		    	Long id = kind.id;
		    	
		    	Kind found = Kind.findById(id);
		    	assertThat(found).isNotNull();
		    	assertThat(found.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();
		    	assertThat(found.voormiddagen).contains(kind.voormiddagen.get(0));
		    	assertThat(kind.achternaam).isEqualTo(found.achternaam);

		    	assertThat(kind.voormiddagen.get(0)).isEqualTo(dag1);
		    	assertThat(kind.voormiddagen.get(1)).isEqualTo(dag2);
		    	assertThat(kind.voormiddagen.get(2)).isEqualTo(dag3);

		    	assertThat(kind.voormiddagen.size()).isEqualTo(3);
			}
    		
    	});
    }

}
