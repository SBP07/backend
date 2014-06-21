package controllers;

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
		return TODO;
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
		return TODO;
	}
	
	/**
	 * Shows the details of a Kind in a nice table
	 * @param	id	The id of the Kind
	 */
	public static Result details(Long id) {
		return TODO;
	}
	
	/**
	 * Show a tabel with all the Kind'eren
	 */
	public static Result table() {
		return TODO;
	}
	
	
}
