INSERT INTO blog_user (id,name,surname,username) VALUES (1,'Ali','Veli','aliveli');
insert into blog_user (id, name, surname) values (2, 'Wini', 'Ledwitch');
insert into blog_user (id, name, surname) values (3, 'Sigismundo', 'Haddow');
insert into blog_user (id, name, surname) values (4, 'Lotty', 'Zuan');
insert into blog_user (id, name, surname) values (5, 'Claiborn', 'Stappard');
insert into blog_user (id, name, surname) values (6, 'Erma', 'Straughan');
insert into blog_user (id, name, surname) values (7, 'Siffre', 'Couper');
insert into blog_user (id, name, surname) values (8, 'Tomasina', 'Brownscombe');
insert into blog_user (id, name, surname) values (9, 'Jacky', 'Englefield');
insert into blog_user (id, name, surname) values (10, 'Obidiah', 'Wooddisse');
insert into blog_user (id, name, surname) values (11, 'Felike', 'Birkinshaw');


INSERT INTO Blog(id,title,body, created_by) VALUES (1,'Blogpost1','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut al',1);
insert into blog (id, title, body, created_by) values (2, 'etiam', 'Nulla facilisi. Cras non velit nec nisi vulputate nonummy. Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque. Quisque porta volutpat erat.', 2);
insert into blog (id, title, body, created_by) values (3, 'faucibus', 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. .', 3);
insert into blog (id, title, body, created_by) values (4, 'condimentum', 'Suspendisse potenti. Cras in purus eu magna vulputate luctus.', 4);
insert into blog (id, title, body, created_by) values (5, 'felis', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', 5);
insert into blog (id, title, body, created_by) values (6, 'pulvinar', 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat. In costo.', 6);
insert into blog (id, title, body, created_by) values (7, 'nisl', 'Duis bibendum. Morbi non quam nec dui luctus rutrum. Nulla tellus. In sagittis dui vel nisl. Duis ac nibh.', 7);
insert into blog (id, title, body, created_by) values (8, 'nisl', 'Maecenas rhoncus aliquam lacus. Morbi quis tortor id nulla ultrices aliquet. Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo. Pellentesque viverra pede ac diam. Cras pelle.', 8);
insert into blog (id, title, body, created_by) values (9, 'congue', 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Ves', 9);
insert into blog (id, title, body, created_by) values (10, 'orci', 'Aenean sit amet justo. Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', 10);
insert into blog (id, title, body, created_by) values (11, 'quis', 'Phasellus in felis. Donec semper sapien a libero. Nam dui. Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis.', 11);

INSERT INTO BLOG_TAG(id, tag) VALUES (1, 'First Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (2, 'Second Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (3, 'Third Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (4, 'Forth Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (5, 'Fifth Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (6, 'Sixth Tag');
INSERT INTO BLOG_TAG(id, tag) VALUES (7, 'Seventh Tag');

INSERT INTO BLOG_TAG_MM(blog_id, blog_tag_id) VALUES (1, 1);

select VALUES NEXT VALUE FOR blog_seq;
select VALUES NEXT VALUE FOR blog_user_seq;
select VALUES NEXT VALUE FOR blog_tag_seq;

ALTER SEQUENCE blog_seq RESTART WITH 12;
ALTER SEQUENCE blog_user_seq RESTART WITH 12;
ALTER SEQUENCE blog_tag_seq RESTART WITH 8;