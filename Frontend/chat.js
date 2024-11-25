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
			
			//If successful connection
			userId = id;
			connectionStatusMessage.textContent = `Connected as User ${userId}`;
			connectionStatusMessage.style.color = 'green';	
			
			//Subscription to hear for first interaction messages (new conversations)
			stomp.subscribe(`/queue/onFirstInteraction/user/${userId}`, function (newConversation) {
				
			    var conversation = JSON.parse(newConversation.body);
			    var conversationId = conversation.id;
				var conversationRecipientId = conversation.recipient.userId;

			    // Display conversation 
			    displayData(conversation, `Conversation with ${conversationRecipientId}`);

				//And for each new one, subscribe to them to hear for messages
			    stomp.subscribe(`/topic/conversation/${conversationId}`, function (message) {
			        
					var sentMessage = JSON.parse(message.body);
					
					//Display new message
			        displayData(sentMessage , "New message");
			    });
			});

			// Function to format and display data
			function displayData(data, title) {
			    const container = $("#receivedData");
			    
			    // Create a card or structured HTML for better presentation
			    const card = $(`
			        <div class="data-card">
			            <h4>${title}</h4>
			            <pre>${JSON.stringify(data, null, 2)}</pre>
			        </div>
			    `);

			    container.append(card);
			}

			// Add some styles for better readability
			const style = `
			    <style>
			        .data-card {
			            border: 1px solid #ddd;
			            border-radius: 5px;
			            padding: 10px;
			            margin: 10px 0;
			            background: #f9f9f9;
			        }
			        .data-card h4 {
			            margin: 0 0 10px;
			            font-size: 16px;
			        }
			        .data-card pre {
			            background: #e8e8e8;
			            padding: 10px;
			            border-radius: 5px;
			            overflow-x: auto;
			        }
			    </style>
			`;
			$("head").append(style);

		}
	});
});