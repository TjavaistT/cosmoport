package com.space.model;

import com.space.model.Exceptions.NotRealCrewSize;
import com.space.model.Exceptions.NotRealMaxSpeedException;
import com.space.model.Exceptions.NotRealProdDateException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "ship")
@NamedQuery(name = "Ship.findAllCustom", query = "select s from Ship s")
public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double USED = 0.5;
    public static final int NEW = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "planet")
    private String planet;

    @Column(name = "shipType")
    private String shipType;

    @Temporal(TemporalType.DATE)
    @Column(name = "prodDate")
    private Date prodDate;

    @Column(name = "isUsed")
    private Boolean isUsed;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "crewSize")
    private Integer crewSize;

    @Column(name = "rating")
    private Double rating;

    public Ship() { }

    public Ship(long id, String name, String planet, String shipType, Date prodDate, Boolean isUsed, double speed, int crewSize) {
        setId(id);
        setName(name);
        setPlanet(planet);
        setShipType(shipType);
        setProdDate(prodDate);
        setIsUsed(isUsed);
        setSpeed(speed);
        setCrewSize(crewSize);
    }

    public String getShipType() {
    return shipType;
    }

    public void setShipType(String shipType) {
    this.shipType = shipType;
    }

    public Long getId() {
    return id;
    }

    public void setId(Long id) {
    this.id = id;
    }

    public String getName() {
    return name;
    }

    public void setName(String name) {
    this.name = name;
    }

    public String getPlanet() {
    return planet;
    }

    public void setPlanet(String planet) {
    this.planet = planet;
    }

    private Date getProdDate() {
    return prodDate;
    }

    public void setProdDate(Date prodDate) {

    int year = getYear(prodDate);

    if (ProdDate.MIN <= year && year <= ProdDate.MAX) {
      this.prodDate = prodDate;
      setRating();
    } else {
      throw new NotRealProdDateException();
    }
    }

    public ProdDate getPropProdDate() {
    return new ProdDate(getProdDate());
    }

    public Boolean isUsed() {
    return isUsed;
    }

    public void setIsUsed(Boolean used) {
    this.isUsed = used;
    setRating();
    }

    public Double getSpeed() {
    return speed;
    }

    public void setSpeed(Double speed) {

    speed = round(speed, 2);

    if (Speed.MIN <= speed && speed <= Speed.MAX) {
      this.speed = speed;
      setRating();
    } else {
      throw new NotRealMaxSpeedException();
    }
    }

    private Integer getCrewSize() {
    return crewSize;
    }

    public void setCrewSize(Integer crewSize) {

    if (CrewSize.MIN <= crewSize && crewSize <= CrewSize.MAX) {
      this.crewSize = crewSize;
    } else {
      throw new NotRealCrewSize();
    }
    }

    public CrewSize getPropCrewSize(){
    return new CrewSize(getCrewSize());
    }

    public Double getRating() {
    setRating();
    return rating;
    }

    public void setRating() {

    if (
            getSpeed() == null ||
            isUsed() == null ||
            prodDate == null
    ) {
      return;
    }

    double koefficientOfUsed = isUsed() ? USED : NEW;
    int currentYear = ProdDate.MAX;
    int prodYear = getYear(prodDate);

    Double computeRating = null;
    try {
      computeRating = (80 * getSpeed() * koefficientOfUsed)/(currentYear - prodYear + 1);
      this.rating = round(computeRating, 3);
    } catch (ArithmeticException e) {
      double result = (currentYear - prodYear + 1);
      System.out.println("нельзя делить на ноль");
      System.out.println("currentYear - prodYear + 1 = " + result );
      e.printStackTrace();
    }
    }

    private double round(Double decimalNumber, int numberDecimals) {
      return BigDecimal.valueOf(decimalNumber).round(new MathContext(numberDecimals, RoundingMode.HALF_EVEN)).doubleValue();
    }

    private int getYear(Date prodDate) {
        Calendar prodCalendar = Calendar.getInstance();
        prodCalendar.setTime(prodDate);

        return prodCalendar.get(Calendar.YEAR);
    }

    public String toString() {
    return "Ship{id=" + id +
      ", name=" + name +
      ", planet=" + planet +
      ", shipType=" + shipType +
      ", prodDate=" + prodDate +
      ", isUsed=" + isUsed +
      ", speed=" + speed +
      ", crewSize=" + crewSize +
      ", rating=" + rating +
      "}";
    }

    public Map<String, String> toJsonMap() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("id", String.valueOf(id));
    map.put("name", name);
    map.put("planet", planet);
    map.put("shipType", shipType);
    map.put("prodDate", String.valueOf(prodDate.getTime()));
    map.put("isUsed", String.valueOf(isUsed));
    map.put("speed", String.valueOf(speed));
    map.put("crewSize", String.valueOf(crewSize));
    map.put("rating", String.valueOf(rating));

    return map;
    }

    public class ProdDate extends ShipProps{

      public static final int MIN = 2800;
      public static final int MAX = 3019;

      ProdDate(Date prodDate) {
        super(getYear(prodDate));
      }

    }

    public class CrewSize extends ShipProps{

      public static final int MIN = 1;
      public static final int MAX = 9999;

      CrewSize(Integer size){
        super(size);
      }

    }

    public class Speed extends ShipProps{

    public static final double MIN = 0.01;
    public static final double MAX = 0.99;

    Speed(Double size){
      super(size);
    }

    }

}