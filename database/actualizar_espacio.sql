ALTER TABLE espacio ADD (ubicacion VARCHAR2(100), activo NUMBER(1) DEFAULT 1 NOT NULL);
ALTER TABLE espacio ADD CONSTRAINT ck_espacio_activo CHECK (activo IN (0, 1));
