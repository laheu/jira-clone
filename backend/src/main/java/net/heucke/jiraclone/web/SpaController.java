package net.heucke.jiraclone.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the SPA entry page for client-side routes so deep links and
 * browser reloads work when the built frontend is bundled into the jar.
 */
@Controller
public class SpaController {

    @GetMapping({"/login", "/projects/{key}", "/issues/{issueKey}"})
    public String forward() {
        return "forward:/index.html";
    }
}
