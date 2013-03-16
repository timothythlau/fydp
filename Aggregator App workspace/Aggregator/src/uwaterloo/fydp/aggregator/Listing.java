/**
 * Represent a listings. Stores all the necessary data for
 * a single listing.
 */

package uwaterloo.fydp.aggregator;

public class Listing {
	private String mTitle;
	private String mBriefDescription;
	private String mFullDescription;
	private int mCategory;
	private double mPrice;
	private String mAddress;
	private double mLatitude;
	private double mLongitude;
	private String mUrl;
	private String mAdditionalFields;

	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String value) {
		mTitle = value;
	}

	public String getBriefDescription() {
		return mBriefDescription;
	}
	
	public void setBriefDescription(String value) {
		mBriefDescription = value;
	}

	public String getFullDescription() {
		return mFullDescription;
	}
	
	public void setFullDescription(String value) {
		mFullDescription = value;
	}

	public int getCategory() {
		return mCategory;
	}
	
	public void setCategory(int value) {
		mCategory = value;
	}
	
	public double getPrice() {
		return mPrice;
	}
	
	public void setPrice(double value) {
		mPrice = value;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void setAddress(String value) {
		mAddress = value;
	}
	
	public double getLatitude() {
		return mLatitude;
	}
	
	public void setLatitude(double value) {
		mLatitude = value;
	}
	
	public double getLongitude() {
		return mLongitude;
	}
	
	public void setLongitude(double value) {
		mLongitude = value;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setUrl(String value) {
		mUrl = value;
	}
	
	public String getAdditionalFields() {
		return mAdditionalFields;
	}
	
	public void setAdditionalFields(String value) {
		mAdditionalFields = value;
	}
}
