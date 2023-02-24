package org.example;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;

import java.util.*;
import java.util.stream.Collectors;

public class AppService {
    HashMap<String, SubMarine> map = new HashMap<>();

    PubNubAppUtil pubNubAppUtil;

    ControlRoom controlRoom ;
    AppService() throws PubNubException {
        controlRoom = new ControlRoom();
        controlRoom.setUser("JackSparrow");
        pubNubAppUtil = new PubNubAppUtil(controlRoom);
        PubNub pubNub = pubNubAppUtil.register("JackSparrow");
        controlRoom.setPubNub(pubNub);
        pubNubAppUtil.subscribe(pubNub);
    }

    boolean register(String user) throws PubNubException {
        if(!map.containsKey(user)) {
            SubMarine subMarine = new SubMarine(pubNubAppUtil);
            subMarine.register(user);
            map.put(user, subMarine);
            controlRoom.getFloating().add(user);
            return true;
        }
        return false;
    }

    List<String> getFloatingList() {
        List<String> temp = controlRoom.getFloating().stream().collect(Collectors.toList());
        return temp;
    }

    Map<String, String> getAllStatus() {
        Map<String, String> res = new HashMap<>();
        for (Map.Entry<String,SubMarine> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue().getShipState().toString());
        }
        return res;
    }
    Boolean changeState(String user, ShipState newShipState) {
        if(!map.containsKey(user))
            return false;
        SubMarine subMarine = map.get(user);
        subMarine.changeState(newShipState);
        return true;
    }

    void subscribe(String user) throws PubNubException {
        if(map.containsKey(user)) {
            SubMarine subMarine = map.get(user);
            subMarine.pubNubAppUtil.subscribe(subMarine.getPubNub());
        }
    }
}
