package com.example.cv.github.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ToString
@Data
public class RepositoriesDto {

    private String id;

    private String node_id;

    private String name;

    private String full_name;

    public RepositoriesDto() {};
}
