package com.example.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private Integer page;
    private Integer limit;
    private Integer totalPages;
    private Long totalElements;

    // Nếu muốn có constructor rỗng
    public PageResponse() {}
}
