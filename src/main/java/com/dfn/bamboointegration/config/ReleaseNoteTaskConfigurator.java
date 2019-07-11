package com.dfn.bamboointegration.config;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.johnson.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReleaseNoteTaskConfigurator extends AbstractTaskConfigurator {
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
                                                     @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("buildRevision", params.getString("buildRevision"));
        config.put("buildPreviousRevision", params.getString("buildPreviousRevision"));
        return config;
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params,
                         @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);
        final String sayValue = params.getString("buildRevision");
        if (StringUtils.isEmpty(sayValue)) {
        }
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context,
                                       @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        context.put("buildRevision", taskDefinition.getConfiguration().get("buildRevision"));
        context.put("buildPreviousRevision", taskDefinition.getConfiguration().get("buildPreviousRevision"));
    }
}
