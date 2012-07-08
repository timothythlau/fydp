/**
 * Loader which queries the listings database and returns a cursor to the data.
 * The query is performed in a background thread and the results are 
 * 
 * This ListingsCursorLoader is based on the CursorLoader implementation
 * found in the Android Open Source Project.
 * 
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uwaterloo.fydp.aggregator.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

public class ListingsCursorLoader extends AsyncTaskLoader<Cursor> {
	private final ForceLoadContentObserver mObserver;
	private Cursor mCursor;

	public ListingsCursorLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}

	/**
	 * Loads data from listings table in worker thread.
	 */
	@Override
	public Cursor loadInBackground() {
		ListingsTable listingsTable = ListingsTable.getInstance(getContext());
		listingsTable.open();
		Cursor cursor = listingsTable.getListings();

		if (cursor != null) {
			// Ensure the cursor window is filled
			cursor.getCount();
			cursor.registerContentObserver(mObserver);
		}

		listingsTable.close();
		return cursor;
	}

	/**
	 * Called when there is data that needs to be delivered to the client. Runs
	 * on the UI thread.
	 */
	@Override
	public void deliverResult(Cursor cursor) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (cursor != null) {
				cursor.close();
			}
			return;
		}

		Cursor oldCursor = mCursor;
		mCursor = cursor;

		if (isStarted()) {
			super.deliverResult(cursor);
		}

		if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
			oldCursor.close();
		}
	}

	/**
	 * Starts an asynchronous load of the listings data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mCursor != null) {
			deliverResult(mCursor);
		}
		if (takeContentChanged() || mCursor == null) {
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the loader. Must be called from the UI thread.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = null;
	}
}
