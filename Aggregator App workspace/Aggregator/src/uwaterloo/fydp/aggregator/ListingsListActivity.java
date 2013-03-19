/**
 * Activity that displays a list view of the listings. Also allows the 
 * user to perform queries.
 */

package uwaterloo.fydp.aggregator;

import com.google.android.gms.maps.model.LatLng;

import uwaterloo.fydp.aggregator.data.ListingsCursorAdapter;
import uwaterloo.fydp.aggregator.data.ListingsCursorLoader;
import uwaterloo.fydp.aggregator.data.ListingsTable;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ListingsListActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public static final int LISTINGS_LIST_LOADER = 0x01;
	
	private CursorAdapter mAdapter;
	private Menu mOptionsMenu;
	private String mLastSearchPhrase = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listings_list_activity);
		
		//populateDummyData();

//		View footer = getLayoutInflater().inflate(R.layout.listings_footer, null);
//		getListView().addFooterView(footer);

		// Create empty adapter which will be used by the ListingsCursorLoader
		mAdapter = new ListingsCursorAdapter(this, null);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(listViewItemClickListener);

		// Prepare the loader
		getLoaderManager().initLoader(LISTINGS_LIST_LOADER, null, this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateActionBarTitle();
	}
	
	/**
	 * Handle list view item clicks.
	 */
	private OnItemClickListener listViewItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String url = (String) view.getTag(R.id.tag_url);
//			if (!url.startsWith("http://") && !url.startsWith("https://"))
//				url = "http://" + url;
			
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//			startActivity(browserIntent);
			
			//Instead of browser, open map view
			Intent mapIntent = new Intent(view.getContext(), ListingsMapActivityNew.class);
			mapIntent.putExtra("url", url);
			startActivity(mapIntent);
			
		}
	};

	/**
	 * Create options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		getMenuInflater().inflate(R.menu.listings_list_activity, menu);
		MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
		final View actionView = searchMenuItem.getActionView();
		final EditText searchEditText = (EditText) actionView.findViewById(R.id.searchEditText);
		final ImageButton searchButton = (ImageButton) actionView.findViewById(R.id.searchButton);
		
		// Listener for when the action view expands/collapses
		searchMenuItem.setOnActionExpandListener(searchMenuActionExpandListener);	
		// Hide keyboard when search field loses focus
		searchEditText.setOnFocusChangeListener(searchEditTextFocusChangeListener);
		// Execute query on action button press from soft keyboard
		searchEditText.setOnEditorActionListener(searchEditorActionListener);
		// Handle search button press
		searchButton.setOnClickListener(searchButtonClickListener);

		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Update the ActionBar title to the search phrase
	 */
	private void updateActionBarTitle() {
		if (mLastSearchPhrase.isEmpty())
			getActionBar().setTitle(R.string.app_name);
		else
			getActionBar().setTitle(mLastSearchPhrase);
	}
	
	/**
	 * Handle IME action button press from soft keyboard for search field.
	 */
	private OnEditorActionListener searchEditorActionListener = new OnEditorActionListener() {	
		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				Log.i(this.getClass().getName(), "About to call submitQuery");
				submitQuery(view.getText().toString());
			}
			return false;
		}
	};
	
	/**
	 * Handle search button clicks.
	 */
	private OnClickListener searchButtonClickListener = new OnClickListener() {		
		@Override
		public void onClick(View view) {
			String searchText = ((EditText) findViewById(R.id.searchEditText)).getText().toString();
			submitQuery(searchText);
		}
	};
	
	/**
	 * Handle search field focus changes.
	 */
	private OnFocusChangeListener searchEditTextFocusChangeListener = new OnFocusChangeListener() {		
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (hasFocus)
				imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
			else
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	};
	
	/**
	 * Handle the expanding/collapsing of the search action item. Shows/hides soft keyboard.
	 */
	private OnActionExpandListener searchMenuActionExpandListener = new OnActionExpandListener() {
		@Override
		public boolean onMenuItemActionExpand(MenuItem item) {
			// Set focus to search field and show soft keyboard
			final EditText searchEditText = (EditText) item.getActionView().findViewById(R.id.searchEditText);
			
			searchEditText.post(new Runnable() {
				@Override
				public void run() {
					searchEditText.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
				}
			});
			return true;
		}

		@Override
		public boolean onMenuItemActionCollapse(MenuItem item) {
			// Hide soft keyboard
			final EditText searchEditText = (EditText) item.getActionView().findViewById(R.id.searchEditText);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
			return true;
		}
	};

	/**
	 * Handle options menu item selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_map_listings:
			// Launch map activity
			Intent intent = new Intent(this, ListingsMapActivityNew.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handle hardware key up events.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_SEARCH:
			// Expand search action view on hardware search key press
			MenuItem searchMenuItem = mOptionsMenu.findItem(R.id.menu_search);
			searchMenuItem.expandActionView();
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	/**
	 * Called when a new loader needs to be created.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new ListingsCursorLoader(this);
	}

	/**
	 * Called when the loader has finished retrieving data.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	/**
	 * Called when a loader resets causing the previous data to become
	 * unavailable.
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	/**
	 * Submit the query to the query engine
	 * @param searchPhrase The user entered search phrase.
	 */
	private void submitQuery(String searchPhrase) {
		// Check for empty or duplicated search phrase
		if (searchPhrase.isEmpty() || searchPhrase.compareToIgnoreCase(mLastSearchPhrase) == 0) {
			// TODO: Show toast
			return;
		}
		
		// Collapse ActionView (a.k.a the search box and button)
		mOptionsMenu.findItem(R.id.menu_search).collapseActionView();
		mLastSearchPhrase = searchPhrase;
		updateActionBarTitle();
		
		// Get user's last known location		
		LatLng ll = new MyLocation(this).getLatLng();
		
		new Query(this, searchPhrase, 0, ll.latitude, ll.longitude, 100000).execute();
	}

	/**
	 * Testing method. TODO: Delete.
	 */
	@SuppressWarnings("unused")
	private void populateDummyData() {
		ListingsTable lt = ListingsTable.getInstance(getApplicationContext());
		lt.open();
		lt.insert("Possible Craigslist listing", "This is a listing",
				"Apple", 1600.00, "http://www.google.com/", 123.123, 123.123);
		lt.insert("Kijiji Listing", "Description goes here!", "Apple",
				199.99, "www.google.com", 123.123, 123.123);
		lt.insert("Another Title", "Another test description2", "Apple",
				1623.10, "http://craigslist.com", 123.123, 123.123);
		lt.insert("Another another title", "Another test description2",
				"Apple", 1.10, "http://kijiji.com", 123.123, 123.123);
		lt.insert("Another another another title",
				"Another test description2", "Apple", 3.15, "www.yahoo.ca", 123.123,
				123.123);
	}
}
