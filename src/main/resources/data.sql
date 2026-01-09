-- =================================================================
-- SCRIPT FINAL - LOGIQUE CODE APOGÉE (SQL SERVER)
-- Mot de passe : 12345
-- =================================================================

-- 1. INSERTION DES RÔLES
INSERT INTO roles (nom) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT * FROM roles WHERE nom = 'ADMIN');
INSERT INTO roles (nom) SELECT 'ENSAMIEN' WHERE NOT EXISTS (SELECT * FROM roles WHERE nom = 'ENSAMIEN');
INSERT INTO roles (nom) SELECT 'EXTERNE' WHERE NOT EXISTS (SELECT * FROM roles WHERE nom = 'EXTERNE');
INSERT INTO roles (nom) SELECT 'PRESIDENT' WHERE NOT EXISTS (SELECT * FROM roles WHERE nom = 'PRESIDENT');

-- 2. REMPLISSAGE DE LA LISTE OFFICIELLE (Table 'ensamiens')
-- Cette table sert de référence. Si un user a ce code, il est ENSAMIEN.
INSERT INTO ensamiens (code_apogee, nom, prenom, filiere, niveau) VALUES
                                                                      ('2023001', 'Alami', 'Ahmed', 'IA', '4A'),
                                                                      ('2023002', 'Bennani', 'Sarah', 'G-INFO', '3A'),
                                                                      ('2023003', 'Chraibi', 'Youssef', 'G-MECA', '5A'),
                                                                      ('2023004', 'Daoudi', 'Hiba', 'G-INDUS', '4A'),
                                                                      ('2023005', 'El Fassi', 'Omar', 'G-CIVIL', '3A');

-- 3. CRÉATION DES COMPTES DE CONNEXION (Table 'utilisateurs')
-- Mdp Hashé pour "12345" : $2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.

-- A. L'ADMINISTRATEUR (Pas de code apogée -> NULL)
INSERT INTO utilisateurs (prenom, nom, email, mot_de_passe, code_apogee)
VALUES ('Super', 'Admin', 'admin@ensam.eu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', NULL);

-- B. LES ENSAMIENS (On met le code apogée pour faire le lien)
INSERT INTO utilisateurs (prenom, nom, email, mot_de_passe, code_apogee) VALUES
                                                                             ('Ahmed', 'Alami', 'ahmed.alami@ensam.eu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', '2023001'),
                                                                             ('Sarah', 'Bennani', 'sarah.bennani@ensam.eu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', '2023002'),
                                                                             ('Youssef', 'Chraibi', 'youssef.chraibi@ensam.eu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', '2023003'),
                                                                             ('Hiba', 'Daoudi', 'hiba.daoudi@ensam.eu', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', '2023004');

-- C. LES EXTERNES (Code Apogée est NULL)
INSERT INTO utilisateurs (prenom, nom, email, mot_de_passe, code_apogee) VALUES
                                                                             ('Jean', 'Dupont', 'jean.dupont@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', NULL),
                                                                             ('Marie', 'Curie', 'marie.curie@yahoo.fr', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4TEQnRjAnperCGwM9q.8.', NULL);

-- 4. ATTRIBUTION AUTOMATIQUE DES RÔLES
-- Admin -> ADMIN
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM utilisateurs u, roles r WHERE u.email = 'admin@ensam.eu' AND r.nom = 'ADMIN';

-- Ensamiens (Ceux qui ont un CODE APOGÉE NON NULL)
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM utilisateurs u, roles r WHERE u.code_apogee IS NOT NULL AND r.nom = 'ENSAMIEN';

-- Externes (Ceux qui ont CODE APOGÉE NULL, sauf l'admin)
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM utilisateurs u, roles r WHERE u.code_apogee IS NULL AND u.email != 'admin@ensam.eu' AND r.nom = 'EXTERNE';

-- 5. CRÉATION DES CLUBS (Liaison avec les Présidents Ensamiens)
-- Ahmed -> Président AMMC
INSERT INTO clubs (nom, categorie, description, photo, president_id)
SELECT 'Arts & Métiers Mechatronics', 'Robotique', 'Club de robotique et mécatronique de l''ENSAM.', 'https://img.freepik.com/vecteurs-libre/modele-logo-robotique-degrade_23-2149366302.jpg', id
FROM utilisateurs WHERE email = 'ahmed.alami@ensam.eu';

-- Youssef -> Président Enactus
INSERT INTO clubs (nom, categorie, description, photo, president_id)
SELECT 'Enactus ENSAM', 'Social', 'Entrepreneuriat social et solidaire.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_sB_FV-Q9ue_sC3Gv7lYm3ZzE7Xv9y8y8yA&s', id
FROM utilisateurs WHERE email = 'youssef.chraibi@ensam.eu';

-- 6. AJOUT DU RÔLE "PRESIDENT" (En plus du rôle ENSAMIEN)
INSERT INTO users_roles (user_id, role_id)
SELECT c.president_id, r.id FROM clubs c, roles r WHERE r.nom = 'PRESIDENT';