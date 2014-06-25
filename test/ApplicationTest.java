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

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }
    
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
		    	dag2.dag = date1;
		    	dag3.dag = date1;

		    	kind.voormiddagen.add(dag1);
		    	dag1.voormiddagAanwezigheden.add(kind);
		    
		    	kind.voormiddagen.add(dag2);
		    	dag2.voormiddagAanwezigheden.add(kind);

		    	kind.voormiddagen.add(dag3);
		    	dag3.voormiddagAanwezigheden.add(kind);
		    	
		    	assertThat(kind).isNotNull();
		    	assertThat(kind.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();

		    	dag1.save();
		    	dag2.save();
		    	dag3.save();
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
		    	dag2.dag = date1;
		    	dag3.dag = date1;

		    	kind.voormiddagen.add(dag1);
		    	dag1.voormiddagAanwezigheden.add(kind);
		    
		    	kind.voormiddagen.add(dag2);
		    	dag2.voormiddagAanwezigheden.add(kind);

		    	kind.voormiddagen.add(dag3);
		    	dag3.voormiddagAanwezigheden.add(kind);
		    	
		    	assertThat(kind).isNotNull();
		    	assertThat(kind.voormiddagen).isNotNull();
		    	assertThat(kind.voormiddagen.get(0)).isNotNull();

		    	/*dag1.save();
		    	dag2.save();
		    	dag3.save();*/
		    	kind.save();
		    	//kind.saveManyToManyAssociations("voormiddagen");
		    	
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
