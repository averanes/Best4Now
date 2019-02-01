package com.adoble.best4now.domain;

public class InputDataCriteria {


    private Place placeCenter;
    private Weather weatherMoment;

    private int sex;
    private int age;
    private int persons;

    public InputDataCriteria(Place placeCenter, Weather weatherMoment, int sex, int age, int persons) {
        this.placeCenter = placeCenter;
        this.weatherMoment = weatherMoment;
        this.sex = sex;
        this.age = age;
        this.persons = persons;
    }

    public InputDataCriteria(int sex, int age, int persons) {
        this.sex = sex;
        this.age = age;
        this.persons = persons;
    }

    public InputDataCriteria() {
        this.age = 1;
    }

    public Place getPlaceCenter() {
        return placeCenter;
    }

    public void setPlaceCenter(Place placeCenter) {
        this.placeCenter = placeCenter;
    }

    public Weather getWeatherMoment() {
        return weatherMoment;
    }

    public void setWeatherMoment(Weather weatherMoment) {
        this.weatherMoment = weatherMoment;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }
}
