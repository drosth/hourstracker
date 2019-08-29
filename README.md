## How to...

### Download registered hours as .PDF file

1. start the REST API, by executing: <br/>```gradle runDocker```
1. to import new registrations, execute: <br/>```http GET http://0.0.0.0:9080/api/v1/registrations/import```
1. to retrieve consolidated registrations execute: 
  <br/>```http GET http://0.0.0.0:9080/api/v1/registrations/{year}/{month}/consolidated```, <br/>for instance: 
  <br/>```http GET http://0.0.0.0:9080/api/v1/registrations/2019/1/consolidated```
