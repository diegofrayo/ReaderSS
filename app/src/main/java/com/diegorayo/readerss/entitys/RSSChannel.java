package com.diegorayo.readerss.entitys;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 */
public class RSSChannel implements Parcelable {

    /**
     *
     */
    private int id;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private List<RSSLink> listRSSLinks;

    /**
     *
     */
    private Category category;

    /**
     * Fecha y hora de la ultima vez que fue actualizado el canal por el usuario
     */
    private String lastUpdate;

    /**
     * Es la fecha del RSSLink mas reciente del canal
     */
    private String dateLastRSSLink;

    /**
     * @param url
     * @param name
     */
    public RSSChannel(String url, String name) {

        this.url = url;
        this.name = name;
    }

    public RSSChannel(Parcel in) {

        readFromParcel(in);
    }

    public RSSChannel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RSSLink> getListRSSLinks() {
        return listRSSLinks;
    }

    public void setListRSSLinks(List<RSSLink> listRSSLinks) {
        this.listRSSLinks = listRSSLinks;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDateLastRSSLink() {
        return dateLastRSSLink;
    }

    public void setDateLastRSSLink(String dateLastRSSLink) {
        this.dateLastRSSLink = dateLastRSSLink;
    }

    public String getWebsite() {

        String domain = "";

        if (!getUrl().contains("co") && !getUrl().contains("com")
                && !getUrl().contains("net") && !getUrl().contains("es")
                && !getUrl().contains("fm")) {

            domain = ".com";
        }

        return "www."
                + getUrl().substring(0, getUrl().indexOf("/", 7))
                .replaceAll("http://", "").replaceAll("www.", "")
                .replaceAll("rss.", "").replaceAll("feeds.", "")
                + domain;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeValue(category);
        dest.writeString(lastUpdate);
        dest.writeString(dateLastRSSLink);
    }

    /**
     * Clase para recuperar los datos de un parcel, IMPORTANTE leerlos en
     * el mismo orden que se escribieron!
     *
     * @param in Parcel con los datos a leer
     */
    private void readFromParcel(Parcel in) {

        id = in.readInt();
        url = in.readString();
        name = in.readString();
        category = (Category) in.readValue(Category.class.getClassLoader());
        lastUpdate = in.readString();
        dateLastRSSLink = in.readString();
    }

        public static final Parcelable.Creator<RSSChannel> CREATOR = new
            Parcelable.Creator<RSSChannel>() {
                public RSSChannel createFromParcel(Parcel in) {
                    return new RSSChannel(in);
                }

                public RSSChannel[] newArray(int size) {
                    return new RSSChannel[size];
                }
            };

}
