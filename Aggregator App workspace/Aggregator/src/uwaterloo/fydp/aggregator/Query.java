/**
 * Sends HTTP request to server. The server responds with the listings which are then
 * stored into the local database. Uses another thread to send request and perform 
 * database IO.
 */

package uwaterloo.fydp.aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import uwaterloo.fydp.aggregator.data.ListingsTable;
import android.content.Context;
import android.util.Log;

public class Query implements Runnable {
	private final String baseUrl = "http://107.21.86.40/index.php";
	//private final String url = "http://107.21.86.40/index.php?action=getListings&search=&cat=1&lat=50&long=50";
	
	private final Context mContext;
	private final String mSearchPhrase;
	private final int mCategory;
	private final double mLatitude;
	private final double mLongitude;
	private final int mSearchRadius;
	
	/**
	 * Constructor
	 * @param context Context of the calling activity. Used for database operations.
	 * @param searchPhrase The search phrase to be used for the query.
	 * @param category The category to limit the query to.
	 * @param latitude The latitude around which the query is centered.
	 * @param longitude The longitude around which the query is centered.
	 * @param searchRadius The radius within which to restrict the query.
	 */
	public Query(Context context, String searchPhrase, int category, double latitude, double longitude, int searchRadius) {
		mContext = context;
		mSearchPhrase = searchPhrase;
		mCategory = category;
		mLatitude = latitude;
		mLongitude = longitude;
		mSearchRadius = searchRadius;
	}

	/**
	 * Send HTTP request to server and process the server response.
	 */
	@Override
	public void run() {				
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(getURI());
			HttpResponse response = httpClient.execute(request);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			
			String responseString = reader.readLine();			
			if (responseString != null) {
				List<Listing> listings = parseJSONResponse(responseString);
				updateDatabase(listings);
			}
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "run(): ", e);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "run(): ", e);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "parseJSONResponse(): ", e);
		} catch (URISyntaxException e) {
			Log.e(this.getClass().getName(), "run(): ", e);
		}
	}
	
	/**
	 * Returns a URI object containing a properly formatted request URL.
	 * @return A URI object with the properly formatted URL.
	 * @throws URISyntaxException
	 */
	private URI getURI() throws URISyntaxException {
		return new URI(baseUrl 
				+ "?action=getListings" 
				+ "&search=" + mSearchPhrase 
				+ "&cat=" + mCategory 
				+ "&lat=" + mLatitude 
				+ "&long=" + mLongitude
				+ "&radius" + mSearchRadius);
	}
	
	/**
	 * Starts the thread which is responsible for sending the query to
	 * the server and processing the response.
	 */
	public void submit() {
		(new Thread(this)).start();
	}
	
	/**
	 * Replaces old listings in database with new ones.
	 * @param listings List of listings to be inserted into the database.
	 */
	private void updateDatabase(List<Listing> listings) {
		ListingsTable listingsTable = ListingsTable.getInstance(mContext);
		listingsTable.deleteAll();
		listingsTable.insert(listings);
	}
	
	/**
	 * Parse the given JSON string into a List of listings.
	 * @param response The response returned by the HTTP request.
	 * @return A list of Listing objects containing the results.
	 * @throws JSONException
	 */
	private List<Listing> parseJSONResponse(String response) throws JSONException {
		List<Listing> results = new LinkedList<Listing>();
		JSONArray resultArray = new JSONArray(response);
		
		for (int i = 0; i < resultArray.length(); i++) {
			String title = resultArray.getJSONObject(i).getString("Title");
			String briefDescription = resultArray.getJSONObject(i).getString("BriefDescription");
			String fullDescription = resultArray.getJSONObject(i).getString("FullDescription");
			int category = resultArray.getJSONObject(i).getInt("Category");
			double price =  resultArray.getJSONObject(i).getDouble("Price");
			String address = resultArray.getJSONObject(i).getString("Address");
			double latitude = resultArray.getJSONObject(i).getDouble("Lat");
			double longitude = resultArray.getJSONObject(i).getDouble("Long");
			String url = resultArray.getJSONObject(i).getString("URL");
			String additionalFields = resultArray.getJSONObject(i).getString("AdditionalFields");
			
			Listing listing = new Listing();
			listing.setTitle(title);
			listing.setBriefDescription(briefDescription);
			listing.setFullDescription(fullDescription);
			listing.setCategory(category);
			listing.setPrice(price);
			listing.setAddress(address);
			listing.setLatitude(latitude);
			listing.setLongitude(longitude);
			listing.setUrl(url);
			listing.setAdditionalFields(additionalFields);
			
			results.add(listing);
		}
		
		return results;
	}

}
