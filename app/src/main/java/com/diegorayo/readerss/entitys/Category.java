package com.diegorayo.readerss.entitys;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Diego Rayo
 * @version 2 <br />
 */
public class Category implements Parcelable {

    private int id;

    private String name;

    private List<RSSChannel> rssChannelsList;

    public Category() {

    }

    public Category(String name) {

        this.name = name;
    }

    public Category(Parcel in) {

        readFromParcel(in);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    /**
     * Escribir a un parcel, OJO el orden es importante, es como escribir
     * en un archivo binario
     *
     * @param dest  Parcel donde se va a escribir
     * @param flags ver documentacion de Parcelable.writeToParcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(name);
    }

    /**
     * Clase para recuperar los datos de un parcel, IMPORTANTE leerlos en
     * el mismo orden que se escribieron!
     *
     * @param in Parcel con los datos a leer
     */
    private void readFromParcel(Parcel in) {

        id = in.readInt();
        name = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new
            Parcelable.Creator<Category>() {
                public Category createFromParcel(Parcel in) {
                    return new Category(in);
                }

                public Category[] newArray(int size) {
                    return new Category[size];
                }
            };

}
