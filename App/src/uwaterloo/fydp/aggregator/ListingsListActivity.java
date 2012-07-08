/**
 * Activity that displays a list view of the listings. Also allows the 
 * user to perform queries.
 */

package uwaterloo.fydp.aggregator;

import uwaterloo.fydp.aggregator.data.ListingsCursorAdapter;
import uwaterloo.fydp.aggregator.data.ListingsCursorLoader;
import uwaterloo.fydp.aggregator.data.ListingsTable;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;

public class ListingsListActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LISTINGS_LIST_LOADER = 0x01;
	private CursorAdapter mAdapter;
	private Menu mOptionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listings_list_activity);

		View footer = getLayoutInflater().inflate(R.layout.listings_footer,
				null);
		getListView().addFooterView(footer);

		// Create empty adapter which will be used by the ListingsCursorLoader
		mAdapter = new ListingsCursorAdapter(this, null);
		setListAdapter(mAdapter);

		// Prepare the loader
		getLoaderManager().initLoader(LISTINGS_LIST_LOADER, null, this);
	}

	/**
	 * Create options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		getMenuInflater().inflate(R.menu.listings_list_activity, menu);
		MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
		final EditText searchEditText = (EditText) searchMenuItem
				.getActionView();
		
		// Hide keyboard when search field loses focus
		searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		});

		// Listener for when the action view expands/collapses
		searchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// Set focus to search field and show keyboard
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
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Handle options menu item selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_map_listings:
			// Launch map activity
			Intent intent = new Intent(this, ListingsMapActivity.class);
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
	 * Testing method. TODO: Delete.
	 */
	@SuppressWarnings("unused")
	private void populateDummyData() {
		ListingsTable lt = ListingsTable.getInstance(this);
		lt.open();
		lt.insertListing("Possible Craigslist listing", "This is a listing",
				"Apple", 1600.00, "abc", 123.123, 123.123);
		lt.insertListing("Kijiji Listing", "Description goes here!", "Apple",
				199.99, "abc", 123.123, 123.123);
		lt.insertListing("Another Title", "Another test description2", "Apple",
				1623.10, "abc", 123.123, 123.123);
		lt.insertListing("Another another title", "Another test description2",
				"Apple", 1.10, "abc", 123.123, 123.123);
		lt.insertListing("Another another another title",
				"Another test description2", "Apple", 3.15, "abc", 123.123,
				123.123);
		lt.close();
	}
}
