# Univesity Schedule Web Application

## Description
This application supports three user roles: students, teachers, and administrators.

Students are assigned to specific groups. Each group has a list of required courses.

Teachers have a list of courses they teach.

Administrators manage user accounts and create university classes.

University Classes can be of various types (laboratory work, practical lessons, lectures, etc.) and are associated with specific courses. Each class specifies the number of attending groups and teachers required. Classes are scheduled at specific times and locations, which can be physical classrooms or online conference links. The system ensures no two classes overlap in the same location.

The application generates schedules on request for specific student groups or teachers, ensuring conflict-free timetables.
![Alt text](uml-diagram.svg)

## Visuals

## Usage
 **Given the user is logged on as Administrator:**
- **Admin can navigate to the Course Management menu**
- **Admin can navigate to the course creation page**
- *Admin can create a new course by specifying the name and description*
- **Admin can find existing course by name**
- **Admin can navigate to the page of a specific course**
- *Admin can see name and description of existing course*
- *Admin can delete existing course*
- **Admin can navigate to a specific course edition page**
- *Admin can edit name and description of existing course*   
------------------------------------------------------------------------------------------------
- **Admin can navigate to the Group Management menu**
- **Admin can navigate to the group creation page**
- *Admin can create a new group by specifying the name*
- **Admin can find existing group by name**
- **Admin can navigate to the page of a specific group**
- *Admin can see name of existing group*
- *Admin can delete existing group*
- **Admin can navigate to a specific group edition page**
- *Admin can edit name of existing group*   
------------------------------------------------------------------------------------------------
- **Admin can navigate to User Management menu**
- **Admin can navigate to the user creation page**
- *Admin can create a new user by specifying login, password, email, set of roles (Student, Teacher, Administrator), first name and last name. Admin can whether specify or not a group for the new student or a position for the new administrator*
- **Admin can find existing user by login or lastname**
- **Admin can navigate to the page of a specific user**
- *Admin can see login, password, email, set of roles, first and last names, group and job title of existing user*
- *Admin can delete existing user*
- **Admin can navigate to a specific user edition page**
- *Admin can edit login, password, email, set of roles, first and last names, group and job title of existing user*   
------------------------------------------------------------------------------------------------
- **Admin can navigate to Class Management menu**   
-------------------------------------------
- **Admin can navigate to the Class Type Management page**
- **Admin see navigate to the class type creation page**
- *Admin can create a new class type by specifying the name*
- **Admin can find existing class type by name**
- **Admin can navigate to the page of a specific class type**
- *Admin can see name of existing class type*
- *Admin can delete existing class type*
- **Admin can navigate to a specific class type edition page**
- *Admin can edit name of existing class type*   
-------------------------------------------
- **Admin can navigate to the Class Venue Management page**
- **Admin can navigate to the class venue creation page**
- *Admin can create a new class venue by specifying the name and description*
- **Admin can find existing class venue by name**
- **Admin can navigate to the page of a specific class venue**
- *Admin can see name and description of existing class venue*
- *Admin can delete existing class venue*
- **Admin can navigate to a specific class venue edition page**
- *Admin can edit name and description of existing class venue*  
-------------------------------------------
- **Admin see navigate to the class creation page**
- *User can create a new class by specifying the type, venue, associated course, groups, teachers, time, and date*
- **Admin can find existing class by date and time**
- **Admin can navigate to the page of a specific class**
- *Admin can see all inforamtion about existing class*
- *Admin can delete existing class venue*   
   


**Given the user is logged on as Teacher:**
- **Teacher can see and navigate to My Schedule menu**
- *Teacher should see their own schedule according to the selected date/range filter.*   

   

**Given the user is logged on as Student:**
- **Student can see and navigate to My Group Schedule menu.**
- *Student should see the schedule for their assigned group according to the selected date/range filter.*	