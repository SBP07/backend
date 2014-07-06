package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.avaje.ebean.Ebean;

import models.*;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Kinderen extends Controller {
	private static final Form<Kind> kindForm = Form.form(Kind.class);
	
	/**
	 * Bind form from request and add the new Kind to the database
	 */
	public static Result save() {
		Form<Kind> boundForm = kindForm.bindFromRequest();

		if (boundForm.hasErrors()) {
			flash("error", "Niet juist ingevuld. Probeer opnieuw.");
			return badRequest(nieuwkind.render(boundForm));
		}

		Kind kind = boundForm.get();
		if (kind.id != null) {
			kind.update();
			flash("success", String.format("Kind %s is geüpdated.", kind));
		} else {
			kind.save();
			flash("success", String.format("Kind %s is toegevoegd.", kind));
		}

		return redirect(routes.Kinderen.details(kind.id));
	}
	
	/**
	 * Show form to create a new Kind
	 */
	public static Result nieuw() {
		return ok(nieuwkind.render(kindForm));
	}
	
	/**
	 * Show (filled) form to edit an existing Kind
	 * @param	id	The id of the Kind
	 */
	public static Result edit(Long id) {
		Kind kind = Kind.findById(id);
		if (kind == null)
			return notFound("Not Found");
		return ok(nieuwkind.render(kindForm.fill(kind)));
	}
	
	/**
	 * Shows the details of a Kind in a nice table
	 * @param	id	The id of the Kind
	 */
	public static Result details(Long id) {
		Kind kind = Kind.findById(id);
		if(kind == null) return notFound("Not Found");
		return ok(kinddetails.render(kind));
	}
	
	/**
	 * Show a table with all the Kind'eren
	 */
	public static Result list() {
		List<Kind> kinderen = Kind.findAll();
		return ok(tabelkind.render(kinderen));
	}
	
	/**
	 * Show a form to record attendances
	 */
	public static Result inschrijfForm(Long id) {
		Kind kind = Kind.findById(id);
		if (kind == null)
			return notFound("Not Found");
		return ok(inschrijven.render(Dag.findAll(), kindForm.fill(kind), kind));
	}
	
	/**
	 * Register attendances for a Kind
	 */
	public static Result registreerAanwezigheden() {
		
		Form<Kind> boundForm = kindForm.bindFromRequest();

		if (boundForm.hasErrors()) {
			flash("error", "Niet juist ingevuld. Probeer opnieuw.");
				
			Kind kind = Kind.findById(Long.parseLong(boundForm.field("id").value()));
			if(kind == null)
				return redirect(routes.Kinderen.list());
			return badRequest(inschrijven.render(Dag.findAll(), boundForm, kind));
		}

		Kind boundKind = boundForm.get(); // this is the bound form
		Kind kind = Kind.findById(boundKind.id); // this is the Kind we want to edit, we check them out of the database and now we're going to set their attendances
		
		if(kind == null)
			return notFound("Not Found");

		kind.unregisterAllVMAttendances(); // unregister all attendances, we'll add the new ones from the bound form now
		
		List<Dag> lijst = boundKind.voormiddagen;
		Iterator<Dag> it = lijst.iterator();
		
		while(it.hasNext()){
			Dag boundDag = it.next();
			if(boundDag.id != null) {
				Dag dag = Dag.findById(boundDag.id);
				if(dag == null)
					continue;
				kind.registerVMAttendance(dag);
			}
		}
		
		kind.update();
		flash("success", String.format("Aanwezigheden van %s zijn geüpdated.", kind));
		return redirect(routes.Kinderen.details(kind.id));
	}
	
}
