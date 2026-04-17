## ADDED Requirements

### Requirement: Desensitization rule CRUD operations
The system SHALL allow users to create, read, update, and delete desensitization rules.

#### Scenario: Create desensitization rule
- **WHEN** user fills in rule name, data type, pattern, replacement, and priority, then clicks "Create"
- **THEN** system creates the rule and returns success message

#### Scenario: List desensitization rules
- **WHEN** user navigates to rule management page
- **THEN** system displays paginated list of rules with name, data type, pattern, and enabled status

#### Scenario: Update desensitization rule
- **WHEN** user edits rule configuration and clicks "Save"
- **THEN** system updates the rule and returns success message

#### Scenario: Delete desensitization rule
- **WHEN** user clicks "Delete" on a rule
- **THEN** system performs logical deletion and removes from active list

### Requirement: Rule pattern matching
The system SHALL support regex-based pattern matching for identifying sensitive data.

#### Scenario: Configure regex pattern
- **WHEN** user enters a valid regex pattern
- **THEN** system validates and stores the pattern for matching

### Requirement: Rule priority
The system SHALL support priority ordering for rules applied to the same data type.

#### Scenario: Set rule priority
- **WHEN** user assigns priority value to rules
- **THEN** system applies rules in priority order (lower number = higher priority)

### Requirement: Rule enable/disable
The system SHALL allow enabling or disabling rules without deletion.

#### Scenario: Disable rule
- **WHEN** user toggles rule status to disabled
- **THEN** system excludes rule from job execution

#### Scenario: Enable rule
- **WHEN** user toggles rule status to enabled
- **THEN** system includes rule in job execution
