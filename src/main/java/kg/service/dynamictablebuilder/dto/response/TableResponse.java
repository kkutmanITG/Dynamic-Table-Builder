package kg.service.dynamictablebuilder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TableResponse {
    private Long id;
    private String tableName;
    private String userFriendlyName;
    private List<ColumnResponse> columns;
}
