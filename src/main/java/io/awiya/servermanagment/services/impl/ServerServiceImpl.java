package io.awiya.servermanagment.services.impl;

import io.awiya.servermanagment.exceptions.ServerNotFoundException;
import io.awiya.servermanagment.model.Server;
import io.awiya.servermanagment.repositories.ServerReposiroty;
import io.awiya.servermanagment.services.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Random;


import static io.awiya.servermanagment.enums.Status.SERVER_DOWN;
import static io.awiya.servermanagment.enums.Status.SERVER_UP;
import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;


@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {

    private final ServerReposiroty serverReposiroty;

    @Override
    public Server createServer(Server server) {
        log.info("New server has been saved: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverReposiroty.save(server);
    }

    @Override
    public Server pingServer(String ipAddress) throws IOException {
        log.info("Pinging server with IP address: {}", ipAddress);
        Server server = serverReposiroty.findByIpAddress(ipAddress);
        if (server == null) {
            throw new ServerNotFoundException("The requested server could not be found.");
        }
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? SERVER_UP : SERVER_DOWN);
        serverReposiroty.save(server);
        return server;
    }

    @Override
    public Collection<Server> allServers(int limit) {
        log.info("Fetching all servers");
        return serverReposiroty.findAll(of(0, limit)).toList();
    }

    @Override
    public Server getServer(Long id) {
        log.info("Fetching server by id: {}", id);
        return serverReposiroty.findById(id)
                .orElseThrow(()-> new ServerNotFoundException("The requested server could not be found."));
    }

    @Override
    public Server updateServer(Server server) {
        log.info("Updating server: {}", server.getName());
        return serverReposiroty.save(server);
    }

    @Override
    public Boolean deleteServer(Long id) {
        log.info("Deleting server by ID: {}", id);
        serverReposiroty.deleteById(id);
        return TRUE;
    }

    private String setServerImageUrl() {
        String[] imageNames = { "server1.png", "server2.png", "server3.png", "server4.png" };
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/server/image/" + imageNames[new Random().nextInt(4)])
                .toUriString();
    }

    private boolean isReachable(String ipAddress, int port, int timeOut) {
        try {
            try(Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipAddress, port), timeOut);
            }
            return true;
        }catch (IOException exception){
            return false;
        }
    }
}