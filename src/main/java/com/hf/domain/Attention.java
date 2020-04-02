package com.hf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attention {
    private Integer aid;
    private Integer befocused;
    private Integer follow;
    private Long attentionTime;
}
