package com.sqs.controller;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping("/profile")
public class SqsWriterController {
	
	private final static String EMPTY_EMPLOYEE_ID = "Employee ID is empty";
	private final static String EMPTY_NAME = "Employee Name is empty";
	private final static Logger logger = (Logger) LoggerFactory.getLogger(SqsWriterController.class);


	@Autowired
	private AmazonSQSAsync amazonSqs;

	
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
    
	@RequestMapping(value="/updateData", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> updateData(@RequestBody Employee employee){
		String successMessage = "Success";
		String failureMessage = "Request failed";
		if(employee.getEmpID() <= 0) {
			return new ResponseEntity<String>(EMPTY_EMPLOYEE_ID, HttpStatus.BAD_REQUEST);
		}
		if(employee.getName() == null || employee.getName().isEmpty()) {
			return new ResponseEntity<String>(EMPTY_NAME, HttpStatus.BAD_REQUEST);
		}
		try {
			queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload( employee.getEmpID() + employee.getName()).build());
			return new ResponseEntity<String>(successMessage, HttpStatus.ACCEPTED);
		}catch (AmazonServiceException e){
			logger.error(e.getMessage());
			return new ResponseEntity<String>(failureMessage, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value="/readData", method = {RequestMethod.GET})
	public void messageProcessor() {
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		executorService.execute(new Runnable() {
		    public void run() {
		    	processData();
		    }
		});

		executorService.shutdown();
	}
	
	private void processData(){
		try {
		    String queueUrl = amazonSqs.getQueueUrl("sqsqueue").getQueueUrl();
		    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
		    receiveMessageRequest.setQueueUrl(queueUrl);
		    receiveMessageRequest.setWaitTimeSeconds(10); // messages for next 10 seconds
		    receiveMessageRequest.setMaxNumberOfMessages(10); // Setting batch size to 10
		    ReceiveMessageResult receiveMessageResult = amazonSqs.receiveMessage(receiveMessageRequest);
		    List<Message> messages = receiveMessageResult.getMessages(); // batch of messages
		    for(Message messageObject : messages) {
		    	String message = messageObject.getBody();
		    	logger.info(message);	
		    	//Acknowledge message
		    	deleteMessage(messageObject,queueUrl);
		    }
		}catch (AmazonServiceException e) {
			logger.error(e.getMessage());

		}

	}
	
	
	private void deleteMessage(Message messageObject, String queueUrl) {
	    final String messageReceiptHandle = messageObject.getReceiptHandle();
	    amazonSqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));

	}
}
