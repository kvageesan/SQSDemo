package com.sqs.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class SqsWriterController {
	
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    private int id;
    private String name;
    
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
    
	@RequestMapping(value="/updateData", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public void updateData(@RequestBody Employee employee) {
		id = employee.getEmpID();
		name = employee.getName();
		queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(id + name).build());
	}
}
