import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Dag;

import org.junit.Test;


public class DagApplicationTest {
    
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
    public void dagEquals() throws ParseException {
    	Dag dag1 = new Dag();
    	dag1.dag = new SimpleDateFormat("dd/MM/yyyy").parse("02/11/1999");
    	
    	Dag dag2 = new Dag();
    	dag2.dag = new SimpleDateFormat("dd/MM/yyyy").parse("02/11/1999");
    	
    	Dag dag3 = new Dag();
    	dag3.dag = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");

    	Dag dag4 = new Dag();
    	Date date = new Date();
    	date.setTime(1067731200L);
    	dag4.dag = date;

    	Dag dag5 = new Dag(); // dag5.dag == null
    	Dag dag6 = new Dag(); // dag6.dag == null
    	
    	assertThat(dag5.dag).isNull();

    	assertThat(dag1).isEqualTo(dag1);
    	assertThat(dag2).isEqualTo(dag2);
    	assertThat(dag3).isEqualTo(dag3);
    	assertThat(date).isEqualTo(date);
    	assertThat(dag4).isEqualTo(dag4);
    	assertThat(dag5).isEqualTo(dag5);
    	

    	assertThat(dag1).isEqualTo(dag2);
    	assertThat(dag2).isEqualTo(dag1);
    	
    	assertThat(dag1).isNotEqualTo(dag3);
    	assertThat(dag3).isNotEqualTo(dag1);
    	
    	assertThat(dag5).isNotEqualTo(dag2);
    	assertThat(dag2).isNotEqualTo(dag5);
    	
    	assertThat(dag6).isNotEqualTo(dag5);
    	assertThat(dag5).isNotEqualTo(dag6);
    }
    
    
}
