package com.example.dn;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestaurantMapper {
    void insertRestaurant(Restaurant restaurant);
}
