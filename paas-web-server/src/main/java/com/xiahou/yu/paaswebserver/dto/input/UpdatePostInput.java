package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostInput {
    private String title;
    private String content;
}
