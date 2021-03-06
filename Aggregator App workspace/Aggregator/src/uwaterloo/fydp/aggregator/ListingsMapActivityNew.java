package uwaterloo.fydp.aggregator;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uwaterloo.fydp.aggregator.MyLocation;
import uwaterloo.fydp.aggregator.data.ListingsTable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ListingsMapActivityNew extends FragmentActivity 
	implements OnInfoWindowClickListener {

	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		//private final View mWindow;
		private final View mContents;

		CustomInfoWindowAdapter() {
			//mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
			mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			render(marker, mContents);
			return mContents;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
//			render(marker, mWindow);
//			return mWindow;
			return null;
		}

		private void render(Marker marker, View view) {
			String title = marker.getTitle();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				// Spannable string allows us to edit the formatting of the text.
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}

			String snippet = marker.getSnippet();
			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null /*&& snippet.length() > 12*/) {
				SpannableString snippetText = new SpannableString(snippet);
				
				Pattern p = Pattern.compile("(\\n)");
				Matcher m = p.matcher(snippet);
				if (m.find()) {
					snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, m.start(), 0);
					snippetText.setSpan(new StyleSpan(Typeface.BOLD), 0, m.start(), 0);
					snippetText.setSpan(new ForegroundColorSpan(Color.GRAY), m.start()+1, snippet.length(), 0);
				}
				else {
					snippetText.setSpan(new ForegroundColorSpan(Color.GRAY), 0, snippet.length(), 0);
				}

				snippetUi.setText(snippetText);
			} else {
				snippetUi.setText("");
			}
		}

	}

	private GoogleMap mMap;
	private MyLocation myLocation;
	private HashMap<Marker, URI> hashUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listings_map_activity_new);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent mapIntent = getIntent();
		updateActionBarTitle(mapIntent.getStringExtra("searchterm"));
		
		//Create new MyLocation object to get location before initiating map
		myLocation = new MyLocation(this);

		//get handle on maps fragment
		setUpMapIfNeeded();
		
		//Intent from listview? Centre map to marker
		if (mapIntent.getStringExtra("url") == null)
			processMarkers(null);
		else
			processMarkers(mapIntent.getStringExtra("url"));
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocation.activate(myLocation.listener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocation.deactivate();
	}

/*	@Override
	protected void onStop() {
		super.onStop();
		//mMap.clear();
		//myLocation.deactivate();
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMap.clear();
	}

	private void setUpMapIfNeeded() {

		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {				
				//This is how you register the LocationSource
				mMap.setLocationSource(myLocation);
				mMap.setMyLocationEnabled(true);
				
				//set custom marker infowindow and infowindowclicklistener
				mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
				mMap.setOnInfoWindowClickListener(this);
				mMap.clear();
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation.getLatLng(),13));
			}
			
		}
	}
	
	// worker thread implementation
	private void processMarkers(final String uri) {

		new Thread(new Runnable() {
			public void run() {
				DecimalFormat df = new DecimalFormat("#0.00");

				hashUri = new HashMap<Marker, URI>();
//				List<Marker> markerList = new LinkedList<Marker>();
//				final Marker displayedMarker;

				Pattern kijijiPat = Pattern.compile("[kijiji]+\\.[ca]+", Pattern.CASE_INSENSITIVE);

				//get listings db and cursor
				ListingsTable lt = ListingsTable.getInstance(getApplicationContext());
				Cursor mCursor = lt.getListings();
				mCursor.moveToFirst();

				//traverse listings db and add marker for all listings
				while (!mCursor.isAfterLast()) {
					final String mTitle = mCursor.getString(mCursor.getColumnIndex(ListingsTable.KEY_TITLE));
					final String mDesc = mCursor.getString(mCursor.getColumnIndex(ListingsTable.KEY_DESCRIPTION))
							.replaceAll("(&nbsp;)", "");
					final Double mPrice = mCursor.getDouble(mCursor.getColumnIndex(ListingsTable.KEY_PRICE));
					final String mUrl = mCursor.getString(mCursor.getColumnIndex(ListingsTable.KEY_URL));
					final Double mLat = mCursor.getDouble(mCursor.getColumnIndex(ListingsTable.KEY_LATITUDE));
					final Double mLng = mCursor.getDouble(mCursor.getColumnIndex(ListingsTable.KEY_LONGITUDE));

					//check if marker lat long is valid, then add markers onto map
					if ((mLat != null || mLng != null) &&
							mLat >= -90 && mLat <= 90 && mLng >= -180 && mLng <= 180) {

						//price formatter
						final String mPriceStr;
						if (mPrice == -1)
							mPriceStr = "Unspecified price";
						else if (mPrice == 0)
							mPriceStr = "Free!";
						else
							mPriceStr = "$" + df.format(mPrice).toString();

						//regex on url to set icon colours
						Matcher kijijiMat = kijijiPat.matcher(mUrl);
						//				Pattern craigPat = Pattern.compile("[craigslist]+\\.[ca]+", Pattern.CASE_INSENSITIVE);
						//				Matcher craigMat = craigPat.matcher(mUrl);

						final float iconColour;

						if (kijijiMat.find())
							iconColour = BitmapDescriptorFactory.HUE_RED;
						else
							iconColour = BitmapDescriptorFactory.HUE_VIOLET;

						ListingsMapActivityNew.this.runOnUiThread(new Runnable () {
							public void run() {
								//add marker to map and add to hash table for easy lookup for browser on infowindow click
								Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLat,mLng))
										.title(mTitle).snippet(mPriceStr + "\n" + mDesc).icon(BitmapDescriptorFactory.defaultMarker(iconColour)));

								try {
									hashUri.put(marker, new URI(mUrl));

									//opened from list view? animate camera to marker
									if (uri != null && uri.equals(mUrl)) {
										marker.showInfoWindow();
										mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));							
									}
								} catch (URISyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//hashUri.remove(marker);
									marker.remove();
								}
							}
						});

						mCursor.moveToNext();
					}
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listings_map_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, ListingsListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), hashUrl.get(marker).toString(), Toast.LENGTH_LONG).show();
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		browserIntent.setData(Uri.parse(hashUri.get(marker).toString()));
		startActivity(browserIntent);
	}
	
	private void updateActionBarTitle(String mLastSearchPhrase) {
		if (mLastSearchPhrase == null || mLastSearchPhrase.isEmpty())
			getActionBar().setTitle(R.string.app_name);
		else
			getActionBar().setTitle(mLastSearchPhrase);
	}
}

