package com.neuralytics.utils;

import java.util.HashMap;
import java.util.Map;

public class CiCdDetector {
    private static final Map<String, String> CI_CD_VARS = new HashMap<>();

    static {
        CI_CD_VARS.put("Jenkins", "JOB_NAME"); // Jenkins
        CI_CD_VARS.put("GitLab CI", "CI_JOB_NAME"); // GitLab CI
        CI_CD_VARS.put("CircleCI", "CIRCLE_JOB"); // CircleCI
        // Add more CI/CD platforms as needed
    }

    public static Map<String, String> getEnvironmentInfo() {
        Map<String, String> info = new HashMap<>();
        for (Map.Entry<String, String> entry : CI_CD_VARS.entrySet()) {
            String varValue = System.getenv(entry.getValue());
            if (varValue != null) {
                info.put(entry.getKey(), varValue);
            }
        }
        return info;
    }
}