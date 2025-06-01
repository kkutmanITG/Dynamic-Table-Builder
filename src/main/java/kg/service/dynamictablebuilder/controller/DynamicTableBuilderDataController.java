package kg.service.dynamictablebuilder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping()
    @Operation(summary = "API для создание схем таблиц")
    public ResponseEntity<Map<String, String>> schemas(@RequestParam String tableName, @RequestBody Map<String, String> request) {
        Map<String, String> response = service.saveData(tableName, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
