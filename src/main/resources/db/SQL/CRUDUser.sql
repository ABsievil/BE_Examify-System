CREATE OR REPLACE FUNCTION create_user_and_account(username_input TEXT, password_input TEXT, role_input TEXT) 
RETURNS VOID AS $$
DECLARE
    user_id INT;
BEGIN
    INSERT INTO Users (Name, Email, Dob)
    VALUES (null, null, null)
    RETURNING ID INTO user_id;

    INSERT INTO Account (Username, Password, Role, UserID)
    VALUES (username_input, password_input, role_input, user_id);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE delete_user(user_id_input INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM Users WHERE user_id = user_id_input;
END;
$$;

CREATE OR REPLACE PROCEDURE edit_user_infor(user_id INT, name_input TEXT, email_input TEXT, dob_input DATE)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Users
    SET 
        name = COALESCE(name_input, name),
        email = COALESCE(email_input, email),
        dob = COALESCE(dob_input, dob)
    WHERE ID = user_id;
END;
$$;

CREATE OR REPLACE PROCEDURE change_password(username_input TEXT, oldpassword TEXT, newpassword TEXT)
LANGUAGE plpgsql
AS $$
DECLARE
    stored_password TEXT;
BEGIN
    SELECT password INTO stored_password
    FROM Account
    WHERE username = username_input;

    IF oldpassword = stored_password THEN
        UPDATE Account
        SET 
            password = newpassword
        WHERE username = username_input;
        RAISE NOTICE 'Password updated successfully for user %', username_input;
    ELSE
        RAISE EXCEPTION 'Old password incorrect for user %', username_input;
    END IF;
END;
$$;

