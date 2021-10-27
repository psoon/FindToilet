package com.example.mainactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mainactivity.category_search.Document;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import static com.example.mainactivity.MainActivity.current_latitude;
import static com.example.mainactivity.MainActivity.current_longitude;

public class BookMarkAdapter extends BaseAdapter {
    MapCircle circleBySearch;
    private TextView nameTv;
    private TextView adrTv;

    private ArrayList<String> listViewArrayList = new ArrayList<>();

    public BookMarkAdapter(){}

    @Override
    public int getCount() {
        return listViewArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = Integer.parseInt(listViewArrayList.get(position));
        final Context context = viewGroup.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_location, viewGroup, false);
        }

        nameTv = convertView.findViewById(R.id.ltem_location_tv_placename);
        adrTv = convertView.findViewById(R.id.ltem_location_tv_address);

        nameTv.setText(MainActivity.dataArr[pos][1]);
        adrTv.setText(MainActivity.dataArr[pos][2]);

        LinearLayout item_location_layout = convertView.findViewById(R.id.ltem_location_layout);
        item_location_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.gpsTracker!=null){
                    MainActivity.gpsTracker.stopUsingGPS();
                }
                MainActivity.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                MainActivity.mapView.removeAllPOIItems();
                MainActivity.mapView.removeAllCircles();
                double latitude = Double.parseDouble(MainActivity.dataArr[pos][17]);
                double longitude = Double.parseDouble(MainActivity.dataArr[pos][18]);
                current_latitude = latitude;
                current_longitude = longitude;


                MainActivity.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude), true);
                circleBySearch = new MapCircle(
                        MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude), // center
                        GpsTracker.radius, // radius
                        Color.argb(128, 0, 0, 0), // strokeColor
                        Color.argb(40, 0, 0, 255) // fillColor
                );
                MainActivity.mapView.addCircle(circleBySearch);
                circleBySearch.setCenter(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude));
                GpsTracker.markerUpdate(current_latitude, current_longitude);

                MapPOIItem item = MainActivity.mapView.findPOIItemByTag(pos);
                MainActivity.mapView.selectPOIItem(item, true);

                Activity activity = (Activity) context;
                activity.finish();
            }
        });
        return convertView;
    }

    public void addItem(String num){
        listViewArrayList.add(num);
    }
}
