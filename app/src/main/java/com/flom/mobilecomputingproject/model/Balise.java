package com.flom.mobilecomputingproject.model;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Balise {

    private final LatLng coordonees;
    private final String titre, date;
    private Marker marqueur = null;

    public Balise(LatLng coordonees, String titre, String date) {
        this.coordonees = coordonees;
        this.titre = titre;
        this.date = date;
    }

    public Marker creerMarqueur(GoogleMap pMap) {
        marqueur = pMap.addMarker(new MarkerOptions().position(coordonees).title(titre).snippet(date));
        /*pMap.addCircle(new CircleOptions()
                .center(new LatLng(coordonees.latitude, coordonees.longitude))
                .radius(10)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));*/
        return marqueur;
    }

    public Marker creerMarqueurTemp(GoogleMap pMap) {
        marqueur = pMap.addMarker(new MarkerOptions().position(coordonees).title(titre).snippet(date));
        marqueur.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        return marqueur;
    }

    public void supprimerMarqueur() {
        marqueur.remove();
    }
}
