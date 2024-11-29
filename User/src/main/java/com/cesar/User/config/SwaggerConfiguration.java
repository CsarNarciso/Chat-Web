package com.cesar.User.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "USER SERVICE",
                description = "User details data handling and retrieve",
                contact = @Contact(
                        name = "CsarNarciso",
                        url = "https://github.com/CsarNarciso",
                        email = "cesarpazol1029@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "DEV",
                        url = "http://localhost:8001"
                )
        }
)
public class SwaggerConfiguration {}