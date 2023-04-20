package io.awiya.servermanagment.repositories;


import io.awiya.servermanagment.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerReposiroty extends JpaRepository<Server, Long> {
    Server findByIpAddress(String ipAddress);
}
