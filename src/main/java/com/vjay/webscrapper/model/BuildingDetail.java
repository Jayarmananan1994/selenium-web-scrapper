package com.vjay.webscrapper.model;

public class BuildingDetail {
    private final  String propertyTitle;
    private final String heading;
    private final String price;
    private final String buildingType;

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public String getHeading() {
        return heading;
    }

    public String getPrice() {
        return price;
    }

    public String getBuildingType() {
        return buildingType;
    }



    public BuildingDetail(String propertyTitle, String heading, String price, String buildingType) {
        this.propertyTitle = propertyTitle;
        this.heading = heading;
        this.price = price;
        this.buildingType = buildingType;
    }


}
