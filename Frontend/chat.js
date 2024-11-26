$(document).ready(function() {
	
	var socket = new SockJS("http://localhost:8002/chat");
	var stomp = Stomp.over(socket);
	stomp.connect({}, function() {
		
		connectionStatusMessage.textContent = `Successful WS Connection`;
		connectionStatusMessage.style.color = 'green';
		$("#userIdInputForm").css({
			display: 'block'
		});
		
		let userId = null;

		document.getElementById("connectBtn").addEventListener("click", function(){

			const idInput = document.getElementById("userId");
			let id = idInput.value.trim();

			if(id){
				
				//If successful user connection
				userId = id;
				connectionStatusMessage.textContent = `User ${userId}`;
				$("#userIdInputForm").hide();
				
				//Subscription to hear for first interaction messages (new conversations)
				stomp.subscribe(`/queue/onFirstInteraction/user/${userId}`, function (newConversation) {
					
					var conversation = JSON.parse(newConversation.body);
					var conversationId = conversation.id;

					// Display conversation 
					displayData(conversation);

					//And for each new one, subscribe to them to hear for messages
					stomp.subscribe(`/topic/conversation/${conversationId}`, function (message) {
						
						//Display new message
						displayData(JSON.parse(message.body));
					});
				});

				//Format and display data
				function displayData(data) {
					const jsonContainer = document.getElementById('json-container');
					jsonContainer.innerHTML = prettyPrintJson.toHtml(data);
				}
			}
		});
		
		
	}, function(error){
		
		connectionStatusMessage.textContent = error;
		connectionStatusMessage.style.color = 'red';
		$("#userIdInputForm").hide();
		$("#json-container").hide();
	});
});