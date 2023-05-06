INSERT INTO public.users (name,email) VALUES
                                            ('updateName','updateName@user.com'),
                                            ('user2','user2@user.com'),
                                            ('user3','user3@user.com'),
                                            ('user','user@user.com'),
                                            ('other','other@other.com'),
                                            ('practicum','practicum@yandex.ru'),
                                            ('user to delete','delete@mail.ru');

INSERT INTO public.items (name,description,is_available,owner_id) VALUES
                                                                        ('Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1),
                                                                        ('Отвертка','Аккумуляторная отвертка',true,4),
                                                                        ('Клей Момент','Тюбик суперклея марки Момент',true,4),
                                                                        ('Кухонный стол','Стол для празднования',true,6),
                                                                        ('Айтем для удаления','Удаляем его',true,1);


INSERT INTO public.bookings (start_time,end_time,item_id,booker_id,status) VALUES
                                                                               ('2023-04-30 20:52:03','2023-04-30 20:52:04',2,1,'WAITING'),
                                                                               ('2023-05-01 20:52:00','2023-05-02 20:52:00',2,1,'APPROVED'),
                                                                               ('2023-05-01 20:52:00','2023-05-01 21:52:00',1,4,'REJECTED'),
                                                                               ('2023-04-30 21:52:00','2023-04-30 22:52:00',2,5,'APPROVED'),
                                                                               ('2023-04-30 20:52:07','2023-05-01 20:52:04',3,1,'REJECTED'),
                                                                               ('2023-04-30 20:52:07','2023-04-30 20:52:08',2,1,'APPROVED'),
                                                                               ('2023-04-30 20:52:17','2023-04-30 21:52:15',4,1,'APPROVED'),
                                                                               ('2023-05-10 20:52:15','2023-05-11 20:52:15',1,5,'APPROVED');

INSERT INTO public.comments (text,item_id,author_id,created) VALUES
    ('Add comment from user1',2,1,'2023-04-30 20:52:21.85272');

INSERT INTO public.requests (description, created, author_id) VALUES
    ('Request from user1', '2023-04-30 20:52:21.85272', 1);

