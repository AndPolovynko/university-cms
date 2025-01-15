insert into roles (id, name) values ('admin', 'ADMINISTRATOR');
insert into roles (id, name) values ('teacher', 'TEACHER');
insert into roles (id, name) values ('student', 'STUDENT');

insert into courses (id, name, description) values ('course-id-1', 'Course Example 1', 'Example of a course.');
insert into courses (id, name, description) values ('course-id-2', 'Course Example 2', 'Example of a course.');

insert into groups (id, name) values ('group-id', 'Group Example');

-- Admin password - 'admin'
insert into users (id, login, password, email) 
	values ('initial-admin-id', 'admin', '{bcrypt}$2a$12$l.3UrnYofL8rrHTW9QFD6eJurdjKWuBEcvaBqNTl.z.dlC2FB5qmO', '-');
-- Student password - 'student'
insert into users (id, login, password, email) 
	values ('student-example-id', 'student', '{bcrypt}$2a$12$vLw3aiq1u3MMOTWbFFFCXOGga/qKA93k1QPvL9PKU0hjVsqioc0YS', '-');
-- Teacher password - 'teacher'
insert into users (id, login, password, email) 
	values ('teacher-example-id', 'teacher', '{bcrypt}$2a$12$iUQ2Prs1jVPs3r9ks1WXQuPLZ25k.cffNHBWBibOGESgOQjts.gNK', '-');

insert into user_details (user_type, id, user_id, first_name, last_name, job_title)
	values ('admin', 'initial-admin-details-id', 'initial-admin-id', 'Erik', 'the Red', 'Administrator');
insert into user_details (user_type, id, user_id, first_name, last_name, group_id)
	values ('student', 'student-example-details-id', 'student-example-id', 'Student', 'Studentson', 'group-id');
insert into user_details (user_type, id, user_id, first_name, last_name)
	values ('teacher', 'teacher-example-details-id', 'teacher-example-id', 'Teach', 'Teachers');

insert into users_roles (user_id, role_id) values ('initial-admin-id', 'admin');
insert into users_roles (user_id, role_id) values ('student-example-id', 'student');
insert into users_roles (user_id, role_id) values ('teacher-example-id', 'teacher');

insert into university_class_types (id, name) values ('type-id', 'Class Type Example');

insert into university_class_venues (id, name, description) 
	values ('venue-id', 'Class Venue Example', 'Example of a venue.');

insert into university_classes (id, type_id, venue_id, course_id, date_and_time) 
	values ('class-id', 'type-id', 'venue-id', 'course-id-1', '2025-01-05 14:30:00+05:30');

insert into university_classes_groups (university_class_id, group_id) values ('class-id', 'group-id');

insert into university_classes_users (university_class_id, user_id) values ('class-id', 'teacher-example-id');
