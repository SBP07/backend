package models;

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
	
	@ManyToMany(mappedBy = "voormiddagen", cascade = CascadeType.ALL)
	public List<Kind> voormiddagAanwezigheden = new ArrayList<Kind>();
}
