package kg.service.dynamictablebuilder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.service.dynamictablebuilder.dto.request.TableCreatedRequest;
import kg.service.dynamictablebuilder.dto.response.TableResponse;
import kg.service.dynamictablebuilder.dto.response.TableResponseColumnSummaryResponse;
import kg.service.dynamictablebuilder.service.DynamicTableBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/dynamic-tables")
@RequiredArgsConstructor
@Tag(name = "API для динамического создание схем таблиц", description = "API для динамического создание схем таблиц и управление")
public class DynamicTableBuilderController {

    private final DynamicTableBuilderService service;

    @PostMapping("/schemas")
    @Operation(summary = "API для создание схем таблиц")
    public ResponseEntity<TableResponse> schemas(@RequestBody TableCreatedRequest request) {
        TableResponse response = service.createTable(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/schemas")
    @Operation(summary = "API для получения схемы динамической таблицы")
    public ResponseEntity<TableResponse> finById(@RequestParam String tableName) {
        TableResponse response = service.findByName(tableName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/schemas/all")
    @Operation(summary = "API для получения списка всех динамических таблиц")
    public ResponseEntity<List<TableResponseColumnSummaryResponse>> getAll() {
        List<TableResponseColumnSummaryResponse> response = service.getAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


}
