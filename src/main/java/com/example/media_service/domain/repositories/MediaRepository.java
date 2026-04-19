package com.example.media_service.domain.repositories;

public interface MediaRepository {


    public void setDataSource(String url);
    public void insertMediaAsync(String name, String type,String urlRef);


}
