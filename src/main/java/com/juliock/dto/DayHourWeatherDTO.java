package com.juliock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true) // instrui o Jackson a ignorar campos que existem no JSON mas nao estão presentes aqui na classe
public class DayHourWeatherDTO {
    private String datetime;
    private Double temp;
    private Double feelslike;
    private Double precip;
    private Double precipprob;

    public DayHourWeatherDTO() {
    }

    public DayHourWeatherDTO(String datetime, Double temp, Double feelslike, Double precip, Double precipprob) {
        this.datetime = datetime;
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

}
