package kg.service.dynamictablebuilder.service;

import kg.service.dynamictablebuilder.dto.PaginationResponse;
import kg.service.dynamictablebuilder.dto.request.TableCreatedRequest;
import kg.service.dynamictablebuilder.dto.response.TableResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponseColumnSummaryResponse;

import java.util.List;
import java.util.Map;

public interface DynamicTableBuilderService {
    TableResponse createTable(TableCreatedRequest createDynamicTableRequest);
    TableResponse findByName(String tableName);
    List<TableResponseColumnSummaryResponse> getAll();
}
