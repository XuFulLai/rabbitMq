package com.dmbjz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/* 教授实体类 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Professor implements Serializable {

    private String id;      //主键
    private String name;    //名称
    private Integer phone;   //联系电话
    private String type;    //教学行业类别

}
