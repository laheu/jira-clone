package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.api.Dtos.IssueDetailDto;
import net.heucke.jiraclone.api.Dtos.IssueSummaryDto;
import net.heucke.jiraclone.api.Dtos.PageDto;
import net.heucke.jiraclone.repo.Rows.IssueRow;
import net.heucke.jiraclone.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class RepositoryIntegrationTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueService issueService;

    @Test
    void listsAllProjectsWithIssueCounts() {
        var projects = projectRepository.findAll();

        // ordered by project name: Demo Projekt, IT Operations, Website Relaunch
        assertThat(projects).extracting(Rows.ProjectRow::key)
                .containsExactly("DEMO", "OPS", "WEB");
        assertThat(projects).filteredOn(p -> p.key().equals("DEMO"))
                .singleElement()
                .extracting(Rows.ProjectRow::issueCount)
                .isEqualTo(12L);
    }

    @Test
    void searchSupportsFiltersAndPaging() {
        assertThat(issueRepository.count("DEMO", null, null, null)).isEqualTo(12);
        assertThat(issueRepository.search("demo", null, null, null, 0, 5)).hasSize(5);

        assertThat(issueRepository.search("DEMO", "1", null, null, 0, 50))
                .isNotEmpty()
                .allMatch(row -> row.statusId().equals("1"));
        assertThat(issueRepository.search("DEMO", null, "1", null, 0, 50))
                .isNotEmpty()
                .allMatch(row -> row.typeName().equals("Bug"));
        assertThat(issueRepository.search("DEMO", null, null, "backup", 0, 50))
                .singleElement()
                .extracting(IssueRow::issueKey)
                .isEqualTo("DEMO-2");
    }

    @Test
    void findByKeyBuildsIssueKeyFromProjectAndNumber() {
        assertThat(issueRepository.findByKey("DEMO", 1))
                .isPresent()
                .get()
                .satisfies(row -> {
                    assertThat(row.issueKey()).isEqualTo("DEMO-1");
                    assertThat(row.summary()).contains("Login-Seite");
                });
        assertThat(issueRepository.findByKey("DEMO", 999)).isEmpty();
    }

    @Test
    void issueDetailContainsCommentsLabelsAndTransitions() {
        IssueDetailDto issue = issueService.get("DEMO-1");

        assertThat(issue.status().name()).isEqualTo("Open");
        assertThat(issue.status().category()).isEqualTo("TO_DO");
        assertThat(issue.assignee().displayName()).isEqualTo("Lars Heucke");
        assertThat(issue.reporter().displayName()).isEqualTo("Anna Schmidt");
        assertThat(issue.labels()).containsExactly("login", "security");
        assertThat(issue.comments()).hasSize(3);
        assertThat(issue.comments().getFirst().author().displayName()).isEqualTo("Lars Heucke");
        assertThat(issue.transitions()).extracting(t -> t.name())
                .containsExactlyInAnyOrder("Start Progress", "Close Issue", "Resolve Issue");
    }

    @Test
    void issueListResolvesAssigneesAndOrdersByUpdated() {
        PageDto<IssueSummaryDto> page = issueService.search("DEMO", null, null, null, 0, 3);

        assertThat(page.total()).isEqualTo(12);
        assertThat(page.items()).hasSize(3);
        // DEMO-12 has the most recent "updated" timestamp in the seed data
        assertThat(page.items().getFirst().key()).isEqualTo("DEMO-12");
        assertThat(page.items().getFirst().assignee().displayName()).isEqualTo("Maria Lopez");
    }
}
