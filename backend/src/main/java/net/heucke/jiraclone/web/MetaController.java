package net.heucke.jiraclone.web;

import net.heucke.jiraclone.api.Dtos.MetaDto;
import net.heucke.jiraclone.api.Dtos.PriorityRef;
import net.heucke.jiraclone.api.Dtos.StatusRef;
import net.heucke.jiraclone.api.Dtos.TypeRef;
import net.heucke.jiraclone.repo.MetaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetaController {

    private final MetaRepository metaRepository;

    public MetaController(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    @GetMapping("/api/meta")
    public MetaDto meta() {
        return new MetaDto(
                metaRepository.listStatuses().stream()
                        .map(s -> StatusRef.of(s.id(), s.name(), s.category()))
                        .toList(),
                metaRepository.listTypes().stream()
                        .map(t -> new TypeRef(t.id(), t.name()))
                        .toList(),
                metaRepository.listPriorities().stream()
                        .map(p -> new PriorityRef(p.id(), p.name(), p.color()))
                        .toList());
    }
}
