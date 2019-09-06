package com.dfn.bamboointegration.config;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReleaseNoteTaskConfigurator extends AbstractTaskConfigurator {
    @Override
    @NotNull
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
                                                     @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("buildRevision", params.getString("buildRevision"));
        config.put("buildPreviousRevision", params.getString("buildPreviousRevision"));
        config.put("releaseLabel", params.getString("releaseLabel"));
        config.put("sourceFileLocation", params.getString("sourceFileLocation"));
        config.put("destinationFileLocation", params.getString("destinationFileLocation"));
        return config;
    }
}
