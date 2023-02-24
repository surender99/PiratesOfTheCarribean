package org.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class PubNubAppUtil {

    AppService service;
    ControlRoom controlRoom;

    PubNubAppUtil(ControlRoom controlRoom) {
        this.controlRoom = controlRoom;
    }
    private String subKey = "sub-c-1c4465fa-0650-4541-8a72-4e51a64558e2";

    private String pubKey = "pub-c-9da5a4d7-dea8-4338-9ebd-2587e473dd49";

    private String channelName = "submarine";

     PubNub register(String userId) throws PubNubException {
         PNConfiguration pnConfiguration = new PNConfiguration(userId);

         pnConfiguration.setSubscribeKey(subKey);
         pnConfiguration.setPublishKey(pubKey);

         PubNub pubnub = new PubNub(pnConfiguration);
         return pubnub;
     }

     void unSubscribe(PubNub pubnub) {
         List<String> subscribedChannels = pubnub.getSubscribedChannels();
         if (subscribedChannels.contains(channelName)) {
             return;
         }
         pubnub.unsubscribe().channels(Arrays.asList(channelName)).execute();
     }

     void subscribe(PubNub pubnub) throws PubNubException {
         List<String> subscribedChannels = pubnub.getSubscribedChannels();
         if (subscribedChannels.contains(channelName)) {
             return;
         }

         pubnub.addListener(new SubscribeCallback() {

            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    log.info("User " + pubnub.getConfiguration().getUserId() + "Subscribed to " + channelName);

                } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                    // Handle message decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // Handle new message stored in message.message
                if (message.getChannel() != null) {
                    // Message has been received on channel group stored in
                    // message.getChannel()
                } else {
                    // Message has been received on channel stored in
                    // message.getSubscription()
                }

                JsonElement receivedMessageObject = message.getMessage();
                // extract desired parts of the payload, using Gson
                String msg = message.getMessage().getAsJsonObject().get("state").getAsString();
                log.info("Received by: "+ pubnub.getConfiguration().getUserId()+ " From " + message.getPublisher());
                log.info("new State:" + receivedMessageObject.toString());
                if(pubnub.getConfiguration().getUserId().getValue().equals("JackSparrow"))
                    controlRoom.updateSets(message.getPublisher(), ShipState.valueOf(msg));

                /*
                 * Log the following items with your favorite logger - message.getMessage() -
                 * message.getSubscription() - message.getTimetoken()
                 */
            }

            @Override
            public void presence(@NotNull PubNub pubNub, @NotNull PNPresenceEventResult pnPresenceEventResult) {

            }

            @Override
            public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {

            }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) {

            }

            @Override
            public void channel(@NotNull PubNub pubNub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {

            }

            @Override
            public void membership(@NotNull PubNub pubNub, @NotNull PNMembershipResult pnMembershipResult) {

            }

            @Override
            public void messageAction(@NotNull PubNub pubNub, @NotNull PNMessageActionResult pnMessageActionResult) {

            }

            @Override
            public void file(@NotNull PubNub pubNub, @NotNull PNFileEventResult pnFileEventResult) {

            }
        });

        pubnub.subscribe()
                .channels(Collections.singletonList(channelName))
                .execute();
     }

     void publish(PubNub pubnub) throws PubNubException {
         JsonObject messagePayload = new JsonObject();
         messagePayload.addProperty("text", "Hello, world!");
         PNPublishResult publishResult = pubnub.publish().channel(channelName).message(messagePayload).sync();
         System.out.println("Publish result: " + publishResult.toString());
     }
}