# Money Transaction REST API
A simple REST API application without DI and ORM framework for money transactions between user accounts

####To run the application:
```
mvn exec:java
```
######Or:
```
mvn clean package
java -jar bank-rest-api-1.0-SNAPSHOT.jar
```

Fat Jar can be downloaded from [Here](https://drive.google.com/open?id=1ZZjBF86lubI04xiYy65AXicdwn-AtTJy)

_Please note: Server runs on port __9090__. This can be changed in __config.properties__ file with __spark.port__ property_

####To run tests:
```
mvn clean verify
```

####Technology stack:
- Lightweight API Framework [Spark](http://sparkjava.com/) 
- JOOQ for interaction with database
- Jackson
- H2 in-memory database
- ModelMapper
- REST-Assured for API testing

Database is populated with test data.

####API endpoints:
| HTTP METHOD | PATH | COMMENTS |
| -----------| ------ | ------ |
| GET | /accounts | get all accounts | 
| GET | /accounts/{id} | get account by ID | 
| POST | /accounts | create new account | 
| PUT | /accounts/{id} | update account | 
| DELETE | /accounts/{id} | delete (deactivate) account | 
| POST | /transactions | perform transaction | 
| GET | /transactions | get all transactions | 

####Sample requests/responses:

##### Get all accounts:
- Method: GET
- Endpoint: http://localhost:9090/api/v1/accounts
- Response: 
```
[
  {
    "id": 1,
    "email": "john@john.com",
    "balance": 10.1
  },
  {
    "id": 2,
    "email": "tom@tom.com",
    "balance": 20.2
  },
  {
    "id": 3,
    "email": "matt@matt.com",
    "balance": 30.3
  }
]
```

##### Create new account:
- Method: POST
- Endpoint: http://localhost:9090/api/v1/accounts
- Request: 
```
{"balance":"500","email":"foo@bar.com"}
```

##### Update account:
- Method: PUT
- Endpoint: http://localhost:9090/api/v1/accounts/1
- Request: 
```
{"balance":"500","email":"foo@bar.com"}
```

##### Create new transaction:
- Method: POST
- Endpoint: http://localhost:9090/api/v1/transactions
- Request: 
```
{"source":1, "target":2, "amount":10}
```
##### Get all transactions:
- Method: GET
- Endpoint: http://localhost:9090/api/v1/transactions
- Response: 
```
[
  {
    "id": 1,
    "source": 1,
    "target": 3,
    "amount": 10,
    "transactionTime": "2020-02-17T02:55:00.167"
  }
]
```
