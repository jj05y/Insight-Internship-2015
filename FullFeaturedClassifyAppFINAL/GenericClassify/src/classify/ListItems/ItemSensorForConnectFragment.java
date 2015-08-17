package classify.ListItems;

import android.view.View;

import com.shimmerresearch.android.Shimmer;

import java.io.Serializable;

import classify.Constants.C;


public class ItemSensorForConnectFragment implements Serializable {
    String name;
    String savedAddress;
    Shimmer sensor;

    public ItemSensorForConnectFragment(String name, String savedAddress, Shimmer sensor) {
        this.name = name;
        this.savedAddress = savedAddress;
        this.sensor = sensor;
    }

    public boolean isConnected() {
        return sensor.getShimmerState() == Shimmer.STATE_CONNECTED;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSavedAddress() {
        return savedAddress;
    }

    public void setSavedAddress(String savedAddress) {
        this.savedAddress = savedAddress;
    }

    public Shimmer getSensor() {
        return sensor;
    }

    public void setSensor(Shimmer sensor) {
        this.sensor = sensor;
    }


}
