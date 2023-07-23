# DataSphere

A data collection platform that is being used by customers in 50+ countries in over 250 organizations and has powered data collection for over 11 million responses. Its features include team management, multilingual forms, and offline data collection. Customers use this platform to power their most critical activities — from governments delivering vaccines to small business owners managing their daily inventory, to a zoo monitoring a rare wildlife species.

## Problem Statement
A data collection platform that is being used by customers in 50+ countries in over 250 organizations and has powered data collection for over 11 million responses. Its features include team management, multilingual forms, and offline data collection. Customers use this platform to power their most critical activities — from governments delivering vaccines to small business owners managing their daily inventory, to a zoo monitoring a rare wildlife species.

1. One of the clients wanted to search for slangs (in local language) for an answer to a text question on the basis of cities (which was the answer to a different MCQ question).
2. A market research agency wanted to validate responses coming in against a set of business rules (eg. monthly savings cannot be more than monthly income) and send the response back to the data collector to fix it when the rules generate a flag.
3. A very common need for organizations is wanting all their data onto Google Sheets, wherein they could connect their CRM, and also generate graphs and charts offered by Sheets out of the box. In such cases, each response to the form becomes a row in the sheet, and questions in the form become columns.
4. A recent client partner wanted us to send an SMS to the customer whose details are collected in the response as soon as the ingestion was complete reliably. The content of the SMS consists of details of the customer, which were a part of the answers in the response. This customer was supposed to use this as a “receipt” for them having participated in the exercise.


We preempt that with time, more similar use cases will arise, with different “actions” being required once the response hits the primary store/database. We want to solve this problem in such a way that each new use case can just be “plugged in” and does not need an overhaul on the backend. Imagine this as a whole ecosystem for integrations. We want to optimize for latency and having a unified interface acting as a middleman.

Design a sample schematic for how you would store forms (with questions) and responses (with answers) in the data store. Forms, Questions, Responses and Answers each will have relevant metadata. Design and implement a solution for the Google Sheets use case and choose any one of the others to keep in mind how we want to solve this problem in a plug-n-play fashion. Make fair assumptions wherever necessary.

Eventual consistency is what the clients expect as an outcome of this feature, making sure no responses get missed in the journey. Do keep in mind that this solution must be failsafe, should eventually recover from circumstances like power/internet/service outages, and should scale to cases like millions of responses across hundreds of forms for an organization. 


There are points for details on how would you benchmark, set up logs, monitor for system health, and alerts for when the system health is affected for both the cloud as well as bare-metal. Read up on if there are limitations on the third party ( Google sheets in this case ) too, a good solution keeps in mind that too.


## Objective
The objective is to provide comprehensive guidance and specifications to the development team for implementing post-submission business logic features on the data collection platform. The platform should enable seamless integration and support for various real-life scenarios, as mentioned in the requirements. To ensure the platform can efficiently handle diverse customer requirements across different countries and organizations while maintaining reliability and scalability.

## Requirements

### Functional Requirements

The platform should support multiple partners (multi-tenancy) and support data collection via form response. Should support and executes rules on these collected data as defined by the different tenant and take action based on that rules.

To limit the scope of development below are the use cases:


1. One of the clients wanted to search for slangs (in local language) for an answer to a text question on the basis of cities (which was the answer to a different MCQ question).
2. A market research agency wanted to validate responses coming in against a set of business rules (eg. monthly savings cannot be more than monthly income) and send the response back to the data collector to fix it when the rules generate a flag.
3. A very common need for organizations is wanting all their data onto Google Sheets, wherein they could connect their CRM, and also generate graphs and charts offered by Sheets out of the box. In such cases, each response to the form becomes a row in the sheet, and questions in the form become columns.
4. A recent client partner wanted us to send an SMS to the customer whose details are collected in the response as soon as the ingestion was complete reliably. The content of the SMS consists of details of the customer, which were a part of the answers in the response. This customer was supposed to use this as a “receipt” for them having participated in the exercise.

### Non-functional requirements

1. The platform should be extensible i.e, any new tenant can be onboarded easily and the existing tenant can create any new rule (condition and action).
2. The platform should be highly available and scalable.
3. Eventual consistency should work.

## Assumptions
Data Sphere is the platform responsible for collecting data via the form from multiple partners and persisting the data. 

For implementation below are assumptions:
1. **RuleService**: The rule service responsible for executing rules and actions on forms will be a separate micro-service. It will receive the form details and rule information via event (Kafka announcement).
2. Partner(s) are already onboarded on the platform.
3. **SMSService**: This will have 3rd party integration (for e.g, Gupshup) for sending the SMS to the users.


## High Level Design
![DataSphere drawio](https://github.com/cef1998/DataSphere/assets/33161970/faec90be-4fcb-4029-b085-abd2513e65fd)

## Low Level Design

### Schema Design

**Tables**

1. form_responses
2. rules

### Why MongoDB (NoSQL) not Postgres (RDBMS) ?
MongoDB was chosen over a traditional relational database management system (RDBMS) for the data collection platform due to the specific requirements and characteristics of the application. 

1. **Flexibility of Schema**: Form’s can have unstructured schema and hence storing it in document form would be better as each form can have different schema.
2. **Scalability**: MongoDB is designed to scale horizontally, which means it can distribute data across multiple servers or nodes easily. With millions of responses generated from users in 50+ countries and over 250 organizations, the platform requires a scalable database solution to handle the large volume of data efficiently.
3. **Geographic Distribution**: With users in numerous countries, having the ability to deploy MongoDB instances across different regions enables reduced latency and improved data access speed for users in their respective locations.
4. **Ad hoc Queries**: MongoDB's query language and indexing capabilities allow for fast and complex ad hoc queries on the data. This is essential for implementing various post-submission business logic requirements that involve searching, validation, and other data manipulations.
5. **Ease of Replication and High Availability**: MongoDB offers built-in replication features, ensuring data redundancy and high availability in case of node failures. This feature is critical for maintaining data integrity and continuous availability of the platform for users across different time zones. Since Consistency can be compromised, not Availability, hence MongoDB would be better choice. 
 

**Note**: As per assumption, partners are already onboarded hence not considering DB choice for that (we can use RDMS for partner(s) onboarding related data).

   
