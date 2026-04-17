## ADDED Requirements

### Requirement: Watermark job CRUD operations
The system SHALL allow users to create, read, update, and delete watermark jobs.

#### Scenario: Create watermark job
- **WHEN** user fills in job name, selects type (embed/extract), factors, source, and target, then clicks "Create"
- **THEN** system creates job with PENDING status and returns success message

#### Scenario: List watermark jobs
- **WHEN** user navigates to job management page
- **THEN** system displays paginated list of jobs with name, type, status, execute time, and completion time

#### Scenario: Delete watermark job
- **WHEN** user clicks "Delete" on a job
- **THEN** system performs logical deletion and removes from list

### Requirement: Job type support
The system SHALL support both watermark embedding and extraction jobs.

#### Scenario: Create embed job
- **WHEN** user selects job type as EMBED
- **THEN** system creates job to embed watermark into target data

#### Scenario: Create extract job
- **WHEN** user selects job type as EXTRACT
- **THEN** system creates job to extract watermark from source data

### Requirement: Job execution
The system SHALL support executing watermark jobs manually.

#### Scenario: Execute pending job
- **WHEN** user clicks "Execute" on a PENDING job
- **THEN** job status changes to RUNNING, and system starts processing

#### Scenario: Job completion
- **WHEN** job processing finishes successfully
- **THEN** job status changes to COMPLETED and completion time is recorded

#### Scenario: Job failure
- **WHEN** job processing fails
- **THEN** job status changes to FAILED and error message is recorded

### Requirement: Job status tracking
The system SHALL track and display job execution status.

#### Scenario: View job details
- **WHEN** user clicks on a job
- **THEN** system displays job details including type, factors, source, target, and execution result

### Requirement: Scheduled job execution
The system SHALL support scheduled job execution using cron expressions.

#### Scenario: Enable scheduled execution
- **WHEN** user enables "Scheduled" toggle and enters a valid cron expression
- **THEN** system saves the schedule and job will execute automatically at specified times

#### Scenario: Disable scheduled execution
- **WHEN** user disables "Scheduled" toggle
- **THEN** system clears the cron expression and job will only execute manually

#### Scenario: Async execution
- **WHEN** user clicks "Execute" on a job
- **THEN** system returns HTTP 200 immediately and processes job in background
