package kg.service.dynamictablebuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class PaginationResponse<T> {
    private List<T> content;
    private PageableDto pageable;
    private int totalPages;
    private long totalElements;

    @Data
    @AllArgsConstructor
    public static class PageableDto {
        private int pageNumber;
        private int pageSize;
    }
}
