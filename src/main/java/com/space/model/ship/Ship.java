package com.space.model.ship;

import com.space.model.ship.validation.YearRange;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "ship")
@NamedQuery(name = "Ship.findAllCustom", query = "select s from Ship s")
public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;

    public interface Create{}
    public interface Update{}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @NotNull(groups = {Create.class})
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    private String name;

    @NotNull(groups = {Create.class})
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    private String planet;

    @NotNull(groups = Create.class)
    private String shipType;

    @NotNull(groups = Create.class)
    @YearRange(min = ProdDate.MIN, max = ProdDate.MAX, groups = {Create.class, Update.class})
    @Temporal(TemporalType.DATE)
    private Date prodDate;

    private Boolean isUsed;

    @NotNull(groups = Create.class)
    @DecimalMin(value = Speed.MIN, groups = {Create.class, Update.class})
    @DecimalMax(value = Speed.MAX, groups = {Create.class, Update.class})
    private Double speed;

    @NotNull(groups = Create.class)
    @Min(value = CrewSize.MIN, groups = {Create.class, Update.class} )
    @Max(value = CrewSize.MAX, groups = {Create.class, Update.class} )
    private Integer crewSize;

    private Double rating;

    public Ship() { }

    public ShipType getShipType() {
        return shipType == null ? null : ShipType.valueOf(shipType);
    }

    @Enumerated(EnumType.STRING)
    public void setShipType(ShipType shipType) {
        this.shipType = String.valueOf(shipType);
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

    public Date getProdDate() {
    return prodDate;
    }

    public void setProdDate(Date prodDate) {
      this.prodDate = prodDate;
      setRating();
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
      this.speed = speed;
      setRating();
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
      this.crewSize = crewSize;
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

    double koefficientOfUsed = isUsed() ? 0.5 : 1;
    int currentYear = ProdDate.MAX;
    int prodYear = getYear(prodDate);

    try {
        Double computeRating = (80 * getSpeed() * koefficientOfUsed)/(currentYear - prodYear + 1);
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

    }

    public class CrewSize extends ShipProps{
      public static final int MIN = 1;
      public static final int MAX = 9999;

    }

    public class Speed extends ShipProps{
        public static final String MIN = "0.01";
        public static final String MAX = "0.99";

    }

}