CREATE OR REPLACE FUNCTION get_studentAnswer(student_id INT, question_id INT)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT row_to_json(q) 
    INTO result
    FROM (SELECT * FROM studentAnswer WHERE StudentID = student_id AND QuestionID = question_id) AS q;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- select get_studentAnswer(1, 1);

CREATE OR REPLACE PROCEDURE create_studentAnswer(student_id INT, question_id INT) 
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO StudentAnswer(StudentID, QuestionID)
    VALUES (student_id, question_id);
END;
$$;

-- call create_studentAnswer(1, 2);

CREATE OR REPLACE PROCEDURE update_studentAnswer(student_id INT, question_id INT, isCorrect_input BOOLEAN, answer_id INT) 
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE StudentAnswer
    SET
        isCorrect = isCorrect_input,
        answerID = answer_id
    WHERE StudentID = student_id AND QuestionID = question_id;
END;
$$;

-- call update_studentAnswer(1, 2, true, 1);