INSERT IGNORE INTO role VALUES (UNHEX('5F4C7646CC69418B87CDF13F17B4925F'), 'ROLE_ADMIN');
INSERT IGNORE INTO role VALUES (UNHEX('5F4C7646CC69418B34CDF13F17C4925F'), 'ROLE_SELLER');
INSERT IGNORE INTO role VALUES (UNHEX('5F4C7646CC99418B87CDF13F17B9025E'), 'ROLE_CUSTOMER');

INSERT IGNORE INTO user (
    invalid_attempt_count,
    is_active,
    is_deleted,
    is_expired,
    is_locked,
    password_update_date,
    activation_token_created_time,
    role,
    user_id,
    email,
    first_name,
    last_name,
    middle_name,
    password,
    created_at,
    updated_at,
    created_by,
    updated_by
) VALUES (
    0,
    b'1',
    b'0',
    b'0',
    b'0',
    CURDATE(),
    NOW(6),
    UNHEX('5F4C7646CC69418B87CDF13F17B4925F'),
    UNHEX('123E4567E89B12D3A456426655440000'),
    'aryan.kohli@tothenew.com',
    'Admin',
    'User',
    NULL,
    '$2a$10$X6dyu03LjBghNjs/Ilm.nOQv/ev2n0ukHqpuKlm/8x.9rA9PcvM7e', --> password is Admin@123
    CURRENT_TIME(),
    CURRENT_TIME(),
    "system",
    "system"
);
