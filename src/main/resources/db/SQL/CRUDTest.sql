-- Lấy thông tin tất cả bài test

CREATE OR REPLACE FUNCTION get_all_test_of_teacher(teacher_id INT)
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

-- SELECT get_all_test_of_teacher(1);

-- Lấy thông tin một bài test dựa trên testID

CREATE OR REPLACE FUNCTION get_test_of_teacher_by_testID(teacher_id INT, test_id INT)
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

-- SELECT get_test_of_teacher_by_testID(1, 1);

-- Tạo bài test

CREATE OR REPLACE PROCEDURE create_test(
    title_input TEXT,
    description_input TEXT,
    testtime_input INT,
    timeopen_input TIMESTAMP,
    timeclose_input TIMESTAMP,
    teacherID_input INT,
    numberquestion_input INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Test (Title, Description, TestTime, TimeOpen, TimeClose, TeacherID, NumberQuestion)
    VALUES (title_input, description_input, testtime_input, timeopen_input, timeclose_input, teacherID_input, numberquestion_input);
END;
$$;

-- CALL create_test('Bài kiểm tra Toán', 'Đề kiểm tra học kỳ môn Toán', 60, '2025-04-01 08:00:00', '2025-04-01 10:00:00', 1, 10);

-- convert to func for this procedure:
CREATE OR REPLACE FUNCTION add_test(
    title_input TEXT,
    description_input TEXT,
    testtime_input INT,
    timeopen_input TIMESTAMP,
    timeclose_input TIMESTAMP,
    teacherID_input INT,
    numberquestion_input INT
)
RETURNS INT AS $$
DECLARE
    new_id INT;
    passcode_text TEXT;
    attempt INT := 0;
    max_attempts INT := 100;
BEGIN
    -- Sinh passcode ngẫu nhiên và kiểm tra tính duy nhất
    LOOP
        -- Tạo chuỗi 6 chữ số ngẫu nhiên (000000 - 999999)
        passcode_text := LPAD(FLOOR(RANDOM() * 1000000)::TEXT, 6, '0');
        
        -- Kiểm tra xem passcode đã tồn tại trong bảng Test chưa
        IF NOT EXISTS (SELECT 1 FROM Test WHERE Passcode = passcode_text) THEN
            EXIT; -- Thoát vòng lặp nếu passcode chưa tồn tại
        END IF;
        
        -- Tăng số lần thử và kiểm tra giới hạn
        attempt := attempt + 1;
        IF attempt >= max_attempts THEN
            RAISE EXCEPTION 'Không thể sinh passcode duy nhất sau % lần thử', max_attempts;
        END IF;
    END LOOP;
    
    -- Chèn dữ liệu vào bảng Test với passcode đã sinh
    INSERT INTO Test (Title, Description, Passcode, TestTime, TimeOpen, TimeClose, TeacherID, NumberQuestion)
    VALUES (title_input, description_input, passcode_text, testtime_input, timeopen_input, timeclose_input, teacherID_input, numberquestion_input)
    RETURNING ID INTO new_id;
    
    RETURN new_id;
END;
$$ LANGUAGE plpgsql;
-- SELECT add_test('Bài kiểm tra Toán', 'Đề kiểm tra học kỳ môn Toán', '111111', 60, '2025-04-01 08:00:00', '2025-04-01 10:00:00', 1, 10);


-- Chỉnh sửa thông tin của bài test

CREATE OR REPLACE PROCEDURE edit_test(
    test_id INT,
    title_input TEXT,
    description_input TEXT,
    numberquestion_input INT,
    testtime_input INT,
    timeopen_input TIMESTAMP,
    timeclose_input TIMESTAMP,
    teacherID_input INT
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

-- CALL edit_test(1, null, null, 15, 75, '2025-05-01 08:00:00', '2025-05-01 09:15:00', 1);

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

-- CALL delete_test(5)

