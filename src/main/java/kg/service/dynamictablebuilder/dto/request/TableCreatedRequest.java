package kg.service.dynamictablebuilder.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TableCreatedRequest {
    private String tableName;
    private String userFriendlyName;
    private List<ColumnCreatedRequest> columns;
}
