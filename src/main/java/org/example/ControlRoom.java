package org.example;

import com.pubnub.api.PubNub;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlRoom {
    String user;
    PubNub pubNub;

    Set<String> floating = new HashSet<>();
    Set<String> notFloating = new HashSet<>();

    void updateSets(String user, ShipState newShipState) {
        if(newShipState == ShipState.REPAIR || newShipState == ShipState.SINK) {
            getFloating().remove(user);
            getNotFloating().add(user);
        }
        else {
            getNotFloating().remove(user);
            getFloating().add(user);
        }
    }
}
