# Database viewer

Designed for PostgreSQL database.    
Application database for data storage - in memory H2 database.    

# V1
- CRUD operations for database details connections    
Response is returned in JSON format for all endpoints, where response is expected.    

| Endpoint | Request method | Response | Response body              | Description                        |
| -------- | -------------- | -------- | -------------------------- | ---------------------------------- |
| /db      | PUT            | 201      |                            | Create new connection details      |
| /db      | POST           | 200      | Updated connection details | Update existing connection details |
| /db      | GET            | 200      | Connection detail list     | Get list of all connection details |
| /db/{id} | GET            | 200      | Connection details by id   | Get connection details by id       |
| /db/{id} | DELETE         | 204      |                            | Delete connection details by id    |

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
