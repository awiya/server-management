package io.awiya.servermanagment.services;



import io.awiya.servermanagment.model.Server;

import java.io.IOException;
import java.util.Collection;


public interface ServerService {
    Server createServer(Server server);
    Server pingServer(String ipAddress) throws IOException;
    Collection<Server> allServers(int limit);
    Server getServer(Long id);
    Server updateServer(Server server);
    Boolean deleteServer(Long id);
}
