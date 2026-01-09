-- =================================================================
-- 1. RÔLES
-- SQL Server va attribuer les IDs automatiquement : 1, 2, 3, 4, 5
-- =================================================================
INSERT INTO roles (nom) VALUES ('ADMIN');      -- Sera ID 1
INSERT INTO roles (nom) VALUES ('ENSAMIEN');   -- Sera ID 2
INSERT INTO roles (nom) VALUES ('PRESIDENT');  -- Sera ID 3
INSERT INTO roles (nom) VALUES ('ADHERENT');   -- Sera ID 4
INSERT INTO roles (nom) VALUES ('EXTERNE');    -- Sera ID 5


-- =================================================================
-- 2. LISTE DE RÉFÉRENCE (Table 'ensamien') - LES 10 AJOUTÉS
-- =================================================================
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO123', 'Houmad', 'Mahfoud', 'G-INFO', '4A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO456', 'Alami', 'Sami', 'G-INDUS', '3A');

-- Les 10 Nouveaux
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO101', 'Tazi', 'Ahmed', 'G-INFO', '3A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO102', 'Bennani', 'Sara', 'IA', '4A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO103', 'Idrissi', 'Omar', 'G-MECA', '5A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO104', 'Mansouri', 'Laila', 'G-INDUS', '4A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO105', 'Chaoui', 'Karim', 'G-ELEC', '3A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO106', 'Fassi', 'Yassine', 'G-CIVIL', '4A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO107', 'Naciri', 'Hiba', 'G-INFO', '5A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO108', 'Alaoui', 'Driss', 'G-INDUS', '4A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO109', 'Berrada', 'Salma', 'IA', '3A');
INSERT INTO ensamien (code_apogee, nom, prenom, filiere, niveau) VALUES ('APO110', 'Daoudi', 'Mehdi', 'G-MECA', '5A');


-- =================================================================
-- 3. UTILISATEURS
-- =================================================================

-- Utilisateur 1 : ADMIN PRINCIPAL
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe)
VALUES ('Admin', 'Principal', 'admin@ensam.eu', '$2a$12$DG9qnj4wjLatflaPNNrqjOOK2LToRBlAoJFg0MwJVmsVmrA0kAODC');


-- Utilisateur 2 : DIRECTEUR (Le 2ème Admin demandé)
-- Mot de passe (même que admin) : admin123
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe)
VALUES ('Directeur', 'ENSAM', 'direction@ensam.eu', '$2a$12$DG9qnj4wjLatflaPNNrqjOOK2LToRBlAoJFg0MwJVmsVmrA0kAODC');


-- =================================================================
-- 4. LIAISON ROLES (Table de jointure)
-- Attention : SQL Server commence les IDs à 1
-- =================================================================

-- Lier User 1 (Admin) -> Role 1 (ADMIN)
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);



-- Lier User 3 (Directeur) -> Role 1 (ADMIN)
INSERT INTO users_roles (user_id, role_id) VALUES (3, 1);