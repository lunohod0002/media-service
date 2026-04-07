package com.example.media_service.business.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Async;

import javax.sql.DataSource;

public interface MediaRepository {


    public void setDataSource(String url);
    public void insertMediaAsync(String name, String type,String urlRef);


}
