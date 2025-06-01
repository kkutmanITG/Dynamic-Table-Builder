package kg.service.dynamictablebuilder.service;

import java.util.Map;

public interface DynamicTableBuilderDataService {
    Map<String, String> saveData(String tableName, Map<String, String> columns);
}
