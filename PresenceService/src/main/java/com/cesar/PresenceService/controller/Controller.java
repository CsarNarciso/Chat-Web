package com.cesar.PresenceService.controller;

import com.cesar.PresenceService.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/onlineUsers.api")
public class Controller {

    @MessageMapping("/updateOnlineUsers")
    public void updateOnlineUsers(OnlineUser user) {

        System.out.println("-----------------------updateAndGetOnlineUsers-----------------------" );

        //Necessary instances.
        String update = "";

        //Get user data.
        String status = user.getStatus();
        Long id = user.getId();


        System.out.println("Status: " + status);


        if ( status.equals("CONNECTED") ) {

            //Reconnect.
            if ( onlineUsers.containsKey(id) ) {

                OnlineUser disconnectedUser = onlineUsers.get(id);
                System.out.println(disconnectedUser.getName());

                if ( ! disconnectedUser.getStatus().equals(status) ) {

                    //Disconnected time.
                    if ( System.currentTimeMillis() - disconnectedUser.getDisconnectionHour() < 5000 ) {

                        //Correct.
                        actionPerformed = "Reconnect";
                        onlineUsers.replace(id, user);
                        simp.convertAndSend("/user/" + id + "/queue/getOnlineUsers", onlineUsers);
                    }
                }
            }
            //Connect.
            else {

                actionPerformed = "Add";
                onlineUsers.put(id, user);
                System.out.println(user.getName());

                simp.convertAndSend("/user/" + id + "/queue/getOnlineUsers", onlineUsers);
                simp.convertAndSend("/topic/updateOnlineUsers", user);
            }
        }
        else if ( status.equals("DISCONNECTED") ) {

            //Disconnect.
            if ( onlineUsers.containsKey(id) ) {

                if ( ! onlineUsers.get(id).getStatus().equals(status) ) {

                    //Save disconnection hour.
                    onlineUsers.get(id).setDisconnectionHour(System.currentTimeMillis());
                    onlineUsers.get(id).setStatus(status);

                    actionPerformed = "WaitReconnection";

                    //Timer: wait for reconnection.
                    new Timer().schedule(new TimerTask() {

                        private Long disconnectedUserId = id;
                        private int counter = 0;

                        @Override
                        public void run() {

                            if ( counter > 0 ) {

                                //If user doesn't reconnect...
                                if ( onlineUsers.get(disconnectedUserId).getStatus().equals("DISCONNECTED") ) {

                                    //Remove them.
                                    onlineUsers.remove(id);

                                    actionPerformed = "Remove";

                                    //Send update to all users.
                                    simp.convertAndSend("/topic/updateOnlineUsers", user);

                                    System.out.println("Action performed: " + actionPerformed);
                                }
                                //Stop timer.
                                cancel();
                            }
                            counter++;
                        }
                    }, 0, 5000);
                }
            }
        }

        //If there's an update...
        else if ( status.contains("UPDATE") ) {

            //Update.
            actionPerformed = status;

            //Send update to all users.
            simp.convertAndSend("/topic/updateOnlineUsers", user);

            //Reconnect.
            user.setStatus("CONNECTED");
            onlineUsers.replace(id, user);

            //Send actual/updated online users list to main user.
            simp.convertAndSend("/user/" + id + "/queue/getOnlineUsers", onlineUsers);
        }

        System.out.println("Action performed: " + actionPerformed);
        System.out.println("-------------------------------------------------------" );
    }

    //Intern classes

    private static class OnlineUser {

        private Long id;
        private String status;
        private String name;
        private String imageName;
        private Long disconnectionHour;

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getImageName() {
            return imageName;
        }
        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
        public Long getDisconnectionHour() {
            return disconnectionHour;
        }
        public void setDisconnectionHour(Long disconnectionHour) {
            this.disconnectionHour = disconnectionHour;
        }
    }

    @PostMapping("/v1/{name}")
    public ResponseEntity<?> addOnlineUsers(@PathVariable String name){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.addOnlineUser(name));
    }

    @GetMapping("/v1/{index}")
    public ResponseEntity<?> getOnlineUser(@PathVariable int index){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getOnlineUser(index));
    }

    @GetMapping("/v1")
    public ResponseEntity<?> getOnlineUsers(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getOnlineUsers());
    }

    @DeleteMapping("/v1/{index}")
    public ResponseEntity<?> removeOnlineUser(@PathVariable int index){
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.removeOnlineUser(index));
    }

    @Autowired
    private PresenceService service;


    private Map<Long, OnlineUser> onlineUsers = new HashMap<>();

    private String actionPerformed;

    @Autowired
    private SimpMessagingTemplate simp;
}
