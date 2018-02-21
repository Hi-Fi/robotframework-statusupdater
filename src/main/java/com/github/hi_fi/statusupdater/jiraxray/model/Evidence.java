package com.github.hi_fi.statusupdater.jiraxray.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evidence {
    private String data;
    private String filename;
    private String contentType;
}
