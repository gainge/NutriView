package server;

public class Error {

	/* DATA MEMBERS */
	private int status;
	private String format;
	private String message;
	
	public Error() {
		this.status = -1;
		this.format = "N/A";
		this.message = "N/A";
	}


	public int getStatus() {
		return status;
	}


	public String getFormat() {
		return format;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("Format: " + getFormat() + '\n');
		str.append("Message: " + getMessage() + '\n');
		str.append("Status: " + getStatus() + '\n');
		
		return str.toString();
	}


	
	
}
