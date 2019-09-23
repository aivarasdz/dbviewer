# Database viewer

Database viewer is REST API application, running on embedded tomcat and using in memory H2 database for data storage.    
Currently supports PostgreSQL database as a target of information extraction.    
Using endpoints, specified in V1 part, information used to connect to database is added, updated, deleted and queried.    
Those connection details are later used in V2 and Bonus specified endpoints to get information on database.

## V1
- CRUD operations for database connection details   
Response is returned in JSON format for all endpoints, where response is expected.    

| Endpoint | Request method | Response | Response body              | Description
| ---      | ---            | ---      | ---                        | ---
| /db      | PUT            | 201      |                            | Create new connection details
| /db      | POST           | 200      | Updated connection details | Update existing connection details
| /db      | GET            | 200      | Connection detail list     | Get list of all connection details
| /db/{id} | GET            | 200      | Connection details by id   | Get connection details by id
| /db/{id} | DELETE         | 204      |                            | Delete connection details by id

Example request/response json body.    

```json
{
  "id": 1,
  "name": "test details",
  "host": "localhost",
  "port": 5432,
  "databaseName": "testDB",
  "username": "testUser",
  "password": "password"
}
```

## V2
- Endpoints for database information retrieval (GET request method)    

Request params:
- *schema*: name of the targeted schema
- *table*:  name of the targeted table
- *offset*: row offset, default is 0
- *limit*:  row limit of query, default is 100

path variable *{id}* should be replaced with connection details id.
    
Endpoint list:    

| Endpoint         | request params| description                      | 
| ---              | ---                                              | ---                                     
| /db/{id}/schemas |                                                  | list of schemas for connection database
| /db/{id}/tables  | schema                                           | tables of schema
| /db/{id}/columns | schema, table                                    | columns of table
| /db/{id}/rows    | schema, table, offset(optional), limit(optional) | rows in table

## Bonus    

- Endpoints for table/column statistics    

| Endpoint                    | request params        | description
| ---                         | ---                   | ---
| /db/{id}/statistics/tables  | schema, table         | list of schemas for connection database
| /db/{id}/statistics/columns | schema, table, column | tables of schema
