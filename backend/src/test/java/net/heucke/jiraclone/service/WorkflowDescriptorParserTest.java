package net.heucke.jiraclone.service;

import net.heucke.jiraclone.service.WorkflowDescriptorParser.Transition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowDescriptorParserTest {

    private static final String DESCRIPTOR = """
            <workflow>
              <common-actions>
                <action id="2" name="Close Issue">
                  <results>
                    <unconditional-result old-status="Finished" status="Closed" step="6"/>
                  </results>
                </action>
              </common-actions>
              <steps>
                <step id="1" name="Open">
                  <meta name="jira.status.id">1</meta>
                  <actions>
                    <common-action id="2"/>
                    <action id="4" name="Start Progress">
                      <results>
                        <unconditional-result old-status="Finished" status="In Progress" step="3"/>
                      </results>
                    </action>
                  </actions>
                </step>
                <step id="3" name="In Progress">
                  <meta name="jira.status.id">3</meta>
                  <actions>
                    <common-action id="2"/>
                  </actions>
                </step>
                <step id="6" name="Closed">
                  <meta name="jira.status.id">6</meta>
                  <actions>
                    <action id="7" name="Reopen Issue">
                      <results>
                        <unconditional-result old-status="Finished" status="Open" step="1"/>
                      </results>
                    </action>
                  </actions>
                </step>
              </steps>
            </workflow>
            """;

    private final WorkflowDescriptorParser parser = new WorkflowDescriptorParser();

    @Test
    void resolvesDirectAndCommonActionsOfAStep() throws Exception {
        List<Transition> transitions = parser.transitionsForStep(DESCRIPTOR, 1);

        assertThat(transitions).extracting(Transition::name)
                .containsExactlyInAnyOrder("Start Progress", "Close Issue");
        assertThat(transitions).filteredOn(t -> t.actionId() == 4)
                .singleElement()
                .extracting(Transition::targetStatusId)
                .isEqualTo("3");
        assertThat(transitions).filteredOn(t -> t.actionId() == 2)
                .singleElement()
                .extracting(Transition::targetStatusId)
                .isEqualTo("6");
    }

    @Test
    void closedStepOnlyOffersReopen() throws Exception {
        List<Transition> transitions = parser.transitionsForStep(DESCRIPTOR, 6);

        assertThat(transitions).singleElement().satisfies(t -> {
            assertThat(t.name()).isEqualTo("Reopen Issue");
            assertThat(t.targetStatusId()).isEqualTo("1");
        });
    }

    @Test
    void unknownStepYieldsNoTransitions() throws Exception {
        assertThat(parser.transitionsForStep(DESCRIPTOR, 99)).isEmpty();
    }
}
