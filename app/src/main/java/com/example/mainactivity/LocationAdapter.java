package com.example.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.category_search.Document;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import static com.example.mainactivity.MainActivity.current_latitude;
import static com.example.mainactivity.MainActivity.current_longitude;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    MapCircle circleBySearch;
    Context context;
    ArrayList<Document> items;
    EditText editText;
    RecyclerView recyclerView;

    public LocationAdapter(ArrayList<Document> items, Context context, EditText editText, RecyclerView recyclerView) {
        this.context = context;
        this.items = items;
        this.editText = editText;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(Document item) {
        items.add(item);
    }


    public void clear() {
        items.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_location, viewGroup, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int i) {
        final Document model = items.get(i);
        holder.placeNameText.setText(model.getPlaceName());
        holder.addressText.setText(model.getAddressName());
    }


    public class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameText;
        TextView addressText;

        public LocationViewHolder(@NonNull final View itemView) {
            super(itemView);
            placeNameText = itemView.findViewById(R.id.ltem_location_tv_placename);
            addressText = itemView.findViewById(R.id.ltem_location_tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        MainActivity.editTextQuery.setText("");
                        MainActivity.gpsTracker.stopUsingGPS();
                        MainActivity.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        MainActivity.mapView.removeAllPOIItems();
                        MainActivity.mapView.removeAllCircles();
                        Document item = items.get(pos);
                        double latitude = Double.parseDouble(item.getY());
                        double longitude = Double.parseDouble(item.getX());
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

                    }
                }
            });
        }
    }
}