package com.example.media_service.repositories;


import com.example.media_service.models.Media;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public class MediaRepository {
    private JdbcTemplate jdbcTemplate;



    public void setDataSource(String url) {
        String user = "postgres";
        String password = "postgres";
        DataSource dataSource = new DriverManagerDataSource(url, user, password);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Async
    public void insertMediaAsync(String name, String type,String urlRef) {
            try {
                String connectionString = "jdbc:postgresql://localhost:5432/postgres";

                setDataSource(connectionString);

                String sql = "INSERT INTO medias (name, type, url_ref) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql,
                        name,
                        type,
                        urlRef
                );
            } catch (Exception ex) {
                System.err.println("Ошибка при вставке пользователя: " + ex.getMessage());
                ex.printStackTrace();
            }
        };
    }
