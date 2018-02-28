package com.revolut.test.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
}
