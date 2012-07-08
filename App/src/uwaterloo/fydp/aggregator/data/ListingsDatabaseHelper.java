/**
 * Class responsible for creating and upgrading the listings database.
 */

package uwaterloo.fydp.aggregator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListingsDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "listings_database";
	private static final int DATABASE_VERSION = 1;

	public ListingsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Called during the creation of the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		ListingsTable.onCreate(db);
	}

	/**
	 * Called when database version is incremented.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ListingsTable.onUpgrade(db, oldVersion, newVersion);
	}

}
