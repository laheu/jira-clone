package net.heucke.jiraclone.service;

import net.heucke.jiraclone.api.Dtos.ProjectDto;
import net.heucke.jiraclone.api.Dtos.UserRef;
import net.heucke.jiraclone.repo.ProjectRepository;
import net.heucke.jiraclone.repo.Rows.ProjectRow;
import net.heucke.jiraclone.repo.Rows.UserRow;
import net.heucke.jiraclone.repo.UserRepository;
import net.heucke.jiraclone.web.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectDto> list() {
        List<ProjectRow> rows = projectRepository.findAll();
        Set<String> leadKeys = rows.stream()
                .map(ProjectRow::leadKey)
                .collect(Collectors.toSet());
        Map<String, UserRow> users = userRepository.resolve(leadKeys);
        return rows.stream().map(row -> toDto(row, users)).toList();
    }

    public ProjectDto get(String key) {
        ProjectRow row = projectRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("Projekt nicht gefunden: " + key));
        Map<String, UserRow> users = userRepository.resolve(
                row.leadKey() == null ? Set.of() : Set.of(row.leadKey()));
        return toDto(row, users);
    }

    private static ProjectDto toDto(ProjectRow row, Map<String, UserRow> users) {
        UserRef lead = null;
        if (row.leadKey() != null) {
            UserRow user = users.get(row.leadKey());
            lead = user != null
                    ? new UserRef(user.key(), user.displayName(), user.email())
                    : new UserRef(row.leadKey(), row.leadKey(), null);
        }
        return new ProjectDto(row.id(), row.key(), row.name(), row.description(), lead, row.issueCount());
    }
}
