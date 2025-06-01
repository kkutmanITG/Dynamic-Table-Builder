package kg.service.dynamictablebuilder.dto.request;

import lombok.Data;

@Data
public class ColumnCreatedRequest {
    private String name;
    private String type;
    private boolean isNullable;
}
