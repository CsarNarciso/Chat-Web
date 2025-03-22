package com.cesar.User.feign;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.cesar.User.exception.CustomBadRequestException;
import org.apache.kafka.common.errors.ApiException;
import com.cesar.User.exception.CustomInternalServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

public class MediaFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = "Unknown error occurred";

        if (response.body() != null) {

            try (InputStream responseBody = response.body().asInputStream()) {

                //Get response message text
                String responseText = new String(responseBody.readAllBytes(), StandardCharsets.UTF_8);

                //If response is JSON body object
                if(responseText.trim().endsWith("{")){

                    final ObjectMapper objectMapper = new ObjectMapper();

                    //Map message (json object body) as exception message
                    ApiException decodedException = objectMapper.readValue(responseBody, ApiException.class);

                    if(decodedException.getMessage() != null) {
                        message = decodedException.getMessage();
                    }
                }

                //If plain text response,
                message = responseText;

            } catch (IOException ex) {
                return new Exception("Failed to decode error response message: " + ex.getMessage(), ex);
            }
        }

        return switch(response.status()){
            case 400 -> new CustomBadRequestException(message);
            case 500 -> new CustomInternalServerErrorException(message);
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}