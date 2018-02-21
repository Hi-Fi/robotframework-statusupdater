package com.github.hi_fi.statusupdater.jiraxray.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Test {
    private String testKey;
    private String status;
    private List<Evidence> evidences;
    private String start;
    private String finish;
    private String comment;
}
