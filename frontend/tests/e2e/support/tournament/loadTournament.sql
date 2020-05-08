INSERT INTO tournaments (available_date, conclusion_date, creation_date, number_questions, title, course_execution_id, user_id, status) VALUES ('2019-03-28 22:20', '2021-03-29 22:20', '2029-03-27 22:20', 5, 'Title1', 11, 647, 'CAN_NOT_GENERATE_QUIZ');

INSERT INTO topics_tournaments (topics_id, tournaments_id) VALUES (108, (SELECT id FROM tournaments WHERE tournaments.number_questions = 5));

INSERT INTO users_enrolled_tournaments (enrolled_students_id, enrolled_tournaments_id) VALUES (647, (SELECT id FROM tournaments WHERE tournaments.number_questions = 5));

INSERT INTO users_created_tournaments (user_id, created_tournaments_id) VALUES (647, (SELECT id FROM tournaments WHERE tournaments.number_questions = 5));
