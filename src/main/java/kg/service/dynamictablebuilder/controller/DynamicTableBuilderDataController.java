package kg.service.dynamictablebuilder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.service.dynamictablebuilder.dto.PaginationResponse;
import kg.service.dynamictablebuilder.service.DynamicTableBuilderDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/dynamic-tables/data")
@RequiredArgsConstructor
@Tag(name = "API Для Универсальных CRUD-операции", description = "API Для Универсальных CRUD-операции для Данных в Динамических Таблицах")
public class DynamicTableBuilderDataController {

    private final DynamicTableBuilderDataService service;

    @PostMapping("/save")
    @Operation(summary = "API для создание записи таблиц")
    public ResponseEntity<Map<String, String>> schemas(@RequestParam String tableName, @RequestBody Map<String, String> request) {
        Map<String, String> response = service.saveData(tableName, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/pagination")
    @Operation(summary = "API для чтения списка записей (с пагинацией)")
    public PaginationResponse<Map<String, Object>> getTableData(@RequestParam String tableName, @RequestParam int page, @RequestParam int size) {
        PaginationResponse<Map<String, Object>> paginationResponse = service.getTableData(tableName, page, size);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paginationResponse).getBody();
    }

    @GetMapping("/findById")
    @Operation(summary = "API для чтения записи по ID")
    public ResponseEntity<Map<String, Object>> getById(@RequestParam String tableName, @RequestParam Long id) {
        Map<String, Object> result = service.getById(tableName, id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    @Operation(summary = "API для обновления записи по ID")
    public ResponseEntity<Map<String, Object>> update(@RequestParam Long id, @RequestParam String tableName, @RequestBody Map<String, Object> payload) {
        Map<String, Object> updated = service.update(tableName, id, payload);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deleted")
    @Operation(summary = "API для удаления записи по ID")
    public ResponseEntity<Void> delete(@RequestParam String tableName, @RequestParam Long id) {
        service.delete(tableName, id);
        return ResponseEntity.ok().build();
    }


}
