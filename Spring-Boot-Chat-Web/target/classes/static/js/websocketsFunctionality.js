$(document).ready(function() {


	////////////////VARIABLES////////////////////
	/////////////////////////////////////////////
	var socket = new SockJS("/chat/index");
	var stomp = Stomp.over(socket);
	
	var name = $("#userData").attr("data-Name");
	var id = $("#userData").attr("data-Id");
	var imageName = $("#userData").attr("data-ImageName");
	
	var actualRecipientId;
	var actualRecipientName;
	var actualRecipientImageName;

	var profileImagesPath = "/images/profile/";
	var generalImagesPath = "/images/general/";
		
	var suscriptionDestination_GetOnlineUsers = "/user/" + id + "/queue/getOnlineUsers";
	var suscriptionDestination_UpdateOnlineUsers = "/topic/updateOnlineUsers";
	var suscriptionDestination_GetConversations = "/user/" + id + "/queue/conversations";
	var suscriptionDestination_GetMessage = "/user/" + id + "/queue/getMessage";
	var suscriptionDestination_UpdateConversationData = "/topic/updateConversationData/";
	var suscriptionDestination_GetMessages = "/user/" + id + "/queue/mensajes";
	
	
	var sendingDestination_UpdateOnlineUsers = "/updateOnlineUsers";
	var sendingDestination_Message = "/sendMessage";
	var sendingDestination_UpdateNewMessages = "/updateNewMessages";
	var sendingDestination_GetMessages = "/getMessages";
	var sendingDestination_CreateConversation = "/createConversation";
	var sendingDestination_GetConversations = "/getConversations";
	var sendingDestination_DeleteConversation = "/deleteConversation";
	



	//////////////////////SHOW USER NAME AND IMAGE ON INTERFACE////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	$("#userName").text(name);
	$("#userImage").attr("src", profileImagesPath + imageName);




		stomp.connect({}, function() {
			
			
			
			/////////GET ONLINE USERS SUSCRIPTION//////////
			/////////////////////////////////////////////////////////////////
			stomp.subscribe(suscriptionDestination_GetOnlineUsers, function(message){
				
				var onlineUsers = JSON.parse(message.body);
	
				for (const [key, user] of Object.entries(onlineUsers)) {
		
					addUser(user.id, user.name, user.imageName);		
	    		}
			});
			
			
			
			/////////UPDATE ONLINE USERS SUSCRIPTION//////////
			/////////////////////////////////////////////////////////////////
			stomp.subscribe(suscriptionDestination_UpdateOnlineUsers, function(message){
	
				var user = JSON.parse(message.body);
				
				if ( user.status === "CONNECTED" ) {
						
					addUser(user.id, user.name, user.imageName);	
				}
				else if ( user.estado === "DISCONNECTED" ) {
						
					$("#user_" + user.id).remove();
				}
				else if ( user.status === "UPDATE_NAME" ) {
						
					$("#user_" + user.id + " #nameElement").text(user.name);
					$("#user_" + user.id).attr("data-Name", user.name);
				}
				else if ( user.status === "UPDATE_IMAGE" ) {
					
					$("#user_" + user.id + " #imageElement").attr("src", profileImagesPath + user.imageName);
					$("#user_" + user.id).attr("data-ImageName", user.imageName);
				}
			});
			
			
	//		
	//		/////////SUSCRIPCION MOSTRAR LISTA DE CONVERSACIONES//////////
	//		/////////////////////////////////////////////////////////////////
	//		stomp.subscribe(destinoSuscripcion_ListaConversaciones, function(message){
	//			
	//			var listaConversaciones = JSON.parse(message.body);
	//			
	//			$("#listaConversaciones").empty();
	//			
	//			listaConversaciones.forEach(function(c){
	//				
	//				agregarConversacion(c.id_remitente, c.id_destinatario, c.nombre, c.nombreImagen, c.mensajesNuevos)
	//
	//			});
	//			
	//		});
	//		
	//		
	//		/////////SUSCRIPCION MOSTRAR LISTA DE MENSAJES//////////
	//		/////////////////////////////////////////////////////////////////
	//		stomp.subscribe(destinoSuscripcion_ListaMensajes, function(message){
	//			
	//			var listaMensajes = JSON.parse(message.body);
	//
	//			$("#bandejaConversacion").empty();
	//			
	//			listaMensajes.forEach(function(m){
	//				
	//				agregarMensajeBandejaConversacion(m);
	//			})
	//			
	//		});
			
	
			/////////MESSAGES PROCESSING SUSCRIPTION//////////
			/////////////////////////////////////////////////////////////////
			stomp.subscribe(suscriptionDestination_GetMessage, function(receivedMessage){
				
				var receivedMessage = JSON.parse(receivedMessage.body);
				
				var senderData = receivedMessage.headers;

				
				var senderId = receivedMessage.senderId;
				
				var senderName = senderData.name;
				var senderImageName = senderData.imageName;
				
				addMessageToConversationTray(receivedMessage, senderData);
//				if ( $("#conversacion_" + idRemitente).length == 0) {
//					
//					agregarConversacionAndGuardarBBDD(id, idRemitente, nombreRemitente, nombreImagenRemitente, 1);
//				}
//				else{
//					
//					if ( verificarSiConversacionEstaAbierta(idRemitente) ){
//	
//						agregarMensajeBandejaConversacion(mensaje.contenido);
//					}
//					else{
//						
//						actualizarMensajesNuevos(idRemitente, "+");
//					}
//				}
				
				
			});
			
			
			
			
			
			
			//////////CONNECT USER SENDING///////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////
			
			var status = "CONNECTED";
			
			if ( $("#userData").attr("data-Update") === "Image" ){
				
				status = "UPDATE_IMAGE";
			}
			
			
			stomp.send(sendingDestination_UpdateOnlineUsers, {}, JSON.stringify({ 
				"id" : id, 
				"status" : status,
				"name" : name,
				"imageName" : imageName
			}));
			
	//		
	//		//////////ENVIO OBTENER LISTA CONVERSACIONES///////////////
	//		////////////////////////////////////////////////////////////////////////////////////////////////////
	//		stomp.send(destinoEnvio_ObtenerListaConversaciones, {}, id);
	//	
	//		
	
			
		});
	
	
	
	

	
	
	
	//////////////MESSAGE SENDING///////////////////
	////////////////////////////////////////////////
	$("#sendMessageBtn").click(function(e) {
		
//		if( $("#menuEnviar").attr("data-CrearConversacion") === "Si" ){
//			
//			agregarConversacionAndGuardarBBDD(id, idDestinatarioActual, nombreDestinatarioActual, nombreImagenDestinatarioActual, 0);
//		}
		
		

		var senderData = {
			"name" : name,
			"imageName" : imageName
		}
		
		var message = {
			"senderId" : id,
			"recipientId" : actualRecipientId,
			"content" : $("#messageField").val(),
			"date" : new Date().toISOString()
		}
		
		
		addMessageToConversationTray(message, senderData);

		stomp.send(sendingDestination_Message, senderData, JSON.stringify(message));
		
		$("#messageField").val("");
	});
	
	
	
	
	
	
	/////////////DICONNECTION EVENT CAPTURE/////////////
 	$(window).on('beforeunload', function() {
		 
		//DISCONNECT USER SENDING
	    stomp.send(sendingDestination_UpdateOnlineUsers, {}, JSON.stringify({
			"id" : id,
			"status" : "DISCONNECT"
	    })); 
	});  
		 
		 

	
	
	
	////////////////////////////////////FUNCTIONS////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	function addUser(newUserId, newUserName, newUserImageName) {
		
		if( newUserId != id ) {
					
			$("#usersList").append(
				
				"<li id='user_" + newUserId + "'>" + 
					"<span id='nameElement'>" + newUserName + "</span>" +
					"<img id='imageElement' src='" + (profileImagesPath + newUserImageName) + "' height='50px' width='50px'></img>" +
				"</li>"
				
			);
			
			$("#user_" + newUserId).attr("data-Id", newUserId);
			$("#user_" + newUserId).attr("data-Name", newUserName);
			$("#user_" + newUserId).attr("data-ImageName", newUserImageName);
			

			$("#user_" + newUserId).click( function() {
				openConversation(newUserId, $(this))
			});
		}
			
			
//			
//			nombreImagen = nombreImagenP;
//			$("#imagenUsuario").attr("src", nombreImagenP);
//			$("#datosUsuario").attr("data-NombreImagen", nombreImagenP);
//			
//			if ( verificarSiConversacionEaAbierta(idP) ){
//			
//				$("#bandejaConversacioeach(function(){
//					
//					if ( $(this).attr("data-Remitente") === id ){
//						
//						if ( $(this).find("#imagen") > 0 ){
//							
//							$(this).find("#imagen").attr("src", rutaImagenesPerfil + nombreImagenP);
//						}
//					}
//				});
//			}
	}

	
//	
//	function agregarConversacion(idRemitente, idDestinatario, nombre, nombreImagen, mensajesNuevos){
//					
//		$("#listaConversaciones").append(
//		
//			"<li id='conversacion_" + idDestinatario + "'>" + 
//				"<img id='elemento_eliminar' src='" + (rutaImagenesGenerales + "EliminarConversacion.png") + "'></img>" +
//				"<img id='elemento_imagen' src='" + (rutaImagenesPerfil + nombreImagen) + "'></img>" +
//				"<span id='elemento_nombre'>" + nombre + "</span>" +
//				"<span id='elemento_mensajesNuevos'>" + mensajesNuevos + "</span>" +
//			"</li>"
//		
//		);	
//	
//	
//		$("#conversacion_" + idDestinatario).attr("data-IdRemitente", idRemitente);
//		$("#conversacion_" + idDestinatario).attr("data-IdDestinatario", idDestinatario);
//		$("#conversacion_" + idDestinatario).attr("data-Nombre", nombre);
//		$("#conversacion_" + idDestinatario).attr("data-NombreImagen", nombreImagen);
//		$("#conversacion_" + idDestinatario).attr("data-MensajesNuevos", mensajesNuevos);
//		
//		
//		
//		
//		$("#conversacion_" + idDestinatario).click(abrirConversacion(idDestinatario, $(this) ));
//		
//
//
//		var suscripcionActualizarDatosConversacion = stomp.subscribe(destinoSuscripcion_ActualizarDatosConversacion + idDestinatario, function(message){
//			
//			
//			var datosUsuarioActualizado = JSON.parse(message.body);
//
//
//			if ( datosUsuarioActualizado.actualizar === "nombre" ){
//				
//				var nuevoNombre = datosUsuarioActualizado.nombre;
//				
//				$("#conversacion_" + idDestinatario).attr("data-Nombre", nuevoNombre);
//				$("#conversacion_" + idDestinatario +" #elemento_nombre").text(nuevoNombre);
//				
//			}
//			else{
//				
//				var nuevoNombreImagen = datosUsuarioActualizado.nombreImagen;
//				
//				$("#conversacion_" + idDestinatario).attr("data-NombreImagen", nuevoNombreImagen);
//				$("#conversacion_" + idDestinatario +" #elemento_imagen").attr("src", rutaImagenesPerfil + nuevoNombreImagen);
//				
//			}
//			
//			
//			
//			if ( verificarSiConversacionEstaAbierta(idDestinatario) ){
//					
//				if ( datosUsuarioActualizado.actualizar === "nombre" ){
//					
//					var nuevoNombre = datosUsuarioActualizado.nombre;
//					
//					$("#nombreConversacion").text(nuevoNombre);
//					nombreDestinatarioActual = nuevoNombre;	
//				}
//				else{
//					
//					var nuevoNombreImagen = datosUsuarioActualizado.nombreImagen;
//
//					nombreImagenDestinatarioActual = nuevoNombreImagen;	
//					
//					$("#bandejaConversacion").each(function(){
//						
//						if ( $(this).attr("data-Remitente") === idDestinatario ){
//							
//							if ( $(this).find("#imagen") > 0 ){
//								
//								$(this).find("#imagen").attr("src", rutaImagenesPerfil + nuevoNombreImagen);
//							}
//						}
//					});
//				}
//			}
//			
//		});
//		
//		$("#conversacion_" + idDestinatario + " #elemento_eliminar").click( eliminarConversacion(idDestinatario, suscripcionActualizarDatosConversacion) );			
//	}
//	
//	
//	function agregarConversacionAndGuardarBBDD(idRemitente, idDestinatario, nombre, nombreImagen, mensajesNuevos){
//		
//		agregarConversacion(idRemitente, idDestinatario, nombre, nombreImagen, mensajesNuevos);
//		
//		var conversacion = {
//			"id_remitente" : idRemitente,
//			"id_destinatario" : idDestinatario, 
//			"nombre" : nombre, 
//			"nombreImagen" : nombreImagen,
//			"mensajesNuevos" : mensajesNuevos
//		}
//			
//		stomp.send(destinoCrearConversacion, JSON.stringify(conversacion));
//	}
	
	
	
	function openConversation(id, element){

		$("#messageField").val("");

		if ( isConversationOpen(id) ){
			
			$("#conversationName").hide();
			$("#sendMenu").hide();
		}
		else{
		
			$("#conversationName").css({
				display:"block"
			});
			$("#sendMenu").css({
				display:"block"
			});
			
			$("#conversationName").text( element.attr("data-Name") );
			
			actualRecipientId = element.attr("data-Id");
//			nombreDestinatarioActual = elemento.attr("data-Nombre");
//			nombreImagenDestinatarioActual = elemento.attr("data-NombreImagen");
			
//			if ( $("#conversacion_" + id).length > 0 ) {
//				
//				$("#formEnviar").attr("data-CrearConversacion", "No");
//				
//				if ( $("#conversacion_" + id).attr("data-MensajesNuevos") > 0 ){
//			
//					actualizarMensajesNuevos(id, "0");
//				}
//			}
//			else{
//				
//				$("#formEnviar").attr("data-CrearConversacion", "Si");
//			}
			
			
//			stomp.send(destinoEnvio_ObtenerListaMensajes, {}, {"idRemitente" : id, "idDestinatario" : idDestinatarioActual});

		}		
	}
	
	
	
//	function eliminarConversacion(id, suscripcionActualizarDatosConversacion){
//		
//		if ( verificarSiConversacionEstaAbierta(id) ){
//			
//			$("formEnviar").hide();
//			$("#nombreConversacion").hide();
//			$("#bandejaConversacion").empty();
//		}
//
//		$("#conversacion_" + id).remove();
//		
//		stomp.send(destinoEnvio_EliminarConversacion, {}, {"id" : id});
//		
//		stomp.unsubscribe(suscripcionActualizarDatosConversacion.id);
//	}
//	
	
	
	function isConversationOpen(recipientId){
		
		if ( $("#sendMenu").is(":visible") ){
			
			if ( actualRecipientId == recipientId ){
				
				return true;
			}
		}
		else{
			return false;
		}
	}
	
	
	
//	function actualizarMensajesNuevos(idConversacion, accion){
//		
//		var mensajesNuevos;
//		
//		
//		if ( accion === "+" ){
//			
//			mensajesNuevos = $("#conversacion_" + idConversacion).attr("data-MensajesNuevos") + 1;
//		}
//		
//		else if ( accion === "0" ) {
//			
//			mensajesNuevos = 0;
//		}
//		
//				
//		var datosActualizacion = {
//			"idRemitente" : id,
//			"idDestinatario" : idConversacion,
//			"mensajesNuevos" : mensajesNuevos
//		}
//		
//		
//		$("#conversacion_" + idConversacion).attr("data-MensajesNuevos", mensajesNuevos);
//		$("#conversacion_" + idConversacion + " #elemento_mensajesNuevos").text(mensajesNuevos);
//		
//		
//		stomp.send(destinoEnvio_ActualizarMensajesNuevos, {}, datosActualizacion);
//	}
	
	
	
	function addMessageToConversationTray(message, senderData){
		
		var senderId = message.senderId;
		var recipientId = message.recipientId;
		var content = message.content;
		
		var senderName = senderData.name;
		var senderImageName = senderData.imageName;
		
		
		//FUNCIONALIDAD DE FECHA DE ENVIO
//		var fechaEnvio = new Date(mensaje.fecha);
//		
//		console.log(fechaEnvio.getTime());
//		
//		var fechaActual = new Date();
//		
//		console.log(fechaActual.getTime());
//		
//		var milisegundosDiferencia = (fechaActual.getTime() - fechaEnvio.getTime());
//		
//		console.log( milisegundosDiferencia );
//		
//		var segundos = Math.floor( milisegundosDiferencia / 1000 );
//		console.log("SEGUNDOS: " + segundos);
//		var minutos = Math.floor( segundos / 60 );
//		var horas = Math.floor( minutos / 60 );
//		var dias = Math.floor( horas / 24 );
//		
//		
//		var enviadoHace = "un instante";
//		
//
//		if ( minutos > 0 ){
//			
//			enviadoHace = minutos + " minutos";
//		}
//		if ( horas > 0 ){
//			
//			enviadoHace = horas + " horas";
//		}
//		if ( dias > 0 ){
//			
//			enviadoHace = dias + " dias";
//		}
		
		
		
		
//		if ( $("#bandejaConversacion li").length > 0 ){
//			
//			
//			
//			if ( idRemitente === $("#bandejaConversacion li").last().attr("data-Remitente") ){
//				
//				if ( $("#bandejaConversacion li").last().attr("data-DiferenciaMinutos") - minutos <= 2 ){
//					
//					if ( id === idRemitente ){
//						
//						$("#bandejaConversacion li").last().find("#imagen").remove();	
//					}
//					
//					$("#bandejaConversacion li").last().find("#fecha").remove();	
//				}	
//				else{
//					
//					$("#bandejaConversacion li").last().append("<br>");	
//				}
//			}
//			else{
//				
//				$("#bandejaConversacion li").last().append("<br>");	
//			}
//		}
//		
//		


//		"<li data-Remitente='" + idRemitente + "' data-DiferenciaMinutos='" + minutos + "'>" +
//				elementoImagen +
//				"<span id='contenido'>" + contenido + "</span> <br>" + 
//				"<span id='fecha'> Enviado hace " + enviadoHace + "</span>" 
//				+ 
//			"</li>"

		var imageElement = "";
		
		if ( id != senderId ){
			
			imageElement = "<img id='image' src='" + profileImagesPath + senderImageName + "' height='35px' width='35px'> <br>";
		}
		
		$("#messagesTray").append(
			
			"<li data-Sender='" + senderId + "'>" +
				imageElement +
				"<span id='content'>" + content + "</span> <br>" + 
			"</li>"
			
		);
	}
	
	
	
	
	//////////////////ELEMENTS FUNCTIONS INITIALIZATION AND ASIGNATION////////////////////
	/////////////////////////////////////////////////////////////////////////
	

		////////show profile options menu
	$("#userImage").click(function(e){
		
		$("#profileOptionsMenu").toggle();
		
		if ( $("#profileOptionsMenu").is(":visible") ){
		
			$("#messageError_UpdateName").hide();	
		}
		
		$("#updateProfileMenu").hide();
	});
	
	
	
		////////show update proile menu
	$("#updateProfileBtn").click(function(e){
		
		$("#profileOptionsMenu").hide();
		
		$("#newNameField").val(name);
		$("#updateNameBtn").prop("disabled", true);
		
		$("#newImageField").val("");
		
		$("#updateProfileMenu").css({
			display:"block"
		});
		
	});
	
	
	$("#closeUpdateProfileMenuBtn").click(function(e){
	
		$("#updateProfileMenu").hide();	
		
		$("#messageError_UpdateName").hide();
	});
	
	
	
		/////////Verify newName field value to enable or not updateNameBtn 
	$("#newNameField").on("input", function(){
		
		var newName = $(this).val(); 
		
		if( newName === name || newName.trim() === ""){

			$("#updateNameBtn").prop("disabled", true);
		}
		else{
			
			$("#updateNameBtn").prop("disabled", false);
		}
		
	});
	
	
	
	/////////Verify validation result while username updates
	if( $("#messageError_UpdateName").attr("data-ValidationResult") === "Incorrect"){
		
		$("#messageError_UpdateName").text("Name not available.");
		
		$("#newNameField").val( $("#messageError_UpdateName").attr("data-NameNotAvailable") );
		
		$("#updateProfileMenu").css({
			display:"block"
		});
	}
	
	

		//////////verify messageField vlaue to enable or not sendMessageBtn
		
		$("#sendMessageBtn").prop("disabled", true);
		
		$("#messageField").on("input", function(){
			
			if ( $(this).val().trim() === "" ){
				
				$("#sendMessageField").prop("disabled", true);
			}
			else{
				
				$("#sendMessageBtn").prop("disabled", false);
			}
			
		});
	
	
	

});