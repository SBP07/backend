package models;

import java.util.*;

import javax.persistence.*;

import play.Logger;
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

	/**
	 * Use id to generate the hashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Use id to check equality
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kind other = (Kind) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Register an attendance for a Kind in the morning
	 * @param dag The day to register attendance for
	 */
	public void registerVMAttendance(Dag dag) {
		if(!this.voormiddagen.contains(dag))
			this.voormiddagen.add(dag);
		
		if(!dag.voormiddagAanwezigheden.contains(this))
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
	
	/**
	 * Remove all morning attendances
	 * This will also remove them from each Dag.voormiddagen
	 */
	public void unregisterAllVMAttendances(){
		// iterate through all voormiddagen
		Iterator<Dag> it1 = this.voormiddagen.iterator();
		while(it1.hasNext()){
			Dag vmDag = it1.next();
			
			// iterate through all attendances on this day and remove this Kind
			Iterator<Kind> it2 = vmDag.voormiddagAanwezigheden.iterator();
			while(it2.hasNext()){
				Kind kind = it2.next();
				if(kind.equals(this)) {
					it2.remove();
				}
			}
			it1.remove();
		}
	}
}
