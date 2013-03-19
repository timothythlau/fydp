/**
 * Class representing the listings table. Contains the constants and SQL
 * statements necessary for table creation.
 */

package uwaterloo.fydp.aggregator.data;

import java.util.List;

import uwaterloo.fydp.aggregator.Listing;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ListingsTable {
	private static final String LISTINGS_TABLE_NAME = "listings";
	public static final String KEY_ROW_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_PRICE = "price";
	public static final String KEY_URL = "url";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_DATE = "date";
	public static final String KEY_SOURCE = "source";
	private static final String LISTINGS_TABLE_CREATE = "CREATE TABLE "
			+ LISTINGS_TABLE_NAME + "(" + KEY_ROW_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE
			+ " TEXT NOT NULL, " + KEY_DESCRIPTION + " TEXT NULL, "
			+ KEY_CATEGORY + " TEXT NULL, " + KEY_PRICE + " REAL, " + KEY_URL
			+ " TEXT NOT NULL, " + KEY_LATITUDE + " REAL NULL, "
			+ KEY_LONGITUDE + " REAL NULL, "
			+ KEY_DATE + " TEXT NULL, "
			+ KEY_SOURCE + " TEXT NULL);";

	private static ListingsTable instance = null;
	private Context mContext;
	private ListingsDatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(LISTINGS_TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ListingsDatabaseHelper.class.getName(),
				"Upgrading database version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + LISTINGS_TABLE_NAME);
		onCreate(database);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Android context
	 */
	private ListingsTable(Context context) {
		this.mContext = context.getApplicationContext();
	}

	/**
	 * Returns an instance to the ListingsTable class. Synchronized singleton
	 * pattern is used to avoid issues with multiple threads accessing the
	 * listings table.
	 * 
	 * @param context
	 *            An Android context.
	 * @return The instance of the ListingsTable class.
	 */
	public static synchronized ListingsTable getInstance(Context context) {
		if (instance == null)
			instance = new ListingsTable(context);

		return instance;
	}

	/**
	 * Open database. If it cannot be opened, try to create a new instance of
	 * the database. If it cannot be created, throw an exception to signal the
	 * failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public ListingsTable open() throws SQLException {
		if (mDatabaseHelper == null) {
			mDatabaseHelper = new ListingsDatabaseHelper(mContext);
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		return this;
	}

	/**
	 * Close database.
	 */
	public void close() {
		if (mDatabaseHelper != null)
			mDatabaseHelper.close();
	}

	/**
	 * Inserts the given listing into the database.
	 * 
	 * @param title
	 *            The title of the listing.
	 * @param description
	 *            The description of the listing.
	 * @param category
	 *            The category the listing belongs to.
	 * @param price
	 *            The price of the listing.
	 * @return rowId or -1 on failure.
	 */
	public long insert(String title, String description,
			String category, double price, String url, double latitude,
			double longitude) {
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, title);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_CATEGORY, category);
		values.put(KEY_PRICE, price);
		values.put(KEY_URL, url);
		values.put(KEY_LATITUDE, latitude);
		values.put(KEY_LONGITUDE, longitude);

		return mDatabase.insert(LISTINGS_TABLE_NAME, null, values);
	}
	
	/**
	 * Inserts the listings in the given list into the database.
	 * @param listings A List of Listing objects to be inserted. 
	 */
	public void insert(List<Listing> listings) {
		InsertHelper ih = new InsertHelper(mDatabase, LISTINGS_TABLE_NAME);
		final int titleColumnIndex = ih.getColumnIndex(KEY_TITLE);
		final int descriptionColumnIndex = ih.getColumnIndex(KEY_DESCRIPTION);
		final int categoryColumnIndex = ih.getColumnIndex(KEY_CATEGORY);
		final int priceColumnIndex = ih.getColumnIndex(KEY_PRICE);
		final int urlColumnIndex = ih.getColumnIndex(KEY_URL);
		final int latitudeColumnIndex = ih.getColumnIndex(KEY_LATITUDE);
		final int longitudeColumnIndex = ih.getColumnIndex(KEY_LONGITUDE);
		
		try {
			for (Listing listing : listings) {
				ih.prepareForInsert();
				
				// Bind data to columns
				ih.bind(titleColumnIndex, listing.getTitle());
				ih.bind(descriptionColumnIndex, listing.getBriefDescription());
				ih.bind(categoryColumnIndex, listing.getCategory());
				ih.bind(priceColumnIndex, listing.getPrice());
				ih.bind(urlColumnIndex, listing.getUrl());
				ih.bind(latitudeColumnIndex, listing.getLatitude());
				ih.bind(longitudeColumnIndex, listing.getLongitude());
				
				ih.execute();
			}
		} finally {
			ih.close();
		}
	}

	/**
	 * Delete the listing with the given id.
	 * 
	 * @param rowId
	 *            The id of the row to be deleted.
	 * @return True if a row was deleted.
	 */
	public boolean deleteListing(long rowId) {
		return mDatabase.delete(LISTINGS_TABLE_NAME, KEY_ROW_ID + " = " + rowId,
				null) > 0;
	}

	/**
	 * Deletes all listings.
	 * 
	 * @return The number of rows deleted.
	 */
	public int deleteAll() {
		return mDatabase.delete(LISTINGS_TABLE_NAME, "1", null);
	}

	/**
	 * Returns the given number of listings starting from the offset + 1
	 * listing.
	 * 
	 * @param numberOfListings
	 *            The number of listings that should be returned.
	 * @param offset
	 *            The starting point after which the listings will be returned.
	 * @return A Cursor pointing to the rows returned by the query.
	 */
	public Cursor getListings(int numberOfListings, int offset) {
		return mDatabase.query(LISTINGS_TABLE_NAME, new String[] { KEY_ROW_ID,
				KEY_TITLE, KEY_DESCRIPTION, KEY_CATEGORY, KEY_PRICE, KEY_URL,
				KEY_LATITUDE, KEY_LONGITUDE }, null, null, null, null, null,
				numberOfListings + ", " + offset);
	}

	/**
	 * Retrieves all listings stored in the listings table.
	 * 
	 * @return A Cursor pointing to the the returned data set.
	 */
	public Cursor getListings() {
		return mDatabase.query(LISTINGS_TABLE_NAME, new String[] { KEY_ROW_ID,
				KEY_TITLE, KEY_DESCRIPTION, KEY_CATEGORY, KEY_PRICE, KEY_URL,
				KEY_LATITUDE, KEY_LONGITUDE }, null, null, null, null, null,
				null);
	}
}
