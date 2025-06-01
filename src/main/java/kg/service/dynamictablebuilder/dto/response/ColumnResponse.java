package kg.service.dynamictablebuilder.dto.response;

import lombok.Data;

@Data
public class ColumnResponse {
    private Long id;
    private String name;
    private String type;
    private String postgresType;
    private boolean isNullable;
    private boolean isPrimaryKey;
}
