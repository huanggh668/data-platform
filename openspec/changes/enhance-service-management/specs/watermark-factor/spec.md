## ADDED Requirements

### Requirement: Watermark factor CRUD operations
The system SHALL allow users to create, read, update, and delete watermark factors.

#### Scenario: Create watermark factor
- **WHEN** user fills in factor name, type, and value, then clicks "Create"
- **THEN** system creates the factor and returns success message

#### Scenario: List watermark factors
- **WHEN** user navigates to factor management page
- **THEN** system displays paginated list of factors with name, type, and value preview

#### Scenario: Update watermark factor
- **WHEN** user edits factor configuration and clicks "Save"
- **THEN** system updates the factor and returns success message

#### Scenario: Delete watermark factor
- **WHEN** user clicks "Delete" on a factor
- **THEN** system performs logical deletion and removes from active list

### Requirement: Factor types
The system SHALL support multiple watermark factor types.

#### Scenario: User ID factor
- **WHEN** user selects type as USER_ID
- **THEN** system stores user identifier for embedding

#### Scenario: Timestamp factor
- **WHEN** user selects type as TIMESTAMP
- **THEN** system generates timestamp during watermark embedding

#### Scenario: Business code factor
- **WHEN** user selects type as BUSINESS_CODE
- **THEN** system stores business identifier for embedding

### Requirement: Factor value validation
The system SHALL validate factor values based on type requirements.

#### Scenario: Validate required value
- **WHEN** user attempts to create factor without required value
- **THEN** system displays validation error message
