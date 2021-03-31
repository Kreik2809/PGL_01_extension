package user;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.util.Objects;

/**
 * La classe DataSupplyPoint représente les données d'un point d'approvisionnement/un compteur dans l'application de
 * gestion de portefeuilles.
 */
@JsonIgnoreProperties({ "supplyPoint" })
public class DataSupplyPoint
{
    private String date;
    private double value;
    private SupplyPoint supplyPoint;

    /**
     * constructeur
     * @param date la date
     * @param value la valeur
     * @param supplyPoint le compteur
     */
    public DataSupplyPoint(@JsonProperty("Date") String date, @JsonProperty("Value") double value, SupplyPoint supplyPoint) {
        this.date = date;
        this.value = value;
        this.supplyPoint = supplyPoint;
    }
    /**
     * constructeur
     * @param date la date
     * @param value la valeur
     */
    @JsonCreator
    public DataSupplyPoint(@JsonProperty("Date") String date, @JsonProperty("Value") double value) {
        this.date = date;
        this.value = value;
        supplyPoint = null;
    }

    public DataSupplyPoint() {
        // no argument constructor required by Jackson
    }

    @JsonProperty("Date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("Value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public SupplyPoint getSupplyPoint()
    {
        return supplyPoint;
    }

    public void setSupplyPoint(SupplyPoint supplyPoint)
    {
        this.supplyPoint = supplyPoint;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSupplyPoint that = (DataSupplyPoint) o;
        return Date.valueOf(date).equals(Date.valueOf(that.getDate()));
    }



    @Override
    public int hashCode() {
        return Objects.hash(date, value, supplyPoint);
    }

    @Override
    public String toString() {
        return "DATE : " + date + " / VALUE : " + value;
    }
}
