package io.magics.baking.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Steps implements Parcelable {

    private double id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public static final Parcelable.Creator<Steps> CREATOR = new Creator<Steps>() {
        @Override
        public Steps createFromParcel(Parcel source) {
            return new Steps(source);
        }

        @Override
        public Steps[] newArray(int size) {
            return new Steps[size];
        }
    };

    public Steps(Parcel in) {
        this.id = (double) in.readValue(double.class.getClassLoader());
        this.shortDescription = (String) in.readValue(String.class.getClassLoader());
        this.description = (String) in.readValue(String.class.getClassLoader());
        this.videoURL = (String) in.readValue(String.class.getClassLoader());
        this.thumbnailURL = (String) in.readValue(String.class.getClassLoader());
    }

    public double getId() { return id; }
    public String getShortDescription() { return shortDescription; }
    public String getDescription() { return description; }
    public String getVideoURL() { return videoURL; }
    public String getThumbnailURL() { return thumbnailURL; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(shortDescription);
        dest.writeValue(description);
        dest.writeValue(videoURL);
        dest.writeValue(thumbnailURL);
    }
}
