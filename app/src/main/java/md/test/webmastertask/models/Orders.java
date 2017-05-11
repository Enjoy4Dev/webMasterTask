package md.test.webmastertask.models;


import android.util.Log;

public class Orders {
    private String atibuteOrderId;
    public Orders() {
    }


//
//    public Orders (String trackId, String artistName, String primaryGenreName, String artworkUrl60,
//                  String trackName, String trackTimeMillis, String trackPrice) {
//        this.atibuteTrackId = trackId;
//        this.atributeArtistName = artistName;
//        this.atributeGenreName = primaryGenreName;
//        this.atributeTrackImageUrl = artworkUrl60;
//        this.atributeTrackName = trackName;
//        this.atributeTrackTimeMillis = trackTimeMillis;
//        this.atributeTrackPrice = trackPrice;
//    }
//
//    public String getOrderId() {
//        return atributeOrderId;
//    }
//
//    public void setOrderId(String artistName) {
//        this.atributeOrderId = artistName;
//    }

    public String getOrderId() {
        return atibuteOrderId;
    }

    public void setOrderId(String orderId) {
        this.atibuteOrderId = orderId;
        Log.i("setOrderId 2", orderId);
    }

//    public String getGenreName() {
//        return atributeGenreName;
//    }
//
//    public void setGenreName(String primaryGenreName) {
//        this.atributeGenreName = primaryGenreName;
//    }
//
//    public String getImageUrl() {
//        return atributeTrackImageUrl;
//    }
//
//    public void setImageUrl(String artworkUrl60) {
//        this.atributeTrackImageUrl = artworkUrl60;
//    }
//
//    public String getTrackPrice() {
//        return atributeTrackPrice;
//    }
//
//    public void setTrackPrice(String trackPrice) {
//        this.atributeTrackPrice = trackPrice;
//    }
//
//    public String getDurationTime() {
//        return atributeTrackTimeMillis;
//    }
//
//    public void setDurationTime(String trackTimeMillis) {
//        this.atributeTrackTimeMillis = trackTimeMillis;
//    }
//
//    public String getTrackName() {
//        return atributeTrackName;
//    }
//
//    public void setTrackName(String trackName) {
//        this.atributeTrackName = trackName;
//    }
//
//
//    public String getArtistNameTrackName() {
//        atributeTrackNameSongName = atributeArtistName + " - " + atributeTrackName;
//        return atributeTrackNameSongName;
//    }
}
