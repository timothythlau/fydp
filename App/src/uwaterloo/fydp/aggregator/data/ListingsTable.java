/**
 * Class representing the listings table. Contains the constants and SQL
 * statements necessary for table creation.
 */

package uwaterloo.fydp.aggregator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
	private static final String LISTINGS_TABLE_CREATE = "CREATE TABLE "
			+ LISTINGS_TABLE_NAME + "(" + KEY_ROW_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE
			+ " TEXT NOT NULL, " + KEY_DESCRIPTION + " TEXT NULL, "
			+ KEY_CATEGORY + " TEXT NULL, " + KEY_PRICE + " REAL, " + KEY_URL
			+ " TEXT NOT NULL, " + KEY_LATITUDE + " REAL NULL, "
			+ KEY_LONGITUDE + " REAL NULL);";

	private static ListingsTable instance = null;
	private Context context;
	private ListingsDatabaseHelper databaseHelper;
	private SQLiteDatabase database;

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
		this.context = context;
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
		databaseHelper = new ListingsDatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close database.
	 */
	public void close() {
		databaseHelper.close();
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
	public long insertListing(String title, String description,
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

		return database.insert(LISTINGS_TABLE_NAME, null, values);
	}

	/**
	 * Delete the listing with the given id.
	 * 
	 * @param rowId
	 *            The id of the row to be deleted.
	 * @return True if a row was deleted.
	 */
	public boolean deleteListing(long rowId) {
		return database.delete(LISTINGS_TABLE_NAME, KEY_ROW_ID + " = " + rowId,
				null) > 0;
	}

	/**
	 * Deletes all listings.
	 * 
	 * @return The number of rows deleted.
	 */
	public int deleteAllListings() {
		return database.delete(LISTINGS_TABLE_CREATE, "1", null);
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
		return database.query(LISTINGS_TABLE_NAME, new String[] { KEY_ROW_ID,
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
		return database.query(LISTINGS_TABLE_NAME, new String[] { KEY_ROW_ID,
				KEY_TITLE, KEY_DESCRIPTION, KEY_CATEGORY, KEY_PRICE, KEY_URL,
				KEY_LATITUDE, KEY_LONGITUDE }, null, null, null, null, null,
				null);
	}
}
