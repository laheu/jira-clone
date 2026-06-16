package net.heucke.jiraclone.web;

import net.heucke.jiraclone.api.Dtos.IssueSummaryDto;
import net.heucke.jiraclone.api.Dtos.PageDto;
import net.heucke.jiraclone.api.Dtos.ProjectDto;
import net.heucke.jiraclone.api.Dtos.ProjectMetaDto;
import net.heucke.jiraclone.api.Dtos.StatusRef;
import net.heucke.jiraclone.api.Dtos.TypeRef;
import net.heucke.jiraclone.repo.MetaRepository;
import net.heucke.jiraclone.service.IssueService;
import net.heucke.jiraclone.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final IssueService issueService;
    private final MetaRepository metaRepository;

    public ProjectController(ProjectService projectService, IssueService issueService,
                             MetaRepository metaRepository) {
        this.projectService = projectService;
        this.issueService = issueService;
        this.metaRepository = metaRepository;
    }

    @GetMapping
    public List<ProjectDto> list() {
        return projectService.list();
    }

    @GetMapping("/{key}")
    public ProjectDto get(@PathVariable String key) {
        return projectService.get(key);
    }

    /** Filter values (statuses, types) restricted to what actually occurs in the project. */
    @GetMapping("/{key}/meta")
    public ProjectMetaDto meta(@PathVariable String key) {
        return new ProjectMetaDto(
                metaRepository.listProjectStatuses(key).stream()
                        .map(s -> StatusRef.of(s.id(), s.name(), s.category()))
                        .toList(),
                metaRepository.listProjectTypes(key).stream()
                        .map(t -> new TypeRef(t.id(), t.name()))
                        .toList());
    }

    @GetMapping("/{key}/issues")
    public PageDto<IssueSummaryDto> issues(@PathVariable String key,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "updated") String sort,
                                           @RequestParam(defaultValue = "desc") String order,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "25") int size) {
        return issueService.search(key, status, type, q, sort, !"asc".equalsIgnoreCase(order), page, size);
    }
}
