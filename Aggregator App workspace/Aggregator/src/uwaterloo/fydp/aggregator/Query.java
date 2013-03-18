package uwaterloo.fydp.aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
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
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class Query extends AsyncTask<String, Void, Void> {
	private final String baseUrl = "http://54.234.149.94/index.php";
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
	 * Run on UI thread.
	 */
	@Override
	protected void onPreExecute() {
		// TODO Show progress bar in UI
		Activity activity = (Activity) mContext;
		activity.findViewById(R.id.emptyTextView).setVisibility(View.GONE);
		activity.findViewById(R.id.emptyProgressBar).setVisibility(View.VISIBLE);
	}

	/**
	 * Run in background thread.
	 */
	@Override
	protected Void doInBackground(String... params) {
		// Clear previous results
		ListingsTable.getInstance(mContext).deleteAll();
		publishProgress();

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(getURI());
			HttpResponse response = httpClient.execute(request);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String responseString = reader.readLine();
			if (responseString != null) {
				List<Listing> listings = parseJSONResponse(responseString);
				ListingsTable.getInstance(mContext).insert(listings);
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

		return null;
	}
	
	/**
	 * Run on UI thread.
	 */
	protected void onProgressUpdate(Void... progress) {
		// TODO Replace list view with progress bar
		((Activity) mContext).getLoaderManager().getLoader(ListingsListActivity.LISTINGS_LIST_LOADER).onContentChanged();
	}

	/**
	 * Run on UI thread.
	 */
	@Override
	protected void onPostExecute(Void result) {
		// TODO Update list view
		
		Activity activity = (Activity) mContext;
		activity.getLoaderManager().getLoader(ListingsListActivity.LISTINGS_LIST_LOADER).onContentChanged();
		activity.findViewById(R.id.emptyTextView).setVisibility(View.VISIBLE);
		activity.findViewById(R.id.emptyProgressBar).setVisibility(View.GONE);
	}
	
	/**
	 * Returns a URI object containing a properly formatted request URL.
	 * @return A URI object with the properly formatted URL.
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException 
	 */
	private URI getURI() throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(baseUrl 
				+ "?action=getListings" 
				+ "&search=" + URLEncoder.encode(mSearchPhrase, "UTF-8") 
				+ "&cat=" + mCategory 
				+ "&lat=" + mLatitude 
				+ "&long=" + mLongitude
				+ "&radius" + mSearchRadius);
		
		Log.i(this.getClass().getName(), uri.toString());
		return uri;
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
