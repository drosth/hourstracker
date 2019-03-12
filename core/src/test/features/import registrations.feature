Feature: Import registrations
  As an User
  I want to import registrations
  So that I can manage my registrations

  Background:
    Given a CSV file named 'import.csv' with the following registrations:
      | Job           | Clocked In       | Clocked Out      | Duration | Hourly Rate | Earnings | Comment      | Tags                        | Breaks                 | Adjustments | TotalTimeAdjustment | TotalEarningsAdjustment |
      | Some Job name | 05/09/2018 07:53 | 05/09/2018 16:41 | 8,5      | 76,5        | 650,25   | Some comment | POS;DPES005-50%;DPES004-50% | 0,33h (12:00 to 12:20) |             | -0,33               | 0                       |

  Scenario: Import single registration
    When I import the registrations from file 'import.csv'
    Then my registrations must contain:
      | Job           | Clocked In       | Clocked Out      | Duration | Hourly Rate | Earnings | Comment       | Tags                          | TotalTimeAdjustment | TotalEarningsAdjustment |
      | Some Job name | 2018-09-05 07:53 | 2018-09-05 16:41 | 8,5      | 76,5        | 650,25   | Some comment  | POS, DPES005-50%, DPES004-50% | -0,33               | 0                       |
