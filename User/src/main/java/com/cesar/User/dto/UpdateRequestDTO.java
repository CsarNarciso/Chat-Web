package com.cesar.User.dto;

import java.util.Optional;
import lombok.Data;

@Data
public class UpdateRequestDTO {
    private Optional<String> username = Optional.empty();
    private Optional<String> email = Optional.empty();
}