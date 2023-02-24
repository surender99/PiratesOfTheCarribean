package org.example;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubMarine {
    String user;
    ShipState shipState;
    PubNub pubNub;

    private String channelName = "submarine";

    PubNubAppUtil pubNubAppUtil;
    public SubMarine(PubNubAppUtil pubNubAppUtil) {
        this.pubNubAppUtil = pubNubAppUtil;

    }

    public void register(String user) throws PubNubException {
        PubNub pubNub = pubNubAppUtil.register(user);
        this.pubNub = pubNub;
        this.user = user;
        shipState = ShipState.FLOAT;
    }


    void changeState(ShipState newShipState) {
        if((shipState == ShipState.REPAIR || shipState == ShipState.SINK) &&
                (newShipState == ShipState.REPAIR || newShipState == ShipState.SINK)) {
            shipState = newShipState;
            return;
        }

        //same float state
        if(shipState.equals(newShipState))
            return;

        JsonObject messagePayload = new JsonObject();
        messagePayload.addProperty("state", newShipState.toString());
        messagePayload.addProperty("recipient", "JackSparrow");
        pubNub.publish().channel(channelName).message(messagePayload).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (!status.isError()) {
                    log.info("Message sent successfully!");
                } else {
                    log.info("Message failed to send: " + status.getErrorData());
                }
            }
        });
        shipState = newShipState;
    }

}
