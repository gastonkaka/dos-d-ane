package yalantis.com.sidemenu.sample.Entity;

/**
 * Created by Jarraya on 24/11/2015.
 */
public class Anomalie {

    float longi;
    float lati;
    int exsistance;
    String type;
    String direction;
    String photo;

    public Anomalie() {
    }

    public Anomalie(int exsistance, String direction, float lati, float longi, String photo, String type) {
        this.exsistance = exsistance;
        this.direction = direction;
        this.lati = lati;
        this.longi = longi;
        this.photo = photo;
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getExsistance() {
        return exsistance;
    }

    public void setExsistance(int exsistance) {
        this.exsistance = exsistance;
    }

    public float getLati() {
        return lati;
    }

    public void setLati(float lati) {
        this.lati = lati;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public float getLongi() {
        return longi;
    }

    public void setLongi(float longi) {
        this.longi = longi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Anomalie{" +
                "direction='" + direction + '\'' +
                ", longi=" + longi +
                ", lati=" + lati +
                ", exsistance=" + exsistance +
                ", type='" + type + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
