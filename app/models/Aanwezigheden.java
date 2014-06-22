package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class Aanwezigheden extends Model {
	
	@Id
	public Long id;
	
	@OneToOne(mappedBy="aanwezigheden")
	public Kind kind;
	
	public List<Date> voormiddagen = new ArrayList<Date>();
	public List<Date> middagen = new ArrayList<Date>();
	public List<Date> namiddagen = new ArrayList<Date>();
	
}
