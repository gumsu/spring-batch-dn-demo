package com.example.dn;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RestaurantRepository {
    void insertRestaurant(Restaurant restaurant);
}
