package com.marquesdev.park_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marquesdev.park_api.dto.ClientResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> {
    private List<T> content;
    @JsonProperty("totalElements")
    private Long totalElements;
    @JsonProperty("totalPages")
    private Integer totalPages;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("number")
    private Integer number;
}
