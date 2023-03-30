package com.vladimirpandurov.serverManagerB.service;

import com.vladimirpandurov.serverManagerB.domain.Server;
import com.vladimirpandurov.serverManagerB.enumeration.Status;
import com.vladimirpandurov.serverManagerB.repository.ServerRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final ServerRepository serverRepository;

    public Server create(Server server){
        log.info("Saving new server: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return this.serverRepository.save(server);
    }

    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = this.serverRepository.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        return server;
    }

    public Collection<Server> list(int limit){
        log.info("Fetching all servers");
        return this.serverRepository.findAll(PageRequest.of(0, limit)).toList();
    }

    public Server get(Long id){
        log.info("Fetching server by id: {}", id);
        return this.serverRepository.findById(id).get();
    }

    public Server update(Server server){
        log.info("Updating server: {}", server.getName());
        return this.serverRepository.save(server);
    }

    public Boolean delete(Long id){
        log.info("Deleting server by ID: {}", id);
        this.serverRepository.deleteById(id);
        return Boolean.TRUE;
    }

    private String setServerImageUrl(){
        String[] imageNames = { "server1.png", "server2.png", "server3.png", "server4.png"};
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/servers/image/" + imageNames[new Random().nextInt(4)]).toUriString();
    }

}
