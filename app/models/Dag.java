package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.Model;
import models.*;

@Entity
public class Dag extends Model {
	public static Finder<Long, Dag> find = new Finder<>(Long.class, Dag.class);

	@Id
	public Long id;
	
	public Date dag;
	
	@ManyToMany(mappedBy = "voormiddagen", cascade = CascadeType.ALL)
	public List<Kind> voormiddagAanwezigheden = new ArrayList<Kind>();
	
	@Override
	public String toString(){
		if(dag == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(dag);
	}
	
	public static List<Dag> findAll() {
		return find.all();
	}
	
	public static Dag findByDate(Date date) {
		return find.where().eq("dag", date).findUnique();
	}
	
	public static Dag findById(Long id){
		return find.byId(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dag == null) ? 0 : dag.hashCode());
		return result;
	}
	
	public boolean equals(Object obj){
		if(obj == null)
			return false;
		
		if(obj instanceof Dag) {
			if(this.dag == null && ((Dag)obj).dag == null)
				return true;
			
			if(((Dag)obj).dag == null || this.dag == null)
				return false;
			
			Calendar otherCal = new GregorianCalendar();
			Calendar thisCal = new GregorianCalendar();
			otherCal.setTime( ((Dag)obj).dag );
			thisCal.setTime(this.dag);
			
			thisCal.set(Calendar.HOUR_OF_DAY, 0);
			thisCal.set(Calendar.MINUTE, 0);
			thisCal.set(Calendar.SECOND, 0);
			thisCal.set(Calendar.MILLISECOND, 0);
			long thisTime = thisCal.getTimeInMillis();
			
			otherCal.set(Calendar.HOUR_OF_DAY, 0);
			otherCal.set(Calendar.MINUTE, 0);
			otherCal.set(Calendar.SECOND, 0);
			otherCal.set(Calendar.MILLISECOND, 0);
			long otherTime = otherCal.getTimeInMillis();

			return otherTime == thisTime;
		} else {
			return false;
		}
	}
}
