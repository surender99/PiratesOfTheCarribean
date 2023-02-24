package org.example;

import com.pubnub.api.PubNubException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppController {

    AppService appService = new AppService();

    public AppController() throws PubNubException {
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Pirates Of Carrebean";
    }

    @GetMapping("/getList")
    public List<String> getList() {
        return appService.getFloatingList();
    }

    @GetMapping("/getAllStatus")
    public Map<String, String> getAllStatus() {
        Map<String, String> map = appService.getAllStatus();
        return appService.getAllStatus();
    }

    @GetMapping("/register/{user}")
    public String register(@PathVariable String user) throws PubNubException {
        Boolean result = appService.register(user);
        if(result)
            return "User Registered";

        return "User Already Registered";
    }

    //current Ship is changing it's own state
    @PostMapping ("/changeState/")
    public String changeStatus(@RequestBody PostDataInput pdi) throws PubNubException {
        Boolean res = appService.changeState(pdi.user, ShipState.valueOf(pdi.state));
        if(res)
            return "Status Changed";
        return "User not registered";
    }

    //JackSparrow is publishing which to change
    //PostDataInput consists of destination user and it's new state
    @PostMapping("/publish")
    public String publish(@RequestBody PostDataInput pdi) {
        if(pdi.user.equals("JackSparrow"))
            return "Invalid";

        Boolean res = appService.changeState(pdi.user, ShipState.valueOf(pdi.state));
        if(res)
            return "Status Changed";
        return "User not registered";
    }

}
