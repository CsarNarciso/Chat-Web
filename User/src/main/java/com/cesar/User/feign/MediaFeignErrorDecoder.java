package com.cesar.User.feign;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Component;
import com.cesar.User.exception.CustomFeignException;
import com.cesar.User.exception.CustomInternalServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class MediaFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = "Unknown error occurred";

        if (response.body() != null) {
            try (InputStream responseBody = response.body().asInputStream()) {

                final ObjectMapper objectMapper = new ObjectMapper();
                ApiException decodedException = objectMapper.readValue(responseBody, ApiException.class);
                message = (decodedException.getMessage() != null) ? decodedException.getMessage() : message;

            } catch (IOException ex) {
                return new Exception("Failed to decode error response message: " + ex.getMessage(), ex);
            }
        }

        return switch (response.status()) {
            case 400 -> new BadRequestException(message);
            case 500 -> new CustomInternalServerErrorException(message);
            default -> new CustomFeignException(message);
        };
    }
}