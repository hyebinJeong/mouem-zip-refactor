package org.scoula.finalreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "DTO for creating a final report")
public class FinalReportInsertDTO {

    @ApiModelProperty(value = "User ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "Registry", example = "1")
    private Long registryId;

    @ApiModelProperty(value = "Report ID", example = "1")
    private Long reportId;
}
