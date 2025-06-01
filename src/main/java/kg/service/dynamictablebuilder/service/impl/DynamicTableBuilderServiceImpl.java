package kg.service.dynamictablebuilder.service.impl;

import kg.service.dynamictablebuilder.dto.request.TableCreatedRequest;
import kg.service.dynamictablebuilder.dto.response.ColumnResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponseColumnSummaryResponse;
import kg.service.dynamictablebuilder.generator.SqlGenerator;
import kg.service.dynamictablebuilder.mapper.DynamicTableBuilderMapper;
import kg.service.dynamictablebuilder.model.DynamicColumnDefinition;
import kg.service.dynamictablebuilder.model.DynamicTableDefinition;
import kg.service.dynamictablebuilder.repository.DynamicTableDefinitionRepository;
import kg.service.dynamictablebuilder.service.DynamicTableBuilderService;
import kg.service.dynamictablebuilder.validator.TableRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicTableBuilderServiceImpl implements DynamicTableBuilderService {

    private final DynamicTableDefinitionRepository repository;
    private final JdbcTemplate jdbcTemplate;

    private DynamicTableBuilderMapper mapper;
    private TableRequestValidator validator;

    @Override
    public TableResponse createTable(TableCreatedRequest createDynamicTableRequest) {
        validator.validate(createDynamicTableRequest);

        DynamicTableDefinition table = mapper.dtoToEntityTable(createDynamicTableRequest);
        List<DynamicColumnDefinition> columns = mapper.dtoToEntityColumns(createDynamicTableRequest);

        table.setColumns(columns);

        String query = SqlGenerator.generateCreateTableSql(table);
        jdbcTemplate.update(query);

        repository.save(table);

        TableResponse tableCreatedResponse = mapper.entityToDtoTable(table);
        List<ColumnResponse> columnResponse = mapper.entityToDtoColumns(table);

        tableCreatedResponse.setColumns(columnResponse);

        return tableCreatedResponse;
    }

    @Override
    public TableResponse findByName(String tableName) {
        DynamicTableDefinition table = repository.findByTableName(tableName);

        if (table == null) {
            throw badRequest("Таблица с таким именем не существует!");
        }

        TableResponse tableCreatedResponse = mapper.entityToDtoTable(table);
        List<ColumnResponse> columnResponse = mapper.entityToDtoColumns(table);

        tableCreatedResponse.setColumns(columnResponse);

        return tableCreatedResponse;
    }

    @Override
    public List<TableResponseColumnSummaryResponse> getAll() {
        List<DynamicTableDefinition> tables = repository.findAll();

        return mapper.entityToDtoColumnSummary(tables);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}