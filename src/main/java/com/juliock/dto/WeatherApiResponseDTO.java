package com.juliock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // instrui o Jackson a ignorar campos que existem no JSON mas nao estão presentes aqui na classe
public class WeatherApiResponseDTO {
    private Integer queryCost;
    private String resolvedAddress;
    private String address;
    private String timezone;

    private List<DayWeatherDTO> days = new ArrayList<>();

    public WeatherApiResponseDTO() {
    }

    public WeatherApiResponseDTO(Integer queryCost, String resolvedAddress, String address, String timezone, List<DayWeatherDTO> days) {
        this.queryCost = queryCost;
        this.resolvedAddress = resolvedAddress;
        this.address = address;
        this.timezone = timezone;
        this.days = days;
    }

    public Integer getQueryCost() {
        return queryCost;
    }

    public void setQueryCost(Integer queryCost) {
        this.queryCost = queryCost;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public void setResolvedAddress(String resolvedAddress) {
        this.resolvedAddress = resolvedAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<DayWeatherDTO> getDays() {
        return days;
    }

    public void setDays(List<DayWeatherDTO> days) {
        this.days = days;
    }
}
