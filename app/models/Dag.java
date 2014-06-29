package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

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
}
