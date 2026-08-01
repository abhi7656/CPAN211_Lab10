DROP TABLE IF EXISTS n01754524_Orders;

CREATE TABLE n01754524_Orders (
    Customer VARCHAR(50),
    Product VARCHAR(50),
    Price INT
);

INSERT INTO n01754524_Orders (Customer, Product, Price) VALUES
('Washington', 'Dress', 119),
('Adams', 'Shirt', 55),
('Adams', 'Tie', 22),
('Washington', 'Blouse', 75),
('Franklin', 'Hat', 33),
('Livingston', 'Gloves', 19),
('Livingston', 'Pants', 89),
('Read', 'Dress', 99);
