package com.cesar.ChatServer.controller;

import com.cesar.ChatServer.dto.SendMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@org.springframework.stereotype.Controller
public class Controller {




// [[[[[[[[[[[ Pending code ]]]]]]]]]]]

//	@MessageMapping("/obtenerListaConversaciones")
//	public void obtenerListaConversaciones(Long id) {
//
//		List<Conversacion> conversaciones = conversacionRepo.findAllByUserID(id);
//
//		simp.convertAndSend("/user/" + id + "/queue/conversaciones", conversaciones);
//	}





    @MessageMapping("/sendMessage")
    public void sendMessage(StompHeaderAccessor headers, SendMessageDTO message) {

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


    //Variables e instances

    @Autowired
    private SimpMessagingTemplate simp;

}
