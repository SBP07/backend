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
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Dag> voormiddagen = new ArrayList<Dag>();
	
	@ManyToMany(cascade=CascadeType.ALL)
	public List<Dag> middagen = new ArrayList<Dag>();
	
	@ManyToMany(cascade=CascadeType.ALL)
	public List<Dag> namiddagen = new ArrayList<Dag>();
	
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
	
	@Override
	public void save() {
		super.save();
		super.saveManyToManyAssociations("voormiddagen");
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Kind) {
			Kind other = (Kind)obj;
			if(this.id == null || other.id == null)
				// even if this.id == other.id == null, the two Kind objects are possibly not equal
				// we'll let Object.equals(...) handle it, that will return true if they point to the same space in memory
				return super.equals(other);

			return this.id.equals(other.id);
		} else {
			return false;
		}
	}
	
	/**
	 * Register an attendance for a Kind in the morning
	 * @param dag The day to register attendance for
	 */
	public void registerVMAttendance(Dag dag) {
		this.voormiddagen.add(dag);
		dag.voormiddagAanwezigheden.add(this);
	}
	
	/**
	 * Register an attendance for a Kind in the morning
	 * @param dag The day to register attendance for
	 */
	public void unregisterVMAttendance(Dag dag) {
		Iterator<Dag> it1 = this.voormiddagen.iterator();
		while(it1.hasNext()){
			Dag vmDag = it1.next();
			if(vmDag.equals(dag)) {
				it1.remove();
			}
		}
		
		Iterator<Kind> it2 = dag.voormiddagAanwezigheden.iterator();
		while(it2.hasNext()){
			Kind kind = it2.next();
			if(kind.equals(this)) {
				it2.remove();
			}
		}
	}
}
