package com.sqs.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.sqs.controller.Employee;
import com.sqs.controller.SqsWriterController;

@WebMvcTest(SqsWriterController.class)
public class SqsWriterControllerTests {

	
	@MockBean
	private QueueMessagingTemplate template;
	
	//@MockBean
	//private AmazonSQSAsync amazonSqs;
	
	@Autowired
	private SqsWriterController sqsWriterController;
	
	
	@Test
	public void testWriteSqsMessage_Success() {
		Employee employee = new Employee();
		employee.setEmpID(1);
		employee.setName("Name");
		ResponseEntity<String> message = sqsWriterController.updateData(employee);
		assertEquals(message.getBody(), "Success");
		assertEquals(message.getStatusCode(), HttpStatus.ACCEPTED);
		
	}
	
	@Test
	public void testWriteSqsMessage_ErrorRequest() {
		Employee employee = new Employee();
		employee.setEmpID(0);
		employee.setName("Name");
		
		ResponseEntity<String> message = sqsWriterController.updateData(employee);
		message = sqsWriterController.updateData(employee);
		assertEquals(message.getBody().toString(), "Employee ID is empty");
		assertEquals(message.getStatusCode(), HttpStatus.BAD_REQUEST);



		employee.setEmpID(1);
		employee.setName("");
		message = sqsWriterController.updateData(employee);
		assertEquals(message.getBody().toString(), "Employee Name is empty");
		assertEquals(message.getStatusCode(), HttpStatus.BAD_REQUEST);


		employee.setEmpID(1);
		employee.setName(null);
		message = sqsWriterController.updateData(employee);
		assertEquals(message.getBody().toString(), "Employee Name is empty");
		assertEquals(message.getStatusCode(), HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	public void testProcessData_Success() {
		sqsWriterController.messageProcessor();
	}
	
}
