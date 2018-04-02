package com.github.hi_fi.statusupdater.jiraxray.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Info {
    private String summary;
    private String description;
    private String user;
    private String testPlanKey;
    private String startDate;
    private String endDate;
}
