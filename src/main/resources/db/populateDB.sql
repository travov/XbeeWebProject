DELETE FROM PARAMETERS;
DELETE FROM LINES;

INSERT INTO lines (name, atCommand, index) VALUES
('DIO0/AD0', 'D0', 0),
('DIO1/AD1', 'D1', 1),
('DIO2/AD2', 'D2', 2),
('DIO3/AD3', 'D3', 3),
('DIO4/AD4', 'D4', 4),
('DIO5/AD5', 'D5', 5),
('DIO6', 'D6', 6),
('DIO7', 'D7', 7),
('DIO8', 'D8', 8),
('DIO9', 'D9', 9),
('DIO10/PWM0', 'P0', 10),
('DIO11/PWM1', 'P1', 11),
('DIO12', 'P2', 12),
('DIO13', 'P3', 13),
('DIO14', 'P4', 14),
('DIO15', 'P5', 15),
('DIO16', 'P6', 16),
('DIO17', 'P7', 17),
('DIO18', 'P8', 18),
('DIO19', 'P9', 19);

INSERT INTO parameters(name) VALUES
('SP'),
('SN'),
('IR'),
('IC'),
('VR');
