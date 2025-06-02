package kg.service.dynamictablebuilder.validator;

import kg.service.dynamictablebuilder.dto.request.ColumnCreatedRequest;
import kg.service.dynamictablebuilder.dto.request.TableCreatedRequest;
import kg.service.dynamictablebuilder.exception.exceptions.BadRequestException;
import kg.service.dynamictablebuilder.repository.DynamicTableDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TableRequestValidator {
    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "TEXT", "INTEGER", "BIGINT", "DECIMAL", "BOOLEAN", "DATE", "TIMESTAMP"
    );

    private final DynamicTableDefinitionRepository repository;

    private boolean isValidName(String name) {
        return name.matches("^[a-z0-9_]{3,63}$");
    }

    public void validate(TableCreatedRequest request) {
        if (request.getTableName() == null || request.getTableName().isEmpty()) {
            throw badRequest("Имя таблицы обязательно.");
        }

        String tableName = request.getTableName();

        if (!isValidName(tableName)) {
            throw badRequest("Имя таблицы должно содержать только строчные латинские буквы, цифры и подчёркивания, длина от 3 до 63 символов.");
        }

        if (tableName.startsWith("pg_") || tableName.startsWith("app_")) {
            throw badRequest("Имя таблицы не должно начинаться с зарезервированного префикса: pg_ или app_");
        }

        if (repository.existsByTableName(tableName)) {
            throw badRequest("Таблица с таким именем уже есть!");
        }

        List<ColumnCreatedRequest> columns = request.getColumns();
        if (columns == null || columns.isEmpty()) {
            throw badRequest("Необходимо указать хотя бы одну колонку.");
        }

        Set<String> uniqueColumnNames = new HashSet<>();
        for (ColumnCreatedRequest column : columns) {
            String colName = column.getName();
            String colType = column.getType();

            if (colName == null || colName.isEmpty()) {
                throw badRequest("У каждой колонки должно быть имя.");
            }

            if (!isValidName(colName)) {
                throw badRequest("Имя колонки '" + colName + "' должно содержать только строчные латинские буквы, цифры и подчёркивания, длина от 3 до 63 символов.");
            }

            if ("id".equals(colName)) {
                throw badRequest("Имя колонки не может быть 'id' — оно зарезервировано системой.");
            }

            if (!uniqueColumnNames.add(colName)) {
                throw badRequest("Имена колонок должны быть уникальными. Повтор: '" + colName + "'");
            }

            if ((colType == null || colType.isEmpty()) || !SUPPORTED_TYPES.contains(colType.toUpperCase())) {
                throw badRequest("Тип колонки '" + colType + "' не поддерживается. Допустимые типы: " + SUPPORTED_TYPES);
            }
        }
    }

    private BadRequestException badRequest(String message) {
        return new BadRequestException(message);
    }
}
