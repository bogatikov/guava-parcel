package com.guava.guavaparcel.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"content", "currentPage", "totalElements", "numberOfElements"})
public class Page<T> {
    private List<T> content;
    private Integer currentPage;
    private Long totalElements;
    private Integer numberOfElements;
}
