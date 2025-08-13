package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserInput {
    private String name;
    private String email;
    private Integer age;
}
