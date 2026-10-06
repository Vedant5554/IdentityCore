INSERT INTO roles (id, name) VALUES
                                 ('11111111-1111-1111-1111-111111111111', 'RIDER'),
                                 ('22222222-2222-2222-2222-222222222222', 'DRIVER'),
                                 ('33333333-3333-3333-3333-333333333333', 'ADMIN');

INSERT INTO permissions (id, name) VALUES
                                       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ride:create'),
                                       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ride:cancel'),
                                       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'user:manage'),
                                       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'ride:view');

INSERT INTO role_permissions (role_id, permission_id) VALUES
                                                          ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), -- RIDER: ride:create
                                                          ('11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), -- RIDER: ride:cancel
                                                          ('11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd'), -- RIDER: ride:view
                                                          ('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), -- DRIVER: ride:cancel
                                                          ('22222222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd'), -- DRIVER: ride:view
                                                          ('33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc'), -- ADMIN: user:manage
                                                          ('33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), -- ADMIN: ride:create
                                                          ('33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'), -- ADMIN: ride:cancel
                                                          ('33333333-3333-3333-3333-333333333333', 'dddddddd-dddd-dddd-dddd-dddddddddddd'); -- ADMIN: ride:view