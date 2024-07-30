package com.cesar.ChatWeb.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.cesar.ChatWeb.model.Message;
import com.cesar.ChatWeb.repository.ConversationRepository;
import com.cesar.ChatWeb.repository.MessageRepository;
import com.cesar.ChatWeb.repository.UserRepository;

@Controller
public class TCP_controller {

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


// [[[[[[[[[[[ Pending code ]]]]]]]]]]]
	
//	@MessageMapping("/obtenerListaConversaciones")
//	public void obtenerListaConversaciones(Long id) {
//
//		List<Conversacion> conversaciones = conversacionRepo.findAllByUserID(id);
//
//		simp.convertAndSend("/user/" + id + "/queue/conversaciones", conversaciones);
//	}





	@MessageMapping("/sendMessage")
	public void sendMessage(StompHeaderAccessor headers, Message message) {

		//Assign headers.
		
		Map<String, Object> nativeHeaders = new HashMap<>();
		
		nativeHeaders.put("name", headers.toNativeHeaderMap().get("name").get(0));
		nativeHeaders.put("imageName", headers.toNativeHeaderMap().get("imageName").get(0));
		
		//Destination.
		String sendingDestination= "/user/" + message.getRecipientId() + "/queue/getMessage";
		
		//Save message in DB [[[[[[ Pending code ]]]]]]]]
//		mensajeRepo.save(message);
		
		//Send.
		simp.convertAndSend(sendingDestination, message, nativeHeaders);
	}




// [[[[[[[[ Pending code ]]]]]]]]
	
//	@MessageMapping("/crearConversacion")
//	public void crearConversacion(Conversacion conversacion) {
//		conversacionRepo.save(conversacion);
//	}
//

	
	
// [[[[[[[[ Pending code ]]]]]]]]
	
//
//
//	@MessageMapping("/eliminarConversacion")
//	public void eliminarConversacion(Long id) {
//		conversacionRepo.deleteById(id);
//	}


	
	
// [[[[[[[[ Pending code ]]]]]]]]
	
//	@MessageMapping("/actualizarMensajesNuevos")
//	public void actualizarMensajesNuevos(Map<String, Object> datos) {
//
//		conversacionRepo.updateMensajesNuevosByIDs(
//			(Long) datos.get("idRemitente"),
//			(Long) datos.get("idDestintatario"),
//			(Integer) datos.get("mensajesNuevos")
//		);
//	}

	
	
	
	
	
// [[[[[[[[ Pending code ]]]]]]]]
	
//	@MessageMapping("/obtenerListaMensajes")
//	public void obtenerListaMensajes(Map<String, Long> ids) {
//
//		Long idRemitente = ids.get("idRemitente");
//		Long idDestinatario = ids.get("idDestinatario");
//
//		List<Mensaje> listaMensajes = mensajeRepo.findAllByIDs(idRemitente, idDestinatario);
//
//		simp.convertAndSend("/user/" + idRemitente + "/queue/mensajes", listaMensajes);
//	}

	
	
	
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
	
	
	
	//Variables e instances

	@Autowired
	private SimpMessagingTemplate simp;

	@Autowired
	private ConversationRepository conversationRepo;

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private UserRepository userRepo;

	private Map<Long, OnlineUser> onlineUsers = new HashMap<>();
	
	private String actionPerformed;
}
