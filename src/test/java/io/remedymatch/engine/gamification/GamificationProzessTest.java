package io.remedymatch.engine.gamification;

import lombok.val;
import org.camunda.bpm.dmn.engine.*;
import org.camunda.bpm.dmn.engine.test.DmnEngineRule;
import org.camunda.bpm.engine.test.*;
import org.camunda.bpm.engine.variable.*;
import org.junit.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class GamificationProzessTest {

    private static String PROCESS_KEY = "gamification_prozess";
    private static String EVENT_START = "gamification_prozess_start";
    private static String TASK_KARMA_ZUORDNUNG = "gamification_prozess_karma_zuordnung";
    private static String TASK_KARMA_SPEICHERN = "gamification_prozess_karma_speichern";
    private static String TASK_BADGE_ZUORDNUNG = "gamification_prozess_badge_zuordnung";
    private static String TASK_BADGE_VERSENDEN = "gamification_prozess_badge_versenden";
    private static String EVENT_KARMA_VERGEBEN = "gamification_prozess_karma_vergeben";
    private static String EVENT_BADGE_VERSCHICKT = "gamification_prozess_badge_versendet";

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Rule
    public DmnEngineRule dmnEngineRule = new DmnEngineRule();

    private DmnEngine dmnEngine;
    private DmnDecision dmnDecision;
    private DmnDecisionResult dmnDecisionResult;

    @Before
    public void setup() throws FileNotFoundException {
        // init the process Engine
        processEngineRule = new ProcessEngineRule();
        init(processEngineRule.getProcessEngine());

        // init the dmn rule engine
        InputStream dmnFileInputStream = new BufferedInputStream(new FileInputStream("src/main/resources/bpmn/karma-entscheidung.dmn"));
        VariableMap variables = Variables.createVariables();

        this.dmnEngine = dmnEngineRule.getDmnEngine();
        this.dmnDecision = dmnEngine.parseDecision("Decision_1bvey2j", dmnFileInputStream);
        this.dmnDecisionResult = dmnEngine.evaluateDecision(dmnDecision, variables);
    }

    /**
     * Test,ob der Prozess erfolgreich startet.
     */
    @Test
    @Deployment(resources = "bpmn/gamificationProzess.bpmn")
    public void prozessIstGestartet() {
        val instanz = runtimeService().startProcessInstanceByKey(PROCESS_KEY);
        assertThat(instanz).isActive();
        assertThat(instanz).isWaitingAt(EVENT_START);
    }

    /**
     *
     */
    @Test
    @Deployment(resources = "bpmn/gamificationProzess.bpmn")
    public void karmaPunkteVergeben() {

        val instanz = runtimeService().startProcessInstanceById(PROCESS_KEY);
        execute(job());

    }


    /**
     * Der Empfaenger erklärt sich bereit, die Ware vom Spender abzuholen
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void badgeVergebenWennPunkteAusreichend() {

    }


}
