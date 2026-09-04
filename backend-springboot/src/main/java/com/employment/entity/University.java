package com.employment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("universities")
public class University {

    @TableId(value = "university_name", type = IdType.INPUT)
    private String universityName;

    private String universityType;

    private String province;

    private String city;
}
