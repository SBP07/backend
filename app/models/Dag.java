package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.*;

import play.db.ebean.Model;
import models.*;

@Entity
public class Dag extends Model {
	@Id
	public Long id;
	
	public Date dag;
	
	@ManyToMany(mappedBy="voormiddagen", cascade=CascadeType.ALL)
	public ArrayList<Kind> voormiddagAanwezigheden = new ArrayList<Kind>();
}
