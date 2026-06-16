package net.heucke.jiraclone.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal parser for Jira's OSWorkflow XML descriptors
 * ({@code jiraworkflows.descriptor}). Extracts the transitions available
 * from a given workflow step: the step's own actions, referenced
 * common-actions and global-actions. The target status is taken from the
 * {@code jira.status.id} meta of the target step.
 */
@Component
public class WorkflowDescriptorParser {

    public record Transition(int actionId, String name, String targetStatusId) {
    }

    public List<Transition> transitionsForStep(String descriptorXml, int currentStepId) throws Exception {
        Document doc = parseSecurely(descriptorXml);
        Element workflow = doc.getDocumentElement();

        Map<Integer, Element> stepsById = new LinkedHashMap<>();
        Map<Integer, String> statusIdByStep = new HashMap<>();
        Element stepsElement = firstChild(workflow, "steps");
        if (stepsElement != null) {
            for (Element step : children(stepsElement, "step")) {
                int id = Integer.parseInt(step.getAttribute("id"));
                stepsById.put(id, step);
                String statusId = metaValue(step, "jira.status.id");
                if (statusId != null) {
                    statusIdByStep.put(id, statusId);
                }
            }
        }

        Map<Integer, Element> commonActions = new HashMap<>();
        Element commonElement = firstChild(workflow, "common-actions");
        if (commonElement != null) {
            for (Element action : children(commonElement, "action")) {
                commonActions.put(Integer.parseInt(action.getAttribute("id")), action);
            }
        }

        Element currentStep = stepsById.get(currentStepId);
        List<Element> actions = new ArrayList<>();
        if (currentStep != null) {
            Element actionsElement = firstChild(currentStep, "actions");
            if (actionsElement != null) {
                actions.addAll(children(actionsElement, "action"));
                for (Element ref : children(actionsElement, "common-action")) {
                    Element resolved = commonActions.get(Integer.parseInt(ref.getAttribute("id")));
                    if (resolved != null) {
                        actions.add(resolved);
                    }
                }
            }
        }
        Element globalElement = firstChild(workflow, "global-actions");
        if (globalElement != null) {
            actions.addAll(children(globalElement, "action"));
        }

        Map<Integer, Transition> transitions = new LinkedHashMap<>();
        for (Element action : actions) {
            int actionId = Integer.parseInt(action.getAttribute("id"));
            int targetStep = targetStep(action, currentStepId);
            transitions.putIfAbsent(actionId, new Transition(
                    actionId,
                    action.getAttribute("name"),
                    statusIdByStep.get(targetStep)));
        }
        return List.copyOf(transitions.values());
    }

    private static int targetStep(Element action, int currentStepId) {
        NodeList results = action.getElementsByTagName("unconditional-result");
        if (results.getLength() > 0) {
            String step = ((Element) results.item(0)).getAttribute("step");
            if (!step.isBlank()) {
                int target = Integer.parseInt(step);
                // step -1 means "stay in the current step" in OSWorkflow
                return target > 0 ? target : currentStepId;
            }
        }
        return currentStepId;
    }

    private static String metaValue(Element parent, String metaName) {
        for (Element meta : children(parent, "meta")) {
            if (metaName.equals(meta.getAttribute("name"))) {
                return meta.getTextContent().trim();
            }
        }
        return null;
    }

    private static Element firstChild(Element parent, String name) {
        List<Element> elements = children(parent, name);
        return elements.isEmpty() ? null : elements.getFirst();
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> result = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && name.equals(element.getTagName())) {
                result.add(element);
            }
        }
        return result;
    }

    private static Document parseSecurely(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }
}
