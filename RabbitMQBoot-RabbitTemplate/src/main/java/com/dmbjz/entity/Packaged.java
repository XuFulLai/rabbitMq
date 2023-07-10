package com.dmbjz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Packaged {

    private String ids;
    private String pname;
    private String pdesc;

}
