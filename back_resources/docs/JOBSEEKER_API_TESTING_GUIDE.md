# HaloKazi Jobseeker Profile - API Testing Guide & Database Inspection Report

> **Target Service**: HaloKazi Jobseeker Profile Backend  
> **Base URL**: `http://localhost:8080`  
> **Target Framework**: Spring Boot 4.1.2 / Java 21  
> **Database Engine**: Oracle Free (23ai), Service: `FREEPDB1`  
> **Postman Collection**: [`halokazi_jobseeker_postman_collection.json`](file:///home/kalicode/Downloads/halo_kazi_complete/back_resources/docs/halokazi_jobseeker_postman_collection.json)  

---

## 1. Spring Boot Database Configuration Inspection

### 1.1 Configuration Files Inspected
The database configuration was inspected across three files:
1. **[`application.properties`](file:///home/kalicode/Downloads/halo_kazi_complete/back_resources/backend/src/main/resources/application.properties)**
2. **Environment File (`.env`)** in the backend root directory (secured, not committed in git)
3. **[`pom.xml`](file:///home/kalicode/Downloads/halo_kazi_complete/back_resources/backend/pom.xml)**

### 1.2 Remote Connection Settings Identified
| Configuration Property | Value / Setting | Description |
| :--- | :--- | :--- |
| **Database Dialect** | Oracle (Version 23.26.3 / Oracle 23ai Free) | Relational enterprise database |
| **Remote Host** | `161.97.99.40` | Remote Contabo server hosted by team |
| **Listener Port** | `1521` | Standard Oracle TNS listener port (verified open) |
| **Service Name** | `FREEPDB1` | Pluggable database (PDB) |
| **Database User** | `halokazi` (schema: `HALOKAZI`) | Team application schema owner |
| **JDBC Driver** | `com.oracle.database.jdbc:ojdbc11` | Oracle Thin JDBC driver (JDK 21 compatible) |
| **JDBC Driver Class**| `oracle.jdbc.OracleDriver` | Standard Oracle driver implementation |
| **Connection Pool** | HikariCP (`maximum-pool-size=5`) | Efficient connection pooling with 30s timeout |
| **DDL Policy** | `spring.jpa.hibernate.ddl-auto=validate` | **Read-only validation**: Hibernate validates that tables match entities without creating, altering, or dropping anything |
| **SQL Logging** | `spring.jpa.show-sql=true` | Queries are logged to console for debugging |

> [!NOTE]
> All credentials and connection secrets are loaded via environment variables (`${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`) from `.env`. The database password is never hardcoded or exposed in logs.

---

## 2. Remote Database Connectivity Status

### 2.1 Connection Verification Evidence
The connection to the remote Oracle database **was tested and confirmed working**:
1. **Network Layer**: TCP port `1521` on `161.97.99.40` is reachable.
2. **Hikari Connection Pool**: Connected successfully on startup:
   ```text
   INFO --- [halokazi-backend] [main] com.zaxxer.hikari.pool.HikariPool : HikariPool-1 - Added connection oracle.jdbc.driver.T4CConnection@...
   INFO --- [halokazi-backend] [main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Start completed.
   ```
3. **Hibernate Schema Validation**: Hibernate connected to `HALOKAZI` schema and validated that all 10 entity tables exist in Oracle:
   - `job_seeker_profile`
   - `education`
   - `work_experience`
   - `profile_skill`
   - `certification`
   - `profile_language`
   - `profile_project`
   - `profile_reference`
   - `saved_vacancy`
   - `application`
4. **Live Query Execution**: Calling `GET /api/db-test` executes `SELECT 1 FROM DUAL` directly against Oracle and returns:
   ```json
   {"status":"connected"}
   ```

---

## 3. Architecture Deep-Dive: In-Memory DataStore vs Oracle Persistence

> [!IMPORTANT]
> **Crucial Finding Regarding Persistence**:  
> While the **connection to the remote Oracle database is 100% active and tested**, the existing Jobseeker Profile CRUD endpoints currently write to an **in-memory data store (`JobSeekerDataStore`)**, not yet to the Oracle tables.

### 3.1 How the Current Code Is Structured
1. **JPA Entities Exist**: Classes in `tz.co.halotel.halokazi.jobseeker.entity.*` map to the 10 Oracle tables.
2. **Services Call In-Memory Store**: `JobSeekerProfileService`, `EducationService`, `WorkExperienceService`, etc., currently inject `JobSeekerDataStore`.
3. **DataStore**: `JobSeekerDataStore` uses thread-safe Java `ConcurrentHashMap` instances pre-seeded with sample data (e.g. John Mwangi, Profile ID 1).
4. **Impact**:
   - Calling `POST /api/v1/jobseeker/education` adds an education record to the in-memory map.
   - The API immediately returns HTTP 200 with the newly created record.
   - **However, that record is not inserted into Oracle's `education` table**.
   - If the application is stopped and restarted, newly added records revert to the default seed data.

---

## 4. How to Verify Whether Records Are in the Remote Database

To verify whether any record was actually saved in the remote Oracle database versus the local in-memory store:

### Method A: Direct SQL Query on Oracle Database
Connect to the shared Oracle database using SQL*Plus, SQL Developer, DBeaver, or IntelliJ Database Tool:
```sql
-- Check total records in job_seeker_profile
SELECT COUNT(*) FROM job_seeker_profile;

-- Check if any education records were written
SELECT education_id, profile_id, institution_name, qualification, created_at 
FROM education 
ORDER BY created_at DESC;

-- Check applications
SELECT application_id, application_ref, status, submitted_at 
FROM application;
```
*If a table returns 0 rows after submitting an API request, the data was saved in the in-memory `JobSeekerDataStore` and has not yet been synced to Oracle.*

### Method B: Server Restart Test
1. Make a POST request (e.g., add a new education record).
2. Fetch the education list via `GET /api/v1/jobseeker/education` — notice the new record is present.
3. Restart the backend application (`Ctrl+C` and `./run-backend.sh`).
4. Fetch the education list again. If the newly added record disappears and only default seed data remains, the endpoint is using in-memory storage.

---

## 5. Security & Validation Configuration

- **Security**: No Spring Security filter chain is active (`spring-boot-starter-security` is omitted). All endpoints are publicly accessible REST endpoints.
- **CORS**: Configured in `CorsConfig.java` with `.allowedOriginPatterns("*")`, `.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")`, and `.allowCredentials(true)`.
- **Validation**:
  - DTOs use Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@Pattern`, `@NotNull`).
  - Validation failures trigger `MethodArgumentNotValidException`, which `GlobalExceptionHandler` converts to a clean `400 Bad Request` containing field-level error messages.

---

## 6. Complete Jobseeker Profile API Reference

### 6.0 System Health & Database Test

#### `GET /api/db-test`
- **Description**: Verifies live connection to remote Oracle database.
- **Expected Success (200 OK)**:
  ```json
  {
    "status": "connected"
  }
  ```
- **Error Response (500 Internal Server Error)**:
  ```json
  {
    "status": "error",
    "message": "IO Error: The Network Adapter could not establish the connection"
  }
  ```

#### `GET /actuator/health`
- **Expected Success (200 OK)**:
  ```json
  {
    "status": "UP",
    "groups": ["liveness", "readiness"]
  }
  ```

---

### 6.1 Profile Management (`/api/v1/jobseeker/profile`)

#### 1. Get Current Profile
- **Method & URL**: `GET /api/v1/jobseeker/profile`
- **Expected Success (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Operation successful",
    "data": {
      "profileId": 1,
      "userId": 101,
      "firstName": "John",
      "lastName": "Mwangi",
      "headline": "Digital Marketing Specialist | Halotel Tanzania",
      "currentLocation": "Dar es Salaam, Tanzania",
      "completionPercentage": 80.0
    }
  }
  ```

#### 2. Get Profile Composite Detail
- **Method & URL**: `GET /api/v1/jobseeker/profile/detail`
- **Expected Success (200 OK)**:
  Returns full composite payload: profile details, strength score, list of educations, work experiences, skills, certifications, languages, projects, references, alert preferences, dashboard stats, and recommended jobs.

#### 3. Update Personal Details
- **Method & URL**: `PUT /api/v1/jobseeker/profile/personal-details`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "firstName": "John",
    "lastName": "Mwangi",
    "dateOfBirth": "1995-08-15",
    "gender": "Male",
    "nationality": "Tanzanian",
    "currentLocation": "Dar es Salaam, Tanzania",
    "regionId": 1,
    "regionName": "Dar es Salaam",
    "districtId": 101,
    "districtName": "Ilala",
    "addressLine": "Samora Avenue, Posta Mpya",
    "contactEmail": "john.mwangi@example.com",
    "phone": "+255 712 345 678",
    "alternatePhone": "+255 765 987 321"
  }
  ```
- **Validation Rules**: `firstName` and `lastName` are required (`@NotBlank`).
- **Error Case (400 Bad Request)** when `firstName` is omitted:
  ```json
  {
    "success": false,
    "message": "Validation failed",
    "data": {
      "firstName": "First name is required"
    }
  }
  ```

#### 4. Update Headline & Summary
- **Method & URL**: `PATCH /api/v1/jobseeker/profile/headline-summary`
- **Request Body**:
  ```json
  {
    "headline": "Senior Full Stack Software Engineer",
    "professionalSummary": "Experienced developer specializing in Spring Boot and Oracle database integrations."
  }
  ```
- **Validation Rules**: `headline` max length 150 chars.

#### 5. Update Profile Visibility
- **Method & URL**: `PATCH /api/v1/jobseeker/profile/visibility`
- **Request Body**:
  ```json
  {
    "visibility": "Public"
  }
  ```
- **Validation Rules**: Must match `Public`, `Private`, `PUBLIC`, or `PRIVATE`.

#### 6. Update Profile Picture
- **Method & URL**: `PATCH /api/v1/jobseeker/profile/picture`
- **Request Body**:
  ```json
  {
    "pictureUrl": "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
  }
  ```

#### 7. Get Profile Strength
- **Method & URL**: `GET /api/v1/jobseeker/profile/strength`
- **Expected Success (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Operation successful",
    "data": {
      "overallPercentage": 80.0,
      "statusLabel": "Good",
      "sections": [
        {"sectionName": "Personal Details", "percentage": 100.0, "completed": true},
        {"sectionName": "Headline & Summary", "percentage": 100.0, "completed": true},
        {"sectionName": "Work Experience", "percentage": 100.0, "completed": true},
        {"sectionName": "Education", "percentage": 100.0, "completed": true},
        {"sectionName": "Skills", "percentage": 100.0, "completed": true}
      ],
      "missingRecommendations": [
        "Add at least one professional project to boost profile visibility to 90%"
      ]
    }
  }
  ```

#### 8. Get Dashboard Stats
- **Method & URL**: `GET /api/v1/jobseeker/profile/stats`
- **Expected Success (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Operation successful",
    "data": {
      "appliedJobsCount": 3,
      "savedJobsCount": 2,
      "activeAlertsCount": 1,
      "cvCount": 1
    }
  }
  ```

#### 9. Get Recommendations
- **Method & URL**: `GET /api/v1/jobseeker/profile/recommendations`

---

### 6.2 Education (`/api/v1/jobseeker/education`)

| Method | Endpoint | Query Param | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/education` | `?profileId=1` (optional) | List education qualifications |
| `POST` | `/api/v1/jobseeker/education` | `?profileId=1` (optional) | Create new education record |
| `PUT` | `/api/v1/jobseeker/education/{id}` | `?profileId=1` (optional) | Update education record by ID |
| `DELETE` | `/api/v1/jobseeker/education/{id}` | `?profileId=1` (optional) | Delete education record by ID |

**Sample POST / PUT Body**:
```json
{
  "institutionName": "University of Dar es Salaam",
  "qualification": "Bachelor of Science in Computer Science",
  "educationLevelId": 4,
  "educationLevelName": "Bachelor Degree",
  "fieldOfStudy": "Computer Science",
  "grade": "First Class Honours",
  "startDate": "2019-10-01",
  "endDate": "2022-11-30",
  "current": false
}
```

---

### 6.3 Work Experience (`/api/v1/jobseeker/work-experience`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/work-experience` | List work experience records |
| `POST` | `/api/v1/jobseeker/work-experience` | Add new work experience |
| `PUT` | `/api/v1/jobseeker/work-experience/{id}` | Update experience record by ID |
| `DELETE` | `/api/v1/jobseeker/work-experience/{id}` | Delete experience record by ID |

**Sample POST / PUT Body**:
```json
{
  "companyName": "Halotel Tanzania PLC",
  "jobTitle": "Full Stack Software Engineer",
  "employmentTypeId": 1,
  "employmentTypeName": "Full-time",
  "location": "Dar es Salaam, Tanzania",
  "description": "Led development of core telecom customer portal.",
  "startDate": "2023-01-15",
  "endDate": null,
  "current": true
}
```

---

### 6.4 Skills (`/api/v1/jobseeker/skills`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/skills` | List skills on profile |
| `POST` | `/api/v1/jobseeker/skills` | Add skill |
| `DELETE` | `/api/v1/jobseeker/skills/{skillId}` | Delete skill by skillId |

**Sample POST Body**:
```json
{
  "skillId": 205,
  "skillName": "Java & Spring Boot",
  "proficiency": "EXPERT",
  "yearsExperience": 5.5
}
```

---

### 6.5 Certifications (`/api/v1/jobseeker/certifications`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/certifications` | List certifications |
| `POST` | `/api/v1/jobseeker/certifications` | Add certification |
| `PUT` | `/api/v1/jobseeker/certifications/{id}` | Update certification |
| `DELETE` | `/api/v1/jobseeker/certifications/{id}` | Delete certification |

**Sample POST / PUT Body**:
```json
{
  "certificationName": "Oracle Certified Professional: Java SE 21 Developer",
  "issuingOrganization": "Oracle University",
  "credentialId": "OCP-JAVA-21-99882",
  "issueDate": "2024-03-15",
  "expiryDate": "2027-03-15",
  "certificateUrl": "https://catalog-education.oracle.com"
}
```

---

### 6.6 Languages (`/api/v1/jobseeker/languages`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/languages` | List spoken languages |
| `POST` | `/api/v1/jobseeker/languages` | Add language |
| `DELETE` | `/api/v1/jobseeker/languages/{id}` | Remove language |

**Sample POST Body**:
```json
{
  "languageId": 2,
  "languageName": "Swahili",
  "proficiency": "NATIVE"
}
```

---

### 6.7 Projects (`/api/v1/jobseeker/projects`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/projects` | List portfolio projects |
| `POST` | `/api/v1/jobseeker/projects` | Add project |
| `DELETE` | `/api/v1/jobseeker/projects/{id}` | Delete project |

**Sample POST Body**:
```json
{
  "title": "Halo Kazi Recruitment Engine",
  "role": "Lead Architect",
  "description": "High-throughput matching platform for jobseekers and employers.",
  "projectUrl": "https://github.com/halotel/halokazi",
  "startDate": "2024-01-01",
  "endDate": "2024-06-30",
  "sortOrder": 1
}
```

---

### 6.8 References (`/api/v1/jobseeker/references`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/references` | List references |
| `POST` | `/api/v1/jobseeker/references` | Add reference |
| `DELETE` | `/api/v1/jobseeker/references/{id}` | Delete reference |

**Sample POST Body**:
```json
{
  "fullName": "Dr. Joseph K. Mrema",
  "jobTitle": "Head of Computer Science",
  "organization": "University of Dar es Salaam",
  "relationship": "Academic Advisor",
  "email": "mrema@udsm.ac.tz",
  "phone": "+255 754 112 233",
  "sortOrder": 1
}
```

---

### 6.9 Saved Jobs (`/api/v1/jobseeker/saved-jobs`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/saved-jobs` | List bookmarked jobs |
| `POST` | `/api/v1/jobseeker/saved-jobs` | Bookmark vacancy |
| `DELETE` | `/api/v1/jobseeker/saved-jobs/{vacancyId}` | Remove bookmark |

**Sample POST Body**:
```json
{
  "vacancyId": 105,
  "jobTitle": "Cloud Infrastructure Engineer",
  "companyName": "Halotel Tanzania",
  "location": "Dar es Salaam",
  "employmentType": "Full-time",
  "salaryRange": "TZS 3,500,000 - 5,000,000",
  "deadline": "2026-12-31"
}
```

---

### 6.10 Applications (`/api/v1/jobseeker/applications`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/applications` | List submitted applications |
| `GET` | `/api/v1/jobseeker/applications/{id}` | Get application by ID |
| `POST` | `/api/v1/jobseeker/applications` | Submit application |

**Sample POST Body**:
```json
{
  "vacancyId": 102,
  "cvId": 1,
  "coverLetter": "Dear Hiring Manager, I am eager to apply for the Senior Engineer position.",
  "requirementsConfirmed": true
}
```

---

### 6.11 Job Alerts (`/api/v1/jobseeker/alerts`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/jobseeker/alerts` | Get alert preferences |
| `PUT` | `/api/v1/jobseeker/alerts` | Update preferences |
| `PATCH` | `/api/v1/jobseeker/alerts/toggle` | Toggle alerts ON / OFF |

**Sample PUT Body**:
```json
{
  "enabled": true,
  "receiveEmailAlerts": true,
  "receiveSmsAlerts": true,
  "frequency": "INSTANT",
  "keywords": ["Java", "Spring Boot", "Cloud Architect"],
  "preferredLocations": ["Dar es Salaam", "Remote"],
  "preferredCategories": ["Technology & Telecommunications"],
  "minSalary": 3000000
}
```

---

## 7. How to Import and Use the Postman Collection

1. Open **Postman**.
2. Click **Import** (top left).
3. Drag and drop or browse to:
   ```
   ~/Downloads/halo_kazi_complete/back_resources/docs/halokazi_jobseeker_postman_collection.json
   ```
4. The collection will appear as **"HaloKazi - Jobseeker Profile API"** with 12 organized folders.
5. In Postman, the collection uses variables:
   - `{{baseUrl}}` default is `http://localhost:8080`
   - `{{profileId}}` default is `1`
6. Run the requests individually or use **Collection Runner** to execute the entire suite sequentially.
