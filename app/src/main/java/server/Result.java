package server;


// Result Superclass
// Contains all the relevant data that is shared across all result objects
public class Result {

	protected Error[] errors;
	
	
	public Result() {
		// Default constructor
	}
	
	public Error[] getErrors() {
		return this.errors.clone();
	}
	
	public Error getErrorAt(int index) {
		if (index < 0 || index > this.errors.length) {
			return null;
		}
		else {
			return this.errors[index];
		}
	}
	
	public boolean hasErrors() {
		if (this.errors == null) {
			return false;
		}
		return (this.errors.length > 0);
	}
	
	public String printErrors() {
		if (!this.hasErrors()) return null;
		
		StringBuilder str = new StringBuilder();
		
		for (Error e : this.errors) {
			str.append(e.toString());
		}
		
		return str.toString();
	}

	public void addError(String errorMessage, String format) {
		Error e = new Error();
		e.setMessage(errorMessage);
		e.setFormat(format);

		// Add the error to our array?
		this.errors = new Error[1];
		this.errors[0] = e;
	}
}
