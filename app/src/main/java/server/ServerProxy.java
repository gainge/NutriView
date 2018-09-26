package server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.Gson;

public class ServerProxy {

	private static Gson gson = new Gson();

	public static final ServerProxy SINGLETON = new ServerProxy();


	public ServerProxy() {
		// Default constructor
	}


	/* API CALL METHODS */

	/**
	 * Search Function
	 * ---------------
	 * Performs a search api call using the parameters and URL given by the request object
	 * @param request	The object containing all the request data.  Generates the URL
	 * @return	A SearchResult object containing all the data returned from the api call.
	 */
	public SearchResult search(SearchRequest request) {
		HttpURLConnection connection = openConnection(request.getURL());
		return (SearchResult) parseConnectionResponse(connection, SearchResult.class);
	}


	/**
	 * List Function
	 * -------------
	 * @param request	The object containing the request data.  Generates the URL
	 * @return	A ListResult Object containing all the result data!
	 */
	public ListResult list(ListRequest request) {
		HttpURLConnection connection = openConnection(request.getURL());
		return (ListResult) parseConnectionResponse(connection, ListResult.class);
	}


	/**
	 * NutrientReport Function
	 * -----------------------------
	 * @param request	The object containing the request data.  Generates the URL
	 * @return	A NutrientReportResult object containing the result data
	 */
	public NutrientReportResult nutrientReport(NutrientReportRequest request) {
		HttpURLConnection connection = openConnection(request.getURL());
		return (NutrientReportResult) parseConnectionResponse(connection, NutrientReportResult.class);
	}

	// Todo: Food report result stuff



	// Actual server stuff
	private HttpURLConnection openConnection(URL url) {
		System.out.println(url.toString());	// For debugging url problems
		HttpURLConnection result = null;

		try {
			result = (HttpURLConnection)url.openConnection();

			// Take care of some awesome stuff
			result.setRequestMethod(HTTP_GET);
			result.setDoOutput(true);

			// "Try to connect that puppy!"
			result.connect();

		} catch (MalformedURLException e) {
			System.out.println("Malformed URL Exception!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception in Open Connection!");
			e.printStackTrace();
		}

		return result;
	}


	private Object parseConnectionResponse(HttpURLConnection connection, Class objectClass) {
		Object result = null;

		try {
			InputStream isStream = connection.getInputStream();
			InputStreamReader isReader = new InputStreamReader(isStream);

			String jsonText = "Nothing read yet";

			try {
				// We need to do this manually, I guess
				StringBuilder str = new StringBuilder();

				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String line;
				while ((line = br.readLine()) != null) {
					str.append(line);
				}

				jsonText = str.toString();

				System.out.println("This is what we got back from the server!");
				System.out.println(jsonText);

				result = gson.fromJson(jsonText, objectClass);

			} catch (NumberFormatException e) {
				System.out.println("NUMBER FORMAT EXCEPTION!!!");
				String evilSequence = "\"--\"";
				int index = jsonText.indexOf(evilSequence);

				while (index != -1) {
					// Clean up the string, replace the crap with valid values
					jsonText = jsonText.replaceFirst(evilSequence, "\"0.00\"");		// Replace the value
					jsonText = jsonText.replaceFirst(evilSequence, "0.0");			// Replace the general measure

					index = jsonText.indexOf(evilSequence);
				}

				// Now make the result an actual cool, useable thing
				result = gson.fromJson(jsonText, objectClass);
			}

			isReader.close();

		} catch (IOException e) {
			System.out.println("IO Excpeption in Parse Connection Response!");
			e.printStackTrace();
		}  finally {
			connection.disconnect();
		}

		return result;
	}



	/* CONSTANTS */
	private static final String HTTP_GET = "GET";

}
