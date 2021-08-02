package com.example.myapp.routes;

import android.os.Parcelable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import java.util.List;

public class RouteViewModel extends ViewModel {

    public static int current_fragment = 0;
    public static int CURRENT_PAGE_ROUTE_BROWSE_FRAGMENT = 0;
    public static int CURRENT_PAGE_CALORIES_FRAGMENT = 1;
    public static int CURRENT_PAGE_WORKOUT_LIST_FRAGMENT = 2;
    public static int CURRENT_PAGE_PLAYLISTS_FRAGMENT = 3;
    public static int CURRENT_PAGE_ROUTE_DETAILS_FRAGMENT = 4;
    public static int CURRENT_PAGE_WORKOUT_CREATE_FRAGMENT = 6;
    public static int CURRENT_PAGE_WORKOUT_START_FRAGMENT = 7;
    public static int CURRENT_PAGE_REMINDER_FRAGMENT = 8;
    public static int CURRENT_PAGE_STATISTICS_FRAGMENT = 9;
    public static int CURRENT_PAGE_MY_GEOFENCES = 10;
    public int show_once = 1;

    public static NavController navController;
    private List<Route> routes;
    static public Parcelable state = null;

    private MutableLiveData<Route> selectedRoute = new MutableLiveData<>(null);

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public LiveData<Route> getSelectedRoute() {
        return selectedRoute;
    }

    public void setSelectedRoute(Route selectedRoute) {
        this.selectedRoute.setValue(selectedRoute);
    }
}
