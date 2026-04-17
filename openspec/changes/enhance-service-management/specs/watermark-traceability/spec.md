## ADDED Requirements

### Requirement: Watermark trace record management
The system SHALL record and display watermark embedding history for traceability.

#### Scenario: Create trace record
- **WHEN** watermark job completes embedding
- **THEN** system creates trace record with job ID, factor ID, and embedded data

#### Scenario: List trace records
- **WHEN** user navigates to traceability page
- **THEN** system displays paginated list of trace records with job info, factor info, and timestamp

#### Scenario: Search trace records
- **WHEN** user enters search criteria (job ID, date range)
- **THEN** system returns matching trace records

### Requirement: Traceability search
The system SHALL support searching trace records for data leak investigation.

#### Scenario: Search by job ID
- **WHEN** user enters job ID in search box
- **THEN** system returns trace records for that job

#### Scenario: Search by date range
- **WHEN** user selects date range
- **THEN** system returns trace records within that period

### Requirement: Trace detail view
The system SHALL display detailed trace information for investigation.

#### Scenario: View trace details
- **WHEN** user clicks on a trace record
- **THEN** system displays full details including all embedded factor values and metadata
