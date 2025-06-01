package kg.service.dynamictablebuilder.dto.response;

import lombok.Data;

@Data
public class TableResponseColumnSummaryResponse {
    private Long id;
    private String tableName;
    private String userFriendlyName;
    private int columnCount;
}
