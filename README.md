# SQSDemo
---------

SQS reader and writer demo
This is a simple projetc to create employee profile. 

**Update profile:

Employee can update their ID and name to the system.
REST endpoint{POST request}: 
/profile/updateData takes Employee ID and Name in JSON format and drops it to Amazon SQS.
Employee id cannot be null. Employee name must be not null and not empty.
Sample request format in JSON:
  {
  id: 1,
  name: "Employee1"
  }

This end point returns 202 Success and 400 Bad request for a missing input.

**Message processor:

  There is a consumer service which reads the messages from amazon SQS and process it. To keep this implementation simple, the processer reads and clears the queue.
  Message processor can be invoked through the REST endpoint{GET request}: /profile/readData. 
    On a successful completion, method returns 200 ok and on error it throws Expectation failed response.
