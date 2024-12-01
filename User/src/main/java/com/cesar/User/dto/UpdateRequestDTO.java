package com.cesar.User.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDTO {
    private Optional<String> username = Optional.empty();
    private Optional<String> email = Optional.empty();
}