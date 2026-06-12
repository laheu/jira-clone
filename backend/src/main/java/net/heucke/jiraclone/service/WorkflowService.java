package net.heucke.jiraclone.service;

import net.heucke.jiraclone.api.Dtos.StatusRef;
import net.heucke.jiraclone.api.Dtos.TransitionDto;
import net.heucke.jiraclone.repo.MetaRepository;
import net.heucke.jiraclone.repo.Rows.IssueRow;
import net.heucke.jiraclone.repo.Rows.StatusRow;
import net.heucke.jiraclone.repo.WorkflowRepository;
import net.heucke.jiraclone.service.WorkflowDescriptorParser.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Determines the transitions available for an issue, for display only.
 * Deliberately defensive: any unexpected workflow configuration results in
 * an empty list instead of an error, since transitions are a nice-to-have.
 */
@Service
public class WorkflowService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);

    private final WorkflowRepository workflowRepository;
    private final MetaRepository metaRepository;
    private final WorkflowDescriptorParser parser;

    public WorkflowService(WorkflowRepository workflowRepository,
                           MetaRepository metaRepository,
                           WorkflowDescriptorParser parser) {
        this.workflowRepository = workflowRepository;
        this.metaRepository = metaRepository;
        this.parser = parser;
    }

    public List<TransitionDto> transitionsFor(IssueRow issue) {
        try {
            if (issue.workflowId() == null) {
                return List.of();
            }
            Optional<Integer> stepId = workflowRepository.findCurrentStepId(issue.workflowId());
            Optional<Long> schemeId = workflowRepository.findWorkflowSchemeId(issue.projectId());
            if (stepId.isEmpty() || schemeId.isEmpty()) {
                return List.of();
            }
            Optional<String> descriptor = workflowRepository
                    .findWorkflowName(schemeId.get(), issue.typeId())
                    .flatMap(workflowRepository::findDescriptor);
            if (descriptor.isEmpty()) {
                return List.of();
            }
            List<Transition> transitions = parser.transitionsForStep(descriptor.get(), stepId.get());
            Map<String, StatusRow> statuses = metaRepository.statusById();
            return transitions.stream()
                    .map(t -> new TransitionDto(t.actionId(), t.name(), toStatusRef(t.targetStatusId(), statuses)))
                    .toList();
        } catch (Exception e) {
            log.warn("Could not determine transitions for issue {}: {}", issue.issueKey(), e.toString());
            return List.of();
        }
    }

    private static StatusRef toStatusRef(String statusId, Map<String, StatusRow> statuses) {
        if (statusId == null) {
            return null;
        }
        StatusRow status = statuses.get(statusId);
        return status != null
                ? StatusRef.of(status.id(), status.name(), status.category())
                : StatusRef.of(statusId, statusId, null);
    }
}
