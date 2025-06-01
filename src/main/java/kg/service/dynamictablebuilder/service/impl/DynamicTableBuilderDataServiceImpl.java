package kg.service.dynamictablebuilder.service.impl;

import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;
import kg.service.dynamictablebuilder.repository.DynamicColumnDefinitionRepository;
import kg.service.dynamictablebuilder.repository.DynamicTableDefinitionRepository;
import kg.service.dynamictablebuilder.service.DynamicTableBuilderDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DynamicTableBuilderDataServiceImpl implements DynamicTableBuilderDataService {

    private final DynamicColumnDefinitionRepository columnDefinitionRepository;
    private final DynamicTableDefinitionRepository tableDefinitionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, String> saveData(String tableName, Map<String, String> columns) {
        DynamicTableDefinition tableDefinition = tableDefinitionRepository.findByTableName(tableName);
        if (tableDefinition == null) {
            throw badRequest("Такая таблица не существует!");
        }

        List<DynamicColumnDefinition> columnDefinitions = columnDefinitionRepository.findDynamicColumnDefinition(tableDefinition.getId());

        for (String key : columns.keySet()) {
            if (key.equalsIgnoreCase("id")) {
                throw badRequest("Поле 'id' указывать нельзя при создании записи");
            }
            boolean exist = columnDefinitions.stream().anyMatch(columnDefinition -> columnDefinition.getColumnName().equals(key));

            if (!exist) {
                throw badRequest("Неизвестное поле: " + key);
            }
        }

        for (DynamicColumnDefinition columnDefinition : columnDefinitions) {
            if (!columnDefinition.isNullable() && !columnDefinition.getColumnName().equalsIgnoreCase("id")) {
                if (!columns.containsKey(columnDefinition.getColumnName())) {
                    throw badRequest("Обязательное поле '" + columnDefinition.getColumnName() + "' отсутствует");
                }
            }
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder();
        List<Object> params = new ArrayList<>();

        int i = 0;
        for (DynamicColumnDefinition columnDefinition : columnDefinitions) {
            String colName = columnDefinition.getColumnName();
            if (colName.equalsIgnoreCase("id")) continue;

            if (columns.containsKey(colName)) {
                if (i > 0) {
                    sql.append(", ");
                    placeholders.append(", ");
                }

                sql.append(colName);
                placeholders.append("?");
                params.add(castToColumnType(columns.get(colName), columnDefinition.getColumnType()));
                i++;
            }
        }

        sql.append(") VALUES (").append(placeholders).append(")");

        jdbcTemplate.update(sql.toString(), params.toArray());

        return columns;
    }

    private Object castToColumnType(String value, String type) {
        if (value == null) return null;
        return switch (type.toUpperCase()) {
            case "INTEGER" -> Integer.valueOf(value);
            case "DECIMAL" -> new java.math.BigDecimal(value);
            case "BOOLEAN" -> Boolean.valueOf(value);
            case "DATE" -> java.sql.Date.valueOf(value);
            case "TEXT" -> value;
            default -> throw badRequest("Такого типа не существует: " + type);
        };
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
