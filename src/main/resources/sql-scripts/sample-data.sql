CREATE TABLE games (
  id INT AUTO_INCREMENT PRIMARY KEY,
  state VARCHAR(50) NOT NULL,
  players VARCHAR(100),
);

insert into games (state, players) values
    ('PLAYING', 'dog,cat'),
    ('OPEN', 'bird'),
    ('OPEN', NULL);

