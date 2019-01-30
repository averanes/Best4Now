package com.adoble.best4now.domain;

import java.sql.Timestamp;

public class Weather {

    private Timestamp day;
    private int id;
    private String mainWeather;

    /*
    0 (despejado - tomaremos rango de los id q empiecen con 8)
    1 (poca lluvia - rango de id q empiecen con 3)
    2 (mucha lluvia  - rango de id q empiecen con 5)
    3 (nieve - rango de id q empiecen con 6)
    4 (catastrofe - consideramos lo demas, serian id q comienzan con 2 y con 7)
    */
    private String weatherDescription;

    /*
    -1 (muy baja - por debajo de cero grados celsius)
    0 (baja - entre 0 y 15 grados)
    1 (normal - entre 16 - 25)
    2 (alta - por encima de 25)
    */
    private double temperature;

    // obtener el icono es a traves de http://openweathermap.org/img/w/"icon".png
    private String icon;

    private int weatherConsideration;
    private int temperatureConsideration;


    public Weather() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainWeather() {
        return mainWeather;
    }

    public void setMainWeather(String mainWeather) {
        this.mainWeather = mainWeather;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Timestamp getDay() {
        return day;
    }

    public void setDay(Timestamp day) {
        this.day = day;
    }

    public int getWeatherConsideration() {
        return weatherConsideration;
    }

    public void setWeatherConsideration(int weatherConsideration) {
        this.weatherConsideration = weatherConsideration;
    }

    public int getTemperatureConsideration() {
        return temperatureConsideration;
    }

    public void setTemperatureConsideration(int temperatureConsideration) {
        this.temperatureConsideration = temperatureConsideration;
    }
}
