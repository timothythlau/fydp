package uwaterloo.fydp.aggregator;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ListingsMapActivity extends MapActivity {
	/** Called when the activity is first created. */

	private MapController mapController;
	private MapView mapView;
	private MyLocationOverlay myLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listings_map_activity);
		
		// Allow the application icon to be used for "up" navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.star_on);
		CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(
				drawable, this);

		double[] latArr = { 43.6366, 43.6783, 43.6922, 43.6897, 43.6805 };
		double[] longArr = { -79.397, -79.423, -79.431, -79.371, -79.334 };

		for (int i = 0; i < 5; i++) {
			GeoPoint point = new GeoPoint((int) (latArr[i] * 1e6),
					(int) (longArr[i] * 1e6));
			OverlayItem overlayitem = new OverlayItem(point,
					"Test Point #" + i,
					"Test field 1\nTest field 2\nTest field 3");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
		}

		mapController = mapView.getController();
		myLocation = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocation);
		myLocation.enableMyLocation();

		myLocation.runOnFirstFix(new Runnable() {
			public void run() {
				// mapController.animateTo(myLocation.getMyLocation());
			}
		});

		mapController.setZoom(12);
	}

	/**
	 * Handle options menu item selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Return to list activity
			Intent intent = new Intent(this, ListingsListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}