print('Start DB init');

db = db.getSiblingDB('prod_db');
db.createUser(
    {
        user: 'prod_user',
        pwd: 'prod_pass',
        roles: [{ role: 'readWrite', db: 'prod_db' }],
    },
);
db.createCollection('users');

db = db.getSiblingDB('dev_db');
db.createUser(
    {
        user: 'dev_user',
        pwd: 'dev_pass',
        roles: [{ role: 'readWrite', db: 'dev_db' }],
    },
);
db.createCollection('users');

db = db.getSiblingDB('test_db');
db.createUser(
    {
        user: 'test_user',
        pwd: 'test_pass',
        roles: [{ role: 'readWrite', db: 'test_db' }],
    },
);
db.createCollection('users');

print('End DB init');