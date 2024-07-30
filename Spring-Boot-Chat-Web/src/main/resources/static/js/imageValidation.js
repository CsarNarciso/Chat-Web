$(document).ready(function(){
	
	var imageField;
	var btn;
	var messageError;
	
	var imageName;
	
	if ( $("#imageMetadataField").length > 0 ){
		
		imageField = $("#imageMetadataField");
		btn = $("#registerBtn");
		messageError = $("#messageError_ImageMetadata");
	}
	else if ( $("#newImageField").length > 0 ){
		
		imageField = $("#newImageField");
		btn = $("#updateImageBtn");
		messageError = $("#messageError_UpdateImage");
		
		imageName = $("#UserData").attr("data-ImageName");
		
		if ( imageName === "NoProfileImage.png" ) {
		
			btn.prop("disabled", true);			
			messageError.hide();
		}
	}
	
	

	
	
	
	
	imageField.on("input", function(){
		
		var imageMetadata = $(this)[0];
		
		var extension = $(this).val().split(".").pop();
		
		
		btn.prop("disabled", false);			
		messageError.hide();
		
		
		if ( imageMetadata.files.length > 0 ){
			 
			if ( extension === "png" || extension === "jpg" || extension === "jpeg" ){
			
				if ( imageMetadata.files[0].size > (1*1024*1024) ){
					
					btn.prop("disabled", true);
					
					messageError.css({ display:"block" });
					
					messageError.text("The image is too large, max size is 1024*1024");
				}
			}
			else{
				
				btn.prop("disabled", true);
					
				messageError.css({ display:"block" });
				
				messageError.text("Allowed only jpg or png image files.");
			}	
		}
		else{
			
			if ( imageName === "NoProfileImage.png" ) {
		
				btn.prop("disabled", true);			
			}
		}

		
		

	});
});
