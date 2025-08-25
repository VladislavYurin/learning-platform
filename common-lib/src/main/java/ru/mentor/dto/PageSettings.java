package ru.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageSettings {
    private int pageNumber;
    private int pageSize;
}
