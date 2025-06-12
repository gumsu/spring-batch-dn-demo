package com.example.dn.batch;

import com.example.dn.batch.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RestaurantItemWriter {

    private final SqlSessionTemplate sqlSessionTemplate;

    @Bean
    public ItemWriter<Restaurant> writer() throws Exception {
        SqlSessionTemplate template = sqlSessionTemplate;
        return items -> {
            for (Restaurant item : items) {
                template.insert("insertRestaurant", item);
            }
        };
    }
}
