import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Score {
    private int score;
    private double latitude;
    private double longitude;
    private long timestamp;

    public Score(int score, double latitude, double longitude) {
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
    }

    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}