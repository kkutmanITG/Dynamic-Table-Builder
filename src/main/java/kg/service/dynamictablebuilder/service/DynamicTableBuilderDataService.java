package kg.service.dynamictablebuilder.service;

import kg.service.dynamictablebuilder.dto.PaginationResponse;

import java.util.Map;

public interface DynamicTableBuilderDataService {
    Map<String, String> saveData(String tableName, Map<String, String> columns);
    PaginationResponse<Map<String, Object>> getTableData(String tableName, int page, int size);
    Map<String, Object> getById(String tableName, Long id);
    Map<String, Object> update(String tableName, Long id, Map<String, Object> payload);
    void delete(String tableName, Long id);
}
