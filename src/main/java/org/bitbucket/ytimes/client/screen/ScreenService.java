package org.bitbucket.ytimes.client.screen;

import org.bitbucket.ytimes.client.main.WSServer;
import org.bitbucket.ytimes.client.screen.record.ScreenInfoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreenService {

    @Autowired
    private WSServer wsServer;

    private static ScreenInfoRecord info;

    public void setInfo(ScreenInfoRecord record) {
        info = record;
        wsServer.refreshAll();
    }

    public ScreenInfoRecord getInfo() {
        return info;
    }

}
