package org.scheez.test.schema.hr;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Employee
{
    private Long id;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String phoneNumber;
    
    private Timestamp hireDate;
    
    private BigDecimal salary;
    
    private Long managerId;
    
    private Long departmentId;
}
