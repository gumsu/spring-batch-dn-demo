package com.example.dn.batch.domain;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestaurantMapper {
    void insertRestaurant(Restaurant restaurant);
}
