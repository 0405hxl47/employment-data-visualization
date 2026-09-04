package com.employment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@TableName("employment")
public class Employment {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String universityName;

    private Integer graduationYear;

    private String majorName;

    private String degree;

    private BigDecimal employmentRate;

    private String employmentType;

    private String industry;

    private String province;

    private String city;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private BigDecimal salaryAvg;

    private BigDecimal salaryTrue;

    private BigDecimal achievementRate;

    private String dataSource;
}
