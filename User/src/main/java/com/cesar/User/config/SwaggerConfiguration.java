package com.cesar.User.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "USER SERVICE",
                description = "User details data handling and retrieve",
                termsOfService = "https://some-condition-terms-for/CsarNarciso/Chat-Web.com",
                version = "1.0.0",
                contact = @Contact(
                        name = "CsarNarciso",
                        url = "https://github.com/CsarNarciso",
                        email = "cesarpazol1029@gmail.com"
                ),
                license = @License(
                        name = "Standard Software Use License for CsarNarciso - Chat-Web",
                        url = "www.csarnarciso.chat-web.com/licence"
                )
        ),
        servers = {
                @Server(
                        description = "DEV",
                        url = "http://localhost:8001"
                ),
                @Server(
                        description = "PROD",
                        url = "http://chat-web:8001"
                )
        }
)
public class SwaggerConfiguration {}