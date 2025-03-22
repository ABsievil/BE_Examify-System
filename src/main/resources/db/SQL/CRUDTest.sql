-- Lấy thông tin tất cả bài test

CREATE OR REPLACE FUNCTION get_all_test_of_teacher(teacher_id TEXT)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT json_agg(row_to_json(t)) 
    INTO result
    FROM (SELECT * FROM Test WHERE TeacherID = teacher_id) AS t;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Lấy thông tin một bài test dựa trên testID

CREATE OR REPLACE FUNCTION get_test_of_teacher_by_testID(teacher_id TEXT, test_id INT)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT row_to_json(t) 
    INTO result
    FROM (SELECT * FROM Test WHERE TeacherID = teacher_id AND ID = test_id) AS t;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Tạo bài test

CREATE OR REPLACE PROCEDURE create_test(
    title_input TEXT,
    description_input TEXT,
    numberquestion_input INT,
    testtime_input INT,
    timeopen_input TIMESTAMP,
    timeclose_input TIMESTAMP,
    teacherID_input TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Test (Title, Description, NumberQuestion, TestTime, TimeOpen, TimeClose, TeacherID)
    VALUES (title_input, description_input, numberquestion_input, testtime_input, timeopen_input, timeclose_input, teacherID_input);
END;
$$;

-- Chỉnh sửa thông tin của bài test

CREATE OR REPLACE PROCEDURE edit_test(
    test_id INT,
    title_input TEXT,
    description_input TEXT,
    numberquestion_input INT,
    testtime_input INT,
    timeopen_input TIMESTAMP,
    timeclose_input TIMESTAMP,
    teacherID_input TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Test
    SET 
        Title = COALESCE(title_input, Title),
        Description = COALESCE(description_input, Description),
        NumberQuestion = COALESCE(numberquestion_input, NumberQuestion),
        TestTime = COALESCE(testtime_input, TestTime),
        TimeOpen = COALESCE(timeopen_input, TimeOpen),
        TimeClose = COALESCE(timeclose_input, TimeClose),
        TeacherID = COALESCE(teacherID_input, TeacherID)
    WHERE ID = test_id;
    
    IF NOT FOUND THEN
        RAISE NOTICE 'No test found with ID %', test_id;
    END IF;
END;
$$;

-- Xóa bài test

CREATE OR REPLACE PROCEDURE delete_test(test_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM Test
    WHERE ID = test_id;
    
    IF NOT FOUND THEN
        RAISE NOTICE 'No test found with ID %', test_id;
    ELSE
        RAISE NOTICE 'Test ID % deleted successfully', test_id;
    END IF;
END;
$$;