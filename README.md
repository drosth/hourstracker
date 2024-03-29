## How to...

### Start application

#### Start from command-line

```
> hourstracker$  sbt 'project rest' clean run
```

#### Start from Docker (wip)
1. Build and publish new docker image:<br/>```sbt clean docker:publishLocal``` 
1. Start new docker image:<br/>```gradle runDocker```
1. To see if the application is working, open the [swagger-ui page ((http://0.0.0.0:8080))](http://0.0.0.0:8080)
 
### Import updated registered hours

1. [Start application](#start-application)
1. Update file: `resources/import/CSVExport.csv`
1. In a browser open: <br/> [http://0.0.0.0:8080/api/v1/registrations/import](http://0.0.0.0:8080/api/v1/registrations/import)
1. or, when using the shell, execute: <br/>

| using      | execute                                                                         |
| ---------: | :------------------------------------------------------------------------------ |
| ```curl``` |  ```curl --request GET --url http://0.0.0.0:8080/api/v1/registrations/import``` |
| ```http``` |  ```http GET http://0.0.0.0:8080/api/v1/registrations/import```                 |
| ```wget``` |  ```wget http://0.0.0.0:8080/api/v1/registrations/import```                     |

### Download registered hours as .PDF file

1. [Start application](#start-application)
1. [Import updated registered hours](#import-updated-registered-hours)
1. Retrieve the so called 'consolidated registrations':
    1. In a browser open: <br/>```http://0.0.0.0:8080/api/v1/registrations/{year}/{month}/consolidated```, <br/>for instance: [http://0.0.0.0:8080/api/v1/registrations/2019/1/consolidated](http://0.0.0.0:8080/api/v1/registrations/2019/1/consolidated)
    
    1. or, when using the shell, execute: <br/>

| using      | execute                                                                                              | example                                                                                     |
| ---------: | :--------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| ```curl``` |  ```curl --request GET --url http://0.0.0.0:8080/api/v1/registrations/{year}/{month}/consolidated``` | ```curl --request GET --url http://0.0.0.0:8080/api/v1/registrations/2019/1/consolidated``` |
| ```http``` |  ```http GET http://0.0.0.0:8080/api/v1/registrations/{year}/{month}/consolidated```                 |  ```http GET http://0.0.0.0:8080/api/v1/registrations/2019/1/consolidated```                |
| ```wget``` |  ```wget http://0.0.0.0:8080/api/v1/registrations/{year}/{month}/consolidated```                     |  ```wget http://0.0.0.0:8080/api/v1/registrations/2019/1/consolidated```                    |

N.B.: when done, the location of the generated .PDF files will be shown in JSON format, for example: <br/> 
```
[
    ["/opt/resources/export/[Timesheet] - de Persgroep Online Services - DPES004 - jan-2019.pdf"]
]
```
