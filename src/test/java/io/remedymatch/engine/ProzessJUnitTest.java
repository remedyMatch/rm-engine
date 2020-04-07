package io.remedymatch.engine;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;


/*@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
        properties = {
                "camunda.bpm.generate-unique-process-engine-name=true",
                "camunda.bpm.generate-unique-process-application-name=true",
                "spring.datasource.generate-unique-name=true"
        }
) */


public class ProzessJUnitTest {
    @Rule
    /*@ClassRule*/
    public ProcessEngineRule rule = new ProcessEngineRule();

    /*@Autowired
    ProcessEngine processEngine;

     */

    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    @Test
    @Deployment(resources = "angebotAnfrageProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess");

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_beantworten");
        complete(task(), withVariables("angenommen", true));

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_match_prozess_starten").
                externalTask().hasTopicName("angebotMatchProzessStarten");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("MatchProzessGestartetEndEvent");


    }
}
