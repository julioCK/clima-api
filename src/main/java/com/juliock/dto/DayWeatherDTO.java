package com.juliock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true) // instrui o Jackson a ignorar campos que existem no JSON mas nao estão presentes aqui na classe
public class DayWeatherDTO {

    private String datetime;
    private Double tempmax;
    private Double tempmin;
    private Double temp;
    private Double feelslike;
    private Double precip;
    private Double precipprob;

    private List<DayHourWeatherDTO> hours = new ArrayList<>();

    public DayWeatherDTO() {
    }

    public DayWeatherDTO(String datetime, Double tempmax, Double tempmin, Double temp, Double feelslike, Double precip, Double precipprob) {
        this.datetime = datetime;
        this.tempmax = tempmax;
        this.tempmin = tempmin;
        this.temp = temp;
        this.feelslike = feelslike;
        this.precip = precip;
        this.precipprob = precipprob;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Double getTempmax() {
        return tempmax;
    }

    public void setTempmax(Double tempmax) {
        this.tempmax = tempmax;
    }

    public Double getTempmin() {
        return tempmin;
    }

    public void setTempmin(Double tempmin) {
        this.tempmin = tempmin;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getFeelslike() {
        return feelslike;
    }

    public void setFeelslike(Double feelslike) {
        this.feelslike = feelslike;
    }

    public Double getPrecip() {
        return precip;
    }

    public void setPrecip(Double precip) {
        this.precip = precip;
    }

    public Double getPrecipprob() {
        return precipprob;
    }

    public void setPrecipprob(Double precipprob) {
        this.precipprob = precipprob;
    }

    public List<DayHourWeatherDTO> getHours() {
        return hours;
    }

    public void setHours(List<DayHourWeatherDTO> hours) {
        this.hours = hours;
    }
}
