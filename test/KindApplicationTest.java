import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.Date;

import models.Dag;
import models.Kind;

import org.junit.Test;


public class KindApplicationTest {

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
    public void unregisterAanwezigheden() {
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
		    	date3.setTime(1199286400L);

		    	Dag dag1 = new Dag();
		    	Dag dag2 = new Dag();
		    	Dag dag3 = new Dag();

		    	dag1.dag = date1;
		    	dag2.dag = date2;
		    	dag3.dag = date3;

		    	kind.registerVMAttendance(dag1);
		    	kind.registerVMAttendance(dag2);
		    	kind.registerVMAttendance(dag3);
		    	
		    	assertThat(kind.voormiddagen.size()).isEqualTo(3);
		    	
		    	kind.save();
		    	
		    	kind.unregisterVMAttendance(dag1);
		    	kind.saveManyToManyAssociations("voormiddagen");
		    	kind.update();
		    	assertThat(kind.voormiddagen.size()).isEqualTo(2);
		    	kind.registerVMAttendance(dag1);
		    	kind.saveManyToManyAssociations("voormiddagen");
		    	kind.update();
		    	assertThat(kind.voormiddagen.size()).isEqualTo(3);
		    	
		    	kind.unregisterVMAttendance(dag2);
		    	kind.update();
		    	kind.saveManyToManyAssociations("voormiddagen");

		    	assertThat(kind.voormiddagen.size()).isEqualTo(2);
		    	assertThat(kind.voormiddagen).contains(dag1);
		    	assertThat(kind.voormiddagen).contains(dag3);
		    	
			}
		});
    }
    @Test
    public void unregisterAllAanwezigheden() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
		    	Kind kind1 = new Kind();
		    	kind1.voornaam = "Milan";
		    	kind1.achternaam = "Balcaen";
		    	
		    	Kind kind2 = new Kind();
		    	kind2.voornaam = "Eenander";
		    	kind2.achternaam = "Kind";
		    	
		    	Date date1 = new Date();
		    	Date date2 = new Date();
		    	Date date3 = new Date();
		    	
		    	date1.setTime(941500800L);
		    	date2.setTime(1067731200L);
		    	date3.setTime(1199286400L);

		    	Dag dag1 = new Dag();
		    	Dag dag2 = new Dag();
		    	Dag dag3 = new Dag();

		    	dag1.dag = date1;
		    	dag2.dag = date2;
		    	dag3.dag = date3;

		    	kind1.registerVMAttendance(dag1);
		    	kind1.registerVMAttendance(dag2);
		    	kind1.registerVMAttendance(dag3);

		    	kind2.registerVMAttendance(dag1);
		    	kind2.registerVMAttendance(dag3);
		    	
		    	assertThat(kind1.voormiddagen.size()).isEqualTo(3);
		    	
		    	kind1.save();
		    	
		    	kind1.unregisterAllVMAttendances();
		    	
		    	assertThat(kind1.voormiddagen).isEmpty();
		    	assertThat(dag1.voormiddagAanwezigheden).containsOnly(kind2);
		    	assertThat(dag2.voormiddagAanwezigheden).isEmpty();
		    	
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
		    	date3.setTime(1404604800L);

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
		    	date3.setTime(1404604800L);

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
    
    @Test
    public void kindEquals() {
    	running(fakeApplication(), new Runnable() {
			public void run() {
				Kind kind1 = new Kind();
				kind1.voornaam = "Testnaam";
				
				Kind kind2 = new Kind();
				kind2.voornaam = "Testnaam"; // even though they have the same name, they shouldn't be equal

				assertThat(kind1).isNotEqualTo(kind2);
				assertThat(kind1).isEqualTo(kind1);
				assertThat(kind2).isEqualTo(kind2);
				
				kind1.save();
				kind2.save();
				
				assertThat(kind1).isNotEqualTo(kind2);
				assertThat(kind1).isEqualTo(kind1);
				assertThat(kind2).isEqualTo(kind2);
				
				Kind found = Kind.findById(kind1.id);
				assertThat(found).isNotNull();
				assertThat(found).isEqualTo(kind1);
			}
    	});
    }
}
