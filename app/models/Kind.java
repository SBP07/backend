package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.Model;


@Entity
public class Kind extends Model{
	public static Finder<Long, Kind> find = new Finder<>(Long.class, Kind.class);
	
	@Id
	public Long id;
	
	@Constraints.Required
	public String voornaam;
	@Constraints.Required
	public String achternaam;
	
	public String gsmnummer;
	public String thuistelefoon;
	
	public String straatEnNummer;
	public String gemeente;

	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date geboortedatum;
	
	public Boolean medischeFicheInOrde;
	
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date medischeFicheGecontroleerd;
	
	public static List<Kind> findAll() {
		return find.all();
	}
	
	public static Kind findById(Long id){
		return find.byId(id);
	}
	
	@Override
	public String toString() {
		return voornaam + " " + achternaam;
	}
}
