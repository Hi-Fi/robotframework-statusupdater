package com.github.hi_fi.statusupdater.jiraxray.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class TestExecution {
    private String testExecutionKey;
    private Info info;
    private List<Test> tests;
}
