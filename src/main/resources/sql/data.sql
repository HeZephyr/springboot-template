-- 插入用户表数据
INSERT INTO user (userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole, createTime, updateTime, isDelete)
VALUES
    ('user1', 'password1', 'unionId1', 'mpOpenId1', 'User One', 'https://example.com/avatar1.jpg', 'This is User One.', 'user', NOW(), NOW(), 0),
    ('user2', 'password2', 'unionId2', 'mpOpenId2', 'User Two', 'https://example.com/avatar2.jpg', 'This is User Two.', 'user', NOW(), NOW(), 0),
    ('admin', 'adminpass', NULL, NULL, 'Admin User', 'https://example.com/admin.jpg', 'This is Admin User.', 'admin', NOW(), NOW(), 0);

-- 插入帖子表数据
INSERT INTO post (title, content, tags, thumbNum, favourNum, userId, createTime, updateTime, isDelete)
VALUES
    ('First Post', 'This is the content of the first post.', '["tag1", "tag2"]', 5, 2, 1, NOW(), NOW(), 0),
    ('Second Post', 'This is the content of the second post.', '["tag2", "tag3"]', 3, 1, 2, NOW(), NOW(), 0),
    ('Admin Post', 'This post was created by an admin.', '["admin", "important"]', 10, 5, 3, NOW(), NOW(), 0);

-- 插入帖子点赞表数据
INSERT INTO post_thumb (postId, userId, createTime, updateTime)
VALUES
    (1, 1, NOW(), NOW()),
    (1, 2, NOW(), NOW()),
    (2, 1, NOW(), NOW());

-- 插入帖子收藏表数据
INSERT INTO post_favour (postId, userId, createTime, updateTime)
VALUES
    (1, 1, NOW(), NOW()),
    (2, 2, NOW(), NOW()),
    (3, 3, NOW(), NOW());