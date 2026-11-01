package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特殊煎服法操作规范提示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecoctionNote {
    /** 药材名称 */
    private String herb;

    /** 煎服法类型：先煎、后下、包煎、烊化 */
    private String method;

    /** 具体操作规范说明 */
    private String instructions;
}
