package kg.service.dynamictablebuilder.service.impl;

import kg.service.dynamictablebuilder.dto.PaginationResponse;
import kg.service.dynamictablebuilder.generator.SqlGenerator;
import kg.service.dynamictablebuilder.generator.TableCreator;
import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;
import kg.service.dynamictablebuilder.repository.DynamicColumnDefinitionRepository;
import kg.service.dynamictablebuilder.repository.DynamicTableDefinitionRepository;
import kg.service.dynamictablebuilder.service.DynamicTableBuilderDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ColumnMapRowMapper;
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
    private final TableCreator creator;

    @Override
    public Map<String, String> saveData(String tableName, Map<String, String> columns) {
        DynamicTableDefinition tableDefinition = tableDefinitionRepository.findByTableName(tableName);
        if (tableDefinition == null) {
            throw badRequest("Такая таблица не существует!");
        }

        List<DynamicColumnDefinition> columnDefinitions = columnDefinitionRepository.findDynamicColumnDefinition(tableDefinition);


        for (String key : columns.keySet()) {
            if (key.equalsIgnoreCase("id")) {
                throw badRequest("Поле id указывать нельзя при создании записи");
            }
            boolean exist = columnDefinitions.stream()
                    .anyMatch(columnDefinition -> columnDefinition.getColumnName().equals(key));
            if (!exist) {
                throw badRequest("Неизвестное поле: " + key);
            }
        }

        for (DynamicColumnDefinition columnDefinition : columnDefinitions) {
            if (!columnDefinition.isNullable() && !columnDefinition.getColumnName().equalsIgnoreCase("id")) {
                if (!columns.containsKey(columnDefinition.getColumnName())) {
                    throw badRequest("Обязательное поле " + columnDefinition.getColumnName() + " отсутствует");
                }
            }
        }

        String sql = SqlGenerator.generateInsertSql(tableName, columnDefinitions, columns);

        creator.query(sql);

        return columns;
    }

    @Override
    public PaginationResponse<Map<String, Object>> getTableData(String tableName, int page, int size) {
        DynamicTableDefinition table = tableDefinitionRepository.findByTableName(tableName);
        if (table == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Таблица не найдена: " + tableName);
        }

        SqlGenerator.SqlQueryPair sqlQueries = SqlGenerator.generateSelectAndCountSql(tableName, page, size);

        List<Map<String, Object>> content = jdbcTemplate.query(
                sqlQueries.selectSql(),
                new Object[]{size, sqlQueries.offset()},
                new ColumnMapRowMapper()
        );

        Long totalElements = jdbcTemplate.queryForObject(sqlQueries.countSql(), Long.class);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        PaginationResponse<Map<String, Object>> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPageable(new PaginationResponse.PageableDto(page, size));
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);

        return response;
    }

    @Override
    public Map<String, Object> getById(String tableName, Long id) {
        DynamicTableDefinition table = tableDefinitionRepository.findByTableName(tableName);
        if (table == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Таблица не найдена: " + tableName);
        }

        String sql = SqlGenerator.generateSelectByIdSql(tableName);

        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись с ID " + id + " не найдена");
        }
    }

    @Override
    public Map<String, Object> update(String tableName, Long id, Map<String, Object> payload) {
        DynamicTableDefinition table = tableDefinitionRepository.findByTableName(tableName);
        if (table == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Таблица не найдена: " + tableName);
        }

        List<DynamicColumnDefinition> columns = columnDefinitionRepository.findDynamicColumnDefinition(table);

        String checkSql = SqlGenerator.generateSelectByIdSql(tableName);
        try {
            jdbcTemplate.queryForMap(checkSql, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись с ID " + id + " не найдена");
        }

        for (DynamicColumnDefinition column : columns) {
            if (!column.isNullable() && !"id".equalsIgnoreCase(column.getColumnName())) {
                if (!payload.containsKey(column.getColumnName())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Обязательное поле не передано: " + column.getColumnName());
                }
            }
        }

        String sql = SqlGenerator.generateUpdateSql(tableName, columns);
        System.out.println("SQL: " + sql);

        List<Object> params = new ArrayList<>();
        for (DynamicColumnDefinition col : columns) {
            if ("id".equalsIgnoreCase(col.getColumnName())) continue;
            Object rawValue = payload.get(col.getColumnName());
            Object casted = castToColumnType(rawValue != null ? rawValue.toString() : null, col.getColumnType());
            params.add(casted);
        }
        params.add(id);

        creator.query(sql, params.toArray());

        return jdbcTemplate.queryForMap(checkSql, id);
    }

    @Override
    public void delete(String tableName, Long id) {
        DynamicTableDefinition table = tableDefinitionRepository.findByTableName(tableName);
        if (table == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Таблица не найдена: " + tableName);
        }

        String selectSql = SqlGenerator.generateSelectByIdSql(tableName);
        try {
            jdbcTemplate.queryForMap(selectSql, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись с ID " + id + " не найдена");
        }

        String deleteSql = SqlGenerator.generateDeleteSql(tableName, id);
        creator.query(deleteSql);
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
