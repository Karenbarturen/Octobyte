-- Esquema PostgreSQL para Octobyte (compatible con Hibernate)

CREATE SEQUENCE IF NOT EXISTS bateria_id_bateria_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS electricidad_id_electricidad_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS medicion_id_medicion_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS operativo_id_operativo_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS total_id_total_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS medicion_total_id_medicion_total_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS operativo (
    id_operativo integer NOT NULL DEFAULT nextval('operativo_id_operativo_seq'),
    nombre text,
    activo boolean NOT NULL DEFAULT false,
    CONSTRAINT operativo_pkey PRIMARY KEY (id_operativo)
);

CREATE TABLE IF NOT EXISTS bateria (
    id_bateria integer NOT NULL DEFAULT nextval('bateria_id_bateria_seq'),
    energia_guardada integer NOT NULL,
    nivel_porcentaje integer NOT NULL,
    id_operativo integer NOT NULL,
    CONSTRAINT bateria_pkey PRIMARY KEY (id_bateria),
    CONSTRAINT bateria_operativo_fk FOREIGN KEY (id_operativo) REFERENCES operativo(id_operativo)
);

CREATE TABLE IF NOT EXISTS electricidad (
    id_electricidad integer NOT NULL DEFAULT nextval('electricidad_id_electricidad_seq'),
    voltaje integer NOT NULL,
    corriente integer NOT NULL,
    generacion_electricidad integer NOT NULL,
    consumo_electricidad integer NOT NULL,
    CONSTRAINT electricidad_pkey PRIMARY KEY (id_electricidad)
);

CREATE TABLE IF NOT EXISTS medicion (
    id_medicion integer NOT NULL DEFAULT nextval('medicion_id_medicion_seq'),
    viento integer NOT NULL,
    temperatura integer NOT NULL,
    lumens integer NOT NULL,
    CONSTRAINT medicion_pkey PRIMARY KEY (id_medicion)
);

CREATE TABLE IF NOT EXISTS medicion_total (
    id_medicion_total integer NOT NULL DEFAULT nextval('medicion_total_id_medicion_total_seq'),
    id_bateria integer NOT NULL,
    id_electricidad integer NOT NULL,
    id_medicion integer NOT NULL,
    CONSTRAINT medicion_total_pkey PRIMARY KEY (id_medicion_total),
    CONSTRAINT medicion_total_bateria_fk FOREIGN KEY (id_bateria) REFERENCES bateria(id_bateria),
    CONSTRAINT medicion_total_electricidad_fk FOREIGN KEY (id_electricidad) REFERENCES electricidad(id_electricidad),
    CONSTRAINT medicion_total_medicion_fk FOREIGN KEY (id_medicion) REFERENCES medicion(id_medicion)
);

CREATE TABLE IF NOT EXISTS total (
    id_total integer NOT NULL DEFAULT nextval('total_id_total_seq'),
    fecha timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    num_alerta integer NOT NULL,
    id_medicion_total integer NOT NULL,
    CONSTRAINT total_pkey PRIMARY KEY (id_total),
    CONSTRAINT total_medicion_total_fk FOREIGN KEY (id_medicion_total) REFERENCES medicion_total(id_medicion_total)
);

ALTER SEQUENCE bateria_id_bateria_seq OWNED BY bateria.id_bateria;
ALTER SEQUENCE electricidad_id_electricidad_seq OWNED BY electricidad.id_electricidad;
ALTER SEQUENCE medicion_id_medicion_seq OWNED BY medicion.id_medicion;
ALTER SEQUENCE operativo_id_operativo_seq OWNED BY operativo.id_operativo;
ALTER SEQUENCE medicion_total_id_medicion_total_seq OWNED BY medicion_total.id_medicion_total;
ALTER SEQUENCE total_id_total_seq OWNED BY total.id_total;

-- Modo operativo por defecto
INSERT INTO operativo (nombre, activo)
SELECT 'Central Norte', true
WHERE NOT EXISTS (SELECT 1 FROM operativo WHERE activo = true);
