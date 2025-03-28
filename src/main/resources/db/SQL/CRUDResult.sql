CREATE OR REPLACE FUNCTION get_all_student_result(test_id INT)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT row_to_json(q) 
    INTO result
    FROM (SELECT * FROM Result WHERE TestID = test_id) AS q;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_result_by_student_id(student_id INT, test_id INT)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT row_to_json(q) 
    INTO result
    FROM (SELECT * FROM Result WHERE StudentID = student_id AND TestID = test_id) AS q;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE create_result(student_id INT, test_id INT, start_time TIMESTAMP, end_time TIMESTAMP) 
LANGUAGE plpgsql
AS $$
DECLARE
    total_score FLOAT := 0;
BEGIN
    SELECT SUM(score) INTO total_score
    FROM question q, studentanswer sa
    WHERE sa.StudentID = student_id AND sa.TestID = test_id AND q.ID = sa.questionID AND sa.isCorrect = true; 

    INSERT INTO Result(StudentID, TestID, TotalScore, StartTime, EndTime)
    VALUES (student_id, test_id, total_score, start_time, end_time);
END;
$$;