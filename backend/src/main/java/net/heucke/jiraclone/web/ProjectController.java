package net.heucke.jiraclone.web;

import net.heucke.jiraclone.api.Dtos.IssueSummaryDto;
import net.heucke.jiraclone.api.Dtos.PageDto;
import net.heucke.jiraclone.api.Dtos.ProjectDto;
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

    public ProjectController(ProjectService projectService, IssueService issueService) {
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @GetMapping
    public List<ProjectDto> list() {
        return projectService.list();
    }

    @GetMapping("/{key}")
    public ProjectDto get(@PathVariable String key) {
        return projectService.get(key);
    }

    @GetMapping("/{key}/issues")
    public PageDto<IssueSummaryDto> issues(@PathVariable String key,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "25") int size) {
        return issueService.search(key, status, type, q, page, size);
    }
}
