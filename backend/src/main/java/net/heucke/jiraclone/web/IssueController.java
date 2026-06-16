package net.heucke.jiraclone.web;

import net.heucke.jiraclone.api.Dtos.IssueDetailDto;
import net.heucke.jiraclone.service.IssueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/{issueKey}")
    public IssueDetailDto get(@PathVariable String issueKey) {
        return issueService.get(issueKey);
    }
}
