import java.util.ArrayList;
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


}
