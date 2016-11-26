
public class Flare {
    private String flareID, imagename,flareText, classification, userName;
    private long latitude, longtitude;


    public Flare(){
        flareID = "";
        imagename = "";
        flareText = "";
        classification = "";
        latitude = 0L;
        longtitude = 0L;
    }

    public String getFlareID() {
        return flareID;
    }

    public void setFlareID(String flareID) {
        this.flareID = flareID;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getFlareText() {
        return flareText;
    }

    public String getClassification() {
        return classification;
    }
    public void setClassification(String classification){
        this.classification = classification;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(long longtitude) {
        this.longtitude = longtitude;
    }
}
