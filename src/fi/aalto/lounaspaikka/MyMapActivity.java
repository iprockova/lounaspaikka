package fi.aalto.lounaspaikka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import fi.aalto.lounaspaikka.map.MyItemizedOverlay;
import fi.aalto.lounaspaikka.map.MyLocation;
import fi.aalto.lounaspaikka.map.MyLocation.LocationResult;
import fi.aalto.lounaspaikka.objectfiles.ObjectsContainer;
import fi.aalto.lounaspaikka.objectfiles.Restaurant;

public class MyMapActivity extends MapActivity{
	
	private MyLocationOverlay myLocationOverlay;
	private GeoPoint geoPoint;
	HashMap<String,GeoPoint> restarauntLocations = new HashMap<String, GeoPoint>();
	ArrayList<String> restNames;
	ArrayList<GeoPoint> restLocations;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
		
	}
	
	/**
	 * Adding restaurants location to the list
	 * @param Restaurant rest - if not null, it will be the only one on the map
	 */
	private void populateLocations(Restaurant rest){
		restNames = new ArrayList<String>();
		restLocations = new ArrayList<GeoPoint>();
		
		if (rest!= null)
		{	 
			 GeoPoint location = new GeoPoint((int)(rest.location.lat* 1E6),
		    			(int)(rest.location.lng* 1E6) );
		    	  restNames.add(rest.name);
		    	  restLocations.add(location);
		    	  return;
		}
		
    	  GeoPoint location = new GeoPoint((int)(60.185137* 1E6),
    			(int)(24.832149* 1E6) );
    	  restNames.add("Teekkariravintolat");
    	  restLocations.add(location);
    	  
    	  location = new GeoPoint((int)(60.18702* 1E6),
      			(int)(24.821034* 1E6) );
    	  restNames.add("Teekkariravintolat");
    	  restLocations.add(location);
    	  
    	  location = new GeoPoint((int)(60.187148* 1E6),
        			(int)(24.818920* 1E6) );
    	  restNames.add("TUAS-talo");
    	  restLocations.add(location);
     
      	 location = new GeoPoint((int)(60.185873* 1E6),
     			(int)(24.827535* 1E6) );
      	restNames.add("Alvari");
      	restLocations.add(location);
      	 
     	  
	}
	
	/**
	 * To show only one restaurant just pass it in an intent with the name "Restaurant"
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		Restaurant rest = (Restaurant)getIntent().getSerializableExtra("Restaurant");
		populateLocations(rest);

		try {
			
			final MapView mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			myLocationOverlay = new MyLocationOverlay(this, mapView);
			mapView.getOverlays().add(myLocationOverlay);
			mapView.invalidate();
			mapView.setSatellite(false);
			
			LocationResult locationResult = new LocationResult(){
			    @Override
			    public void gotLocation(Location location){ 
			        //Got the location!
			    	geoPoint = new GeoPoint((int)(location.getLatitude()* 1E6),
			    			(int)(location.getLongitude()* 1E6) );
			    	MapController mc = mapView.getController();
					mc.setZoom(16);
					mc.animateTo(geoPoint);
					Log.w("map",geoPoint.toString());
					
					Resources res = getApplicationContext().getResources();
					Drawable myImage = res.getDrawable(R.drawable.restaurant_map);
					MyItemizedOverlay restMarker = new MyItemizedOverlay(myImage,
							getApplicationContext());
					restMarker.setMapActivity(MyMapActivity.this);
				 
		 
					//Can't get it from the list of restaurants, incorrect location from the API
					/*
					for(Restaurant rest:ObjectsContainer.restaurants)
					{
						GeoPoint point = new GeoPoint((int)(rest.location.lat* 1E6),
				    			(int)(rest.location.lng* 1E6) );
						OverlayItem item = new OverlayItem(point,rest.name,"snippet");
						item.setMarker(myImage);
					restMarker.addOverlay(item);
						
					}*/
					
					//Iterating through items
					for(int i=0;i<restNames.size();i++){
						OverlayItem item = new OverlayItem(restLocations.get(i), restNames.get(i),"snippet");
						item.setMarker(myImage);
						restMarker.addOverlay(item);
					}
					
					mapView.getOverlays().add(restMarker);	
			    
			    }
			};
			MyLocation myLocation = new MyLocation();
			myLocation.getLocation(this, locationResult);

			
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("map", e.toString());
		}

	}
	/**
	 * Show RestaurauntActivity for selected restaurant
	 * @param name
	 */
	public void showRestaurantInfo(String name){
		Restaurant choice = null;
		for (Restaurant R : ObjectsContainer.restaurants)
			if (R.name.equals(name)){
				choice = R;
				break;
			}
		String info = new String();
		info+="Restaurant: "+name+"\n"+"Campus: "+choice.campus+"\n"+"Address: "+choice.location.address+"\n";
		Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
	}
	 
	@Override
	protected void onPause() {
		super.onPause();
		//Don't using navigation to prevent battery drain
		myLocationOverlay.disableMyLocation();
	}
	
	@Override
		protected void onResume() {
		super.onResume();
	myLocationOverlay.enableMyLocation();
	}
	
	
	
}