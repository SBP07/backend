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
	
	@Id
	public Date dag;
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Kind> voormiddagAanwezigheden = new ArrayList<Kind>();
	
	@Override
	public String toString(){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(dag);
	}
}