package cs490.blitz;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 * Created by Dingzhe on 12/7/2015.
 */
public class Maptest extends FragmentActivity {

    private GoogleMap mMap;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maptest);

        setUpMap();
    }

    public void onSearch(View view){
        EditText location = (EditText)findViewById(R.id.searchMT);
        String locaStr = location.getText().toString();
        List<Address> addressesList = null;

        if(locaStr != null && !locaStr.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressesList = geocoder.getFromLocationName(locaStr, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressesList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            System.out.println(latLng.latitude);
            System.out.println(latLng.longitude);

        }
    }

    private void setUpMap(){
        if(mMap == null){
            mMap = ((WorkaroundMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.mapMT)).getMap();
        }

        if(mMap!= null){
            mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
            mMap.setMyLocationEnabled(true);

            sv = (ScrollView) findViewById(R.id.containerMT);
            //disable sv when touching map
            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMT)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
            });

        }
    }

}
