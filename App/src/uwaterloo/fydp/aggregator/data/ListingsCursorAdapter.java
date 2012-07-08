package uwaterloo.fydp.aggregator.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import uwaterloo.fydp.aggregator.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListingsCursorAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private int titleColumnIndex;
	private int descriptionColumnIndex;
	private int priceColumnIndex;

	static class ViewHolder {
		public TextView title;
		public TextView description;
		public TextView price;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Android context
	 * @param cursor
	 *            A Cursor object pointing to the listings data.
	 */
	public ListingsCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		this.inflater = LayoutInflater.from(context);

		if (cursor != null) {
			this.titleColumnIndex = cursor
					.getColumnIndexOrThrow(ListingsTable.KEY_TITLE);
			this.descriptionColumnIndex = cursor
					.getColumnIndexOrThrow(ListingsTable.KEY_DESCRIPTION);
			this.priceColumnIndex = cursor
					.getColumnIndexOrThrow(ListingsTable.KEY_PRICE);
		}
	}

	/**
	 * Bind the given view to data pointed to by the cursor.
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder vh = (ViewHolder) view.getTag();
		vh.title.setText(cursor.getString(titleColumnIndex));
		vh.description.setText(cursor.getString(descriptionColumnIndex));

		String pattern = "'$'#,##0.00";
		NumberFormat numberFormat = new DecimalFormat(pattern);
		vh.price.setText(numberFormat.format(cursor.getDouble(priceColumnIndex)));
	}

	/**
	 * Creates a new view to hold cursor data.
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.listings_list_item, parent,
				false);
		ViewHolder vh = new ViewHolder();
		vh.title = (TextView) rowView.findViewById(R.id.listing_title);
		vh.description = (TextView) rowView
				.findViewById(R.id.listing_description);
		vh.price = (TextView) rowView.findViewById(R.id.listing_price);
		rowView.setTag(vh);

		return rowView;
	}

	/**
	 * Updates column indices on cursor swap before calling the default
	 * CursorAdapter swapCursor implementation.
	 */
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null) {
			this.titleColumnIndex = newCursor
					.getColumnIndexOrThrow(ListingsTable.KEY_TITLE);
			this.descriptionColumnIndex = newCursor
					.getColumnIndexOrThrow(ListingsTable.KEY_DESCRIPTION);
			this.priceColumnIndex = newCursor
					.getColumnIndexOrThrow(ListingsTable.KEY_PRICE);
		}

		return super.swapCursor(newCursor);
	}
}
