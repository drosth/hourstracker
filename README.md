## How to...

### Download tracked hours as .PDF file

- execute command ```sbt 'project rest' run```
- open browser with address: ```http://0.0.0.0:8080/api/v1/registrations/consolidated?startAt=<startAt> [ &endAt=<endAt>]```
where endAt is optional

| Parameter | Required | Description | 
| --- | --- | --- |
| **startAt** | mandatory | by weeknumber: ```wk99```, or: ```w99```, or: ```W99```<br/> by month: ```sep```, or ```september``` <br/> by date: ```yyyy-MM-dd``` | 
| **endAt**   | optional  | by weeknumber: ```wk99```, or: ```w99```, or: ```W99```<br/> by month: ```sep```, or ```september``` <br/> by date: ```yyyy-MM-dd``` | 
