## ADDED Requirements

### Requirement: Data source CRUD operations
The system SHALL allow users to create, read, update, and delete data sources for desensitization.

#### Scenario: Create data source
- **WHEN** user fills in data source name, type, and configuration, then clicks "Create"
- **THEN** system creates the data source and returns success message

#### Scenario: List data sources
- **WHEN** user navigates to data source management page
- **THEN** system displays paginated list of data sources with name, type, and status

#### Scenario: Update data source
- **WHEN** user edits data source configuration and clicks "Save"
- **THEN** system updates the data source and returns success message

#### Scenario: Delete data source
- **WHEN** user clicks "Delete" on a data source
- **THEN** system performs logical deletion and removes from active list

### Requirement: Data source types
The system SHALL support multiple data source types including DATABASE and FILE.

#### Scenario: Configure database source
- **WHEN** user selects type as DATABASE
- **THEN** system shows database-specific fields: host, port, database, username, password

#### Scenario: Configure file source
- **WHEN** user selects type as FILE
- **THEN** system shows file-specific fields: path, file type (CSV, JSON, XML)

### Requirement: Data source validation
The system SHALL validate data source configuration before saving.

#### Scenario: Validate required fields
- **WHEN** user attempts to create data source without required fields
- **THEN** system displays validation error message
