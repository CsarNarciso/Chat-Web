$(document).ready(function() {

	var socket = new SockJS("http://localhost:8002/chat");
	var stomp = Stomp.over(socket);
	stomp.connect({}, function() {});

	let userId = null;

	document.getElementById("connectBtn").addEventListener("click", function(){

		const idInput = document.getElementById("userId");
		const connectionStatusMessage = document.getElementById("connectionStatusMessage");

		let id = idInput.value.trim();

		if(id){
			
			userId = id;
			connectionStatusMessage.textContent = `Connected as User ${userId}`;
			connectionStatusMessage.style.color = 'green';	
			
			stomp.subscribe(`/queue/onFirstInteraction/user/${userId}`, function(newConversation){
				
				var conversation = JSON.parse(newConversation.body);
				var conversationId = conversation.id;
				console.log(conversation);
				
				stomp.subscribe(`/topic/conversation/${conversationId}`, function(message){
					console.log(JSON.parse(message.body));
				});
			});
		}
	});
});