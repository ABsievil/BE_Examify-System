-- CREATE OR REPLACE FUNCTION get_studentAnswer(student_id INT, question_id INT)
-- RETURNS JSON AS $$
-- DECLARE
--     result JSON;
-- BEGIN
--     SELECT row_to_json(q) 
--     INTO result
--     FROM (SELECT * FROM Result WHERE StudentID = student_id AND TestID = test_id) AS q;

--     RETURN result;
-- END;
-- $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE create_studentAnswer(student_id INT, question_id INT, isCorrect_input BOOLEAN) 
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO StudentAnswer(StudentID, QuestionID, isCorrect)
    VALUES (student_id, question_id, isCorrect_input);
END;
$$;

CREATE OR REPLACE PROCEDURE update_studentAnswer(student_id INT, question_id INT, isCorrect_input BOOLEAN) 
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE StudentAnswer
    SET
        isCorrect = isCorrect_input
    WHERE StudentID = student_id AND QuestionID = question_id;
END;
$$;