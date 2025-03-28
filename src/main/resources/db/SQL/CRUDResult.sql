-- PASS
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

-- select get_all_student_result(1)
-- PASS
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

-- select get_result_by_student_id(1, 1)
CREATE OR REPLACE PROCEDURE create_result(student_id INT, test_id INT, start_time TIMESTAMP) 
LANGUAGE plpgsql AS $$
DECLARE
    test_time INT;
BEGIN
    SELECT testTime INTO test_time FROM Test WHERE ID = test_id;

    INSERT INTO Result(StudentID, TestID, TotalScore, StartTime, EndTime)
    VALUES (student_id, test_id, 0, start_time, start_time + (test_time * interval '1 minute'));

END;
$$;

CREATE OR REPLACE PROCEDURE update_result(student_id INT, test_id INT, end_time TIMESTAMP) 
LANGUAGE plpgsql
AS $$
DECLARE
    score_of_answer FLOAT;
    score_of_question FLOAT;
    total_score FLOAT := 0;
BEGIN

    SELECT COALESCE(SUM(q.score), 0) INTO score_of_answer
    FROM question q, studentanswer sa
    WHERE sa.StudentID = student_id AND sa.TestID = test_id AND q.ID = sa.questionID AND sa.isCorrect = true; 

    SELECT COALESCE(SUM(score), 0) INTO score_of_question
    FROM question q
    WHERE q.TestID = test_id;

    IF score_of_question > 0 THEN
        total_score := score_of_answer / score_of_question;
    ELSE
        total_score := 0;
    END IF;

    UPDATE Result 
    SET
        TotalScore = COALESCE(total_score, TotalScore),
        EndTime = COALESCE(end_time, EndTime)
    WHERE StudentID = student_id AND TestID = test_id;
END;
$$;
