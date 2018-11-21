# shift-service
API to manage shifts.

This API assumes it is dedicated to one workplace, with managers and employees. Each manager is assumed to have access to the info of each employee or user. 

This API also allows for external users to get quick info from the api (via the `/app` endpoint)

**There is additional application documentation if you open the index.html file located in the `documents` directory.**

## How to Run

### Hosted Endpoint
This app is hosted at: https://evening-savannah-83499.herokuapp.com

### Docker
From the root directory of the project, use Docker to build and deploy the application image in a containerized Java/Tomcat environment.
The application is exposed on port 8080

### Execute Jar
- Install Java 8+
- Install JRE
- Install Gradle 4+

From the root of the project, run:
```batch
./gradlew clean build
```

```batch
java -jar build/libs/shift-service-0.1.0.jar
```


## Getting Access
Aside from the default endpoint (which returns a default message) in order to access the api, you will need to register as either a Manager, Employee, or User.
### Roles
#### Manager
Role: ROLE_MANAGER
- Managers can access any application endpoint. 
- Managers can access `/app` endpoints
- Managers can access `/user` endpoints to see all users that have registered
- Managers can access `/shifts` endpoints and do the following for **any** user's shift: search, get, create, update, and delete shifts (others and own)

#### Employee
Role: ROLE_EMPLOYEE
- Employees can access most application endpoints
- Employees can access `/app` endpoints
- Employers can access `/shifts` endpoints and do the following for their **own** shifts: search and get

#### User
Role: ROLE_USER
- Users can access the `/app` endpoints for application information

### Register
To register, create a `POST` to the `/register` endpoint with a username, password, and list of role(s) you would like to obtain.

**You will not be able to register a username that already exists**

### Default Users
By Default, there are 3 users already loaded into the database upon startup. You may skip registration and login with these users to test the app.

#### Manager
- Username: manager
- Password: manager
- Role: ROLE_MANAGER

#### Employee
- Username: employee
- Password: employee
- Role: ROLE_EMPLOYEE

#### User
- Username: user
- Password: user
- Role: ROLE_USER

### JWT (JSON Web Token) Token Authentication
After registering, you will be able to "login" (with username and passowrd) by `POST` to the `/auth/token/generate-token` endpoint and a JWT will be returned.

For each request to the API, include an `Authorization` header with the value of `Bearer <your-generated-jwt>` 

### H2 database
This app includes **embedded** relational database for user/role and shift management.

** Warning: Data is not permanently persisted. Whenever the app is restarted, all saved data is erased and defaul data is reloaded.

You can access the H2 DB UI at: `{host}/h2`
