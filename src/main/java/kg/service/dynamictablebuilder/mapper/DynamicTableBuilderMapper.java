package kg.service.dynamictablebuilder.mapper;

import kg.service.dynamictablebuilder.dto.request.ColumnCreatedRequest;
import kg.service.dynamictablebuilder.dto.request.TableCreatedRequest;
import kg.service.dynamictablebuilder.dto.response.ColumnResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponseColumnSummaryResponse;
import kg.service.dynamictablebuilder.exception.exceptions.BadRequestException;
import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicTableBuilderMapper {

    private static final Map<String, String> columnTypeMapping = new HashMap<>();

    static {
        columnTypeMapping.put("TEXT", "TEXT");
        columnTypeMapping.put("INTEGER", "INTEGER");
        columnTypeMapping.put("BIGINT", "BIGINT");
        columnTypeMapping.put("DECIMAL", "NUMERIC(19, 4)");
        columnTypeMapping.put("BOOLEAN", "BOOLEAN");
        columnTypeMapping.put("DATE", "DATE");
        columnTypeMapping.put("TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE");
    }

    public static String getColumnType(String type) {
        String result = columnTypeMapping.get(type.toUpperCase());
        if (result == null) {
            throw new BadRequestException("Нет такого типа таблицы: " + type);
        }
        return result;
    }

    public DynamicTableDefinition dtoToEntityTable(TableCreatedRequest dto) {
        DynamicTableDefinition entity = new DynamicTableDefinition();
        entity.setTableName(dto.getTableName());
        entity.setUserFriendlyName(dto.getUserFriendlyName());

        return entity;
    }

    public List<DynamicColumnDefinition> dtoToEntityColumns(TableCreatedRequest dto) {
        List<DynamicColumnDefinition> columns = new ArrayList<>();
        List<ColumnCreatedRequest> dtoColumns = dto.getColumns();

        for (ColumnCreatedRequest column : dtoColumns) {
            DynamicColumnDefinition entityColumn = new DynamicColumnDefinition();
            entityColumn.setColumnName(column.getName());
            entityColumn.setColumnType(getColumnType(column.getType()));
            entityColumn.setNullable(column.isNullable());
        }

        return columns;
    }

    public TableResponse entityToDtoTable(DynamicTableDefinition entity) {
        TableResponse response = new TableResponse();
        response.setId(entity.getId());
        response.setTableName(entity.getTableName());
        response.setUserFriendlyName(entity.getUserFriendlyName());

        return response;
    }

    public List<ColumnResponse> entityToDtoColumns(DynamicTableDefinition entity) {
        List<ColumnResponse> columns = new ArrayList<>();
        List<DynamicColumnDefinition> columnsEntity = entity.getColumns();

        for (DynamicColumnDefinition column : columnsEntity) {
            ColumnResponse response = new ColumnResponse();
            response.setId(column.getId());
            response.setName(column.getColumnName());
            response.setType(column.getColumnType());
            response.setNullable(column.isNullable());
            response.setPrimaryKey(column.isPrimaryKeyInternal());

            columns.add(response);
        }

        return columns;
    }

    public List<TableResponseColumnSummaryResponse> entityToDtoColumnSummary(List<DynamicTableDefinition> entity) {
        List<TableResponseColumnSummaryResponse> list = new ArrayList<>();

        for (DynamicTableDefinition dynamicTableDefinition : entity) {
            TableResponseColumnSummaryResponse response = new TableResponseColumnSummaryResponse();
            response.setId(dynamicTableDefinition.getId());
            response.setTableName(dynamicTableDefinition.getTableName());
            response.setUserFriendlyName(dynamicTableDefinition.getUserFriendlyName());
            response.setColumnCount(dynamicTableDefinition.getColumns().size());

            list.add(response);
        }

        return list;
    }
}
