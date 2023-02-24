INSERT INTO blog_user (id,name,surname,username) VALUES (1,'Ali','Veli','aliveli');
INSERT INTO Blog(id,title,body, created_by) VALUES (1,'Blogpost1','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut al',1);
INSERT INTO BLOG_TAG(id, tag) VALUES (1, 'First Tag');
INSERT INTO BLOG_TAG_MM(blog_id, blog_tag_id) VALUES (1, 1);

select VALUES NEXT VALUE FOR blog_seq;
select VALUES NEXT VALUE FOR blog_user_seq;
select VALUES NEXT VALUE FOR blog_tag_seq;
