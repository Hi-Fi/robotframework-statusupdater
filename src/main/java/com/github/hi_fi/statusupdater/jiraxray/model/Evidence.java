package com.github.hi_fi.statusupdater.jiraxray.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.ws.commons.util.Base64;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evidence {
    private String data;
    private String filename;
    private String contentType;
    
    public Evidence evidenceWithFilename(String fileName) {
        File evidence = new File(fileName);
        this.filename = evidence.getName();
        try {
            this.contentType = Files.probeContentType(evidence.toPath());
            this.data = Base64.encode(FileUtils.readFileToByteArray(evidence));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        
        return this;
    }
}
