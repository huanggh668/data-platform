## 1. Database Schema

- [ ] 1.1 Create desensitization service database schema (data_source, rule, job tables)
- [ ] 1.2 Create watermark service database schema (factor, job, trace_record tables)
- [ ] 1.3 Create encryption service database schema (algorithm, job tables)

## 2. Desensitization Service - Data Source Management

### Backend
- [ ] 2.1 Create DataSource entity class
- [ ] 2.2 Create DataSourceMapper interface
- [ ] 2.3 Create DataSourceService class
- [ ] 2.4 Create DataSourceController with CRUD endpoints
- [ ] 2.5 Create DataSourceRequest/Response DTOs

### API Endpoints
- [ ] 2.6 GET /api/v1/desensitization/data-sources (list)
- [ ] 2.7 GET /api/v1/desensitization/data-sources/{id} (detail)
- [ ] 2.8 POST /api/v1/desensitization/data-sources (create)
- [ ] 2.9 PUT /api/v1/desensitization/data-sources/{id} (update)
- [ ] 2.10 DELETE /api/v1/desensitization/data-sources/{id} (delete)

## 3. Desensitization Service - Rule Management

### Backend
- [ ] 3.1 Create DesensitizationRule entity class
- [ ] 3.2 Create DesensitizationRuleMapper interface
- [ ] 3.3 Create DesensitizationRuleService class
- [ ] 3.4 Create DesensitizationRuleController with CRUD endpoints
- [ ] 3.5 Create DesensitizationRuleRequest/Response DTOs

### API Endpoints
- [ ] 3.6 GET /api/v1/desensitization/rules (list)
- [ ] 3.7 GET /api/v1/desensitization/rules/{id} (detail)
- [ ] 3.8 POST /api/v1/desensitization/rules (create)
- [ ] 3.9 PUT /api/v1/desensitization/rules/{id} (update)
- [ ] 3.10 DELETE /api/v1/desensitization/rules/{id} (delete)

## 4. Desensitization Service - Job Management

### Backend
- [ ] 4.1 Create DesensitizationJob entity class (with cron_expression, is_scheduled fields)
- [ ] 4.2 Create DesensitizationJobMapper interface
- [ ] 4.3 Create DesensitizationJobService with async execution logic
- [ ] 4.4 Create DesensitizationJobController with CRUD and execute endpoints
- [ ] 4.5 Create DesensitizationJobRequest/Response DTOs
- [ ] 4.6 Configure async executor and scheduled task runner

### API Endpoints
- [ ] 4.7 GET /api/v1/desensitization/jobs (list)
- [ ] 4.8 GET /api/v1/desensitization/jobs/{id} (detail)
- [ ] 4.9 POST /api/v1/desensitization/jobs (create)
- [ ] 4.10 DELETE /api/v1/desensitization/jobs/{id} (delete)
- [ ] 4.11 POST /api/v1/desensitization/jobs/{id}/execute (execute job)

## 5. Watermark Service - Factor Management

### Backend
- [ ] 5.1 Create WatermarkFactor entity class
- [ ] 5.2 Create WatermarkFactorMapper interface
- [ ] 5.3 Create WatermarkFactorService class
- [ ] 5.4 Create WatermarkFactorController with CRUD endpoints
- [ ] 5.5 Create WatermarkFactorRequest/Response DTOs

### API Endpoints
- [ ] 5.6 GET /api/v1/watermark/factors (list)
- [ ] 5.7 GET /api/v1/watermark/factors/{id} (detail)
- [ ] 5.8 POST /api/v1/watermark/factors (create)
- [ ] 5.9 PUT /api/v1/watermark/factors/{id} (update)
- [ ] 5.10 DELETE /api/v1/watermark/factors/{id} (delete)

## 6. Watermark Service - Job Management

### Backend
- [ ] 6.1 Create WatermarkJob entity class (with cron_expression, is_scheduled fields)
- [ ] 6.2 Create WatermarkJobMapper interface
- [ ] 6.3 Create WatermarkJobService with async execution logic
- [ ] 6.4 Create WatermarkJobController with CRUD and execute endpoints
- [ ] 6.5 Create WatermarkJobRequest/Response DTOs
- [ ] 6.6 Configure async executor and scheduled task runner

### API Endpoints
- [ ] 6.7 GET /api/v1/watermark/jobs (list)
- [ ] 6.8 GET /api/v1/watermark/jobs/{id} (detail)
- [ ] 6.9 POST /api/v1/watermark/jobs (create)
- [ ] 6.10 DELETE /api/v1/watermark/jobs/{id} (delete)
- [ ] 6.11 POST /api/v1/watermark/jobs/{id}/execute (execute job)

## 7. Watermark Service - Traceability Management

### Backend
- [ ] 7.1 Create WatermarkTraceRecord entity class
- [ ] 7.2 Create WatermarkTraceRecordMapper interface
- [ ] 7.3 Create WatermarkTraceRecordService class
- [ ] 7.4 Create WatermarkTraceRecordController with list and search endpoints
- [ ] 7.5 Create WatermarkTraceRecordRequest/Response DTOs

### API Endpoints
- [ ] 7.6 GET /api/v1/watermark/trace-records (list with search)
- [ ] 7.7 GET /api/v1/watermark/trace-records/{id} (detail)

## 8. Encryption Service - Algorithm Management

### Backend
- [ ] 8.1 Create EncryptionAlgorithm entity class
- [ ] 8.2 Create EncryptionAlgorithmMapper interface
- [ ] 8.3 Create EncryptionAlgorithmService class
- [ ] 8.4 Create EncryptionAlgorithmController with CRUD endpoints
- [ ] 8.5 Create EncryptionAlgorithmRequest/Response DTOs

### API Endpoints
- [ ] 8.6 GET /api/v1/encryption/algorithms (list)
- [ ] 8.7 GET /api/v1/encryption/algorithms/{id} (detail)
- [ ] 8.8 POST /api/v1/encryption/algorithms (create)
- [ ] 8.9 PUT /api/v1/encryption/algorithms/{id} (update)
- [ ] 8.10 DELETE /api/v1/encryption/algorithms/{id} (delete)

## 9. Encryption Service - Job Management

### Backend
- [ ] 9.1 Create EncryptionJob entity class (with cron_expression, is_scheduled fields)
- [ ] 9.2 Create EncryptionJobMapper interface
- [ ] 9.3 Create EncryptionJobService with async execution logic
- [ ] 9.4 Create EncryptionJobController with CRUD and execute endpoints
- [ ] 9.5 Create EncryptionJobRequest/Response DTOs
- [ ] 9.6 Configure async executor and scheduled task runner

### API Endpoints
- [ ] 9.7 GET /api/v1/encryption/jobs (list)
- [ ] 9.8 GET /api/v1/encryption/jobs/{id} (detail)
- [ ] 9.9 POST /api/v1/encryption/jobs (create)
- [ ] 9.10 DELETE /api/v1/encryption/jobs/{id} (delete)
- [ ] 9.11 POST /api/v1/encryption/jobs/{id}/execute (execute job)

## 10. Frontend - API Layer

- [ ] 10.1 Create admin-ui/src/api/desensitization.js with data source, rule, job APIs
- [ ] 10.2 Create admin-ui/src/api/watermark.js with factor, job, trace APIs
- [ ] 10.3 Create admin-ui/src/api/encryption.js with algorithm, job APIs

## 11. Frontend - Desensitization Pages

- [ ] 11.1 Create admin-ui/src/views/desensitization/DataSource.vue page
- [ ] 11.2 Create admin-ui/src/views/desensitization/RuleManagement.vue page
- [ ] 11.3 Create admin-ui/src/views/desensitization/JobManagement.vue page

## 12. Frontend - Watermark Pages

- [ ] 12.1 Create admin-ui/src/views/watermark/FactorManagement.vue page
- [ ] 12.2 Create admin-ui/src/views/watermark/JobManagement.vue page
- [ ] 12.3 Create admin-ui/src/views/watermark/Traceability.vue page

## 13. Frontend - Encryption Pages

- [ ] 13.1 Create admin-ui/src/views/encryption/AlgorithmManagement.vue page
- [ ] 13.2 Create admin-ui/src/views/encryption/JobManagement.vue page

## 14. Frontend - Router Configuration

- [ ] 14.1 Add desensitization sub-routes to router
- [ ] 14.2 Add watermark sub-routes to router
- [ ] 14.3 Add encryption sub-routes to router
- [ ] 14.4 Add navigation menu items for new pages

## 15. Build and Test

- [ ] 15.1 Run maven build for all services
- [ ] 15.2 Run npm build for admin-ui
- [ ] 15.3 Verify all API endpoints work correctly
- [ ] 15.4 Verify all frontend pages render correctly
