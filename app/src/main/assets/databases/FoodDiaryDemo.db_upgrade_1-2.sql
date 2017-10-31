CREATE TABLE IF NOT EXISTS "Categories" (
    "_id" INTEGER PRIMARY KEY NOT NULL,
    "Name" TEXT NOT NULL,
    "Color" TEXT NOT NULL
);

INSERT INTO Categories (
                           Color,
                           Name
                       )
                       VALUES (
                           '#9E9E9E',
                           'Other'
                       ),
					   (
                           '#CFD8DC',
                           'Dairy'
                       ),
                       (
                           '#795548',
                           'Carb'
                       ),
                       (
                           '#F44336',
                           'Protein'
                       ),
                       (
                           '#4CAF50',
                           'Vegetable'
                       ),
                       (
                           '#FF9800',
                           'Fruit'
                       ),
                       (
                           '#FFEB3B',
                           'Beverage'
                       ),
                       (
                           '#E91E63',
                           'Dessert'
                       );
