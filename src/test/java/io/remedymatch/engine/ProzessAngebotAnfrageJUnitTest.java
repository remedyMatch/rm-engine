package io.remedymatch.engine;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.remedymatch.engine.benachrichtigung.BenachrichtigungService;

//@SpringBootTest(
//		// ...other parameters...
//		properties = { "camunda.bpm.generate-unique-process-engine-name=true",
//				// this is only needed if a SpringBootProcessApplication
//				// is used for the test
//				"camunda.bpm.generate-unique-process-application-name=true",
//				"spring.datasource.generate-unique-name=true",
//		// additional properties...
//		})
public class ProzessAngebotAnfrageJUnitTest {

//	@Rule
//	public ProcessEngineRule rule;

//	@Rule
//	public ProcessEngineRule rule = new ProcessEngineRule();

//	@Before
//	public void setup() {
//		init(rule.getProcessEngine());
//	}

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
//    
//	@Autowired
//	ProcessEngine processEngine;

	@Autowired
	private BenachrichtigungService benachrichtigungService;

	@Before
	public void setUp() {
		init(rule.getProcessEngine());

		Mocks.register("benachrichtigungService", benachrichtigungService);
	}

//	@Test
	@Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
	public void testHappyPath() {
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess",
				withVariables("institution", "Camunda"));

		assertThat(processInstance).isWaitingAt("StartEvent_1");
		execute(job());

		assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_beantworten");
		assertThat(task()).hasCandidateGroup("Camunda");
		complete(task(), withVariables("angenommen", true));

		assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_match_prozess_starten").externalTask()
				.hasTopicName("angebotMatchProzessStarten");
		complete(externalTask(), withVariables("angebotmenge", 3));

		assertThat(processInstance).isEnded().hasPassed("MatchProcessStartedEndEvent");
	}

	@Test
	@Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
	public void testHappyPathMitAufgebrauchterAngebotsMenge() {
		ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("angebot_anfrage_prozess")
				.setVariable("angebotmenge", 0).startAfterActivity("angebot_anfrage_prozess_match_prozess_starten")
				.execute();

		assertThat(processInstance).isWaitingAt("AnfragenSuchenUndLoeschenTask").externalTask()
				.hasTopicName("AnfragenSuchenUndLoeschen");
		complete(externalTask());

		assertThat(processInstance).isEnded().hasPassed("MatchProcessStartedEndEvent");
	}

	@Test
	@Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
	public void testRejectOffer() {

		ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("angebot_anfrage_prozess")
				.setVariable("angenommen", false).startAfterActivity("angebot_anfrage_prozess_beantworten").execute();

		assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_stornierung_verarbeiten").externalTask()
				.hasTopicName("angebotAnfrageAblehnen");
		complete(externalTask());

		assertThat(processInstance).isEnded().hasPassed("EndEvent_0txu0vj");

	}

	@Test
	@Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
	public void testCancleOffer() {
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess",
				withVariables("institution", "Camunda"));

		runtimeService().createMessageCorrelation("angebot_anfrage_stornieren").setVariable("reason", "Mag nicht mehr.")
				.processInstanceVariableEquals("institution", "Camunda").correlateWithResult().getProcessInstance();

		assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_stornierung").externalTask()
				.hasTopicName("angebotAnfrageAblehnen");
		complete(externalTask());

		assertThat(processInstance).isEnded().hasPassed("EndEvent_0zwid5r");

	}

}
