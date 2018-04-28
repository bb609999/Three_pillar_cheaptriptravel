package com.example.three_pillar_cheaptriptravel.object;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/4/28.
 */

public class EventLocationCluster {

    public static ArrayList<ArrayList<Event>> clusterEvent(List<Event> eventList,int RadiusInMeters) {



        ArrayList<ArrayList<Event>> clusterList = new ArrayList<ArrayList<Event>>();

        while(eventList.size()>0){

            Log.d("eventList", "clusterEvent: "+eventList.size());

            if(eventList.size()==1){
                ArrayList<Event> cluster = new ArrayList<>();
                cluster.add(eventList.get(0));
                clusterList.add(cluster);
                break;
            }


        Place place = eventList.get(0).getPlace();


        LatLngBounds latLngBounds = toBounds(place.getLatLng(),RadiusInMeters);

        ArrayList<Event> cluster = new ArrayList<>();

        for(Event event:eventList) {
            Log.d("SS", "clusterEvent: " + latLngBounds.contains(event.getPlace().getLatLng()));

            if (latLngBounds.contains(event.getPlace().getLatLng())) {
                    cluster.add(event);
            }
        }

        //add the cluster to  clusterList
            clusterList.add(cluster);
        //remove the cluster from origin list
            for(Event event:cluster){
               eventList.remove(event);
            }

        }

        for(List<Event> eventList1 : clusterList){
            Log.d("SS", "clusterEvent: ClusterDivide");
            for(Event event:eventList1){
                Log.d("SS", "clusterEvent: "+event.getPlaceName());
            }
        }

        return clusterList;

    }

    public static List<Double> getDistanceList(List<Event> eventList){

        List<Double> DistanceList = new ArrayList<>();

        for (int i = 0; i < eventList.size() - 1; i++) {
            Place place_1 = eventList.get(0).getPlace();
            Place place_2 = eventList.get(i + 1).getPlace();

            double distance = Place.getDistance(place_1.getLat(), place_1.getLng(), place_2.getLat(), place_2.getLng());

            DistanceList.add(distance);
        }

        return DistanceList;

    }


    public static LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }



}
