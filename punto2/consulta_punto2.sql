create schema PUNTOLL;

SET search_path TO PUNTOLL;

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    ciudad VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sucursal (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    ciudad VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipoproducto VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS inscripcion (
    idproducto BIGINT,
    idcliente BIGINT,
    PRIMARY KEY (idproducto, idcliente),
    FOREIGN KEY (idproducto) REFERENCES producto(id),
    FOREIGN KEY (idcliente) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS disponibilidad (
    idsucursal BIGINT,
    idproducto BIGINT,
    PRIMARY KEY (idsucursal, idproducto),
    FOREIGN KEY (idsucursal) REFERENCES sucursal(id),
    FOREIGN KEY (idproducto) REFERENCES producto(id)
);

CREATE TABLE IF NOT EXISTS visitan (
    idsucursal BIGINT,
    idcliente BIGINT,
    fechavisita DATE NOT NULL,
    PRIMARY KEY (idsucursal, idcliente),
    FOREIGN KEY (idsucursal) REFERENCES sucursal(id),
    FOREIGN KEY (idcliente) REFERENCES cliente(id)
);


CREATE INDEX IF NOT EXISTS idx_visitan_cliente 
ON visitan(idcliente, idsucursal);

CREATE INDEX IF NOT EXISTS idx_disponibilidad_prod 
ON disponibilidad(idproducto, idsucursal);

CREATE INDEX IF NOT EXISTS idx_inscripcion_cliente 
ON inscripcion(idcliente, idproducto);

INSERT INTO cliente (id, nombre, apellidos, ciudad)
SELECT 1, 'Ismael', 'Trocha', 'Bogotá'
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE id = 1);

INSERT INTO cliente (id, nombre, apellidos, ciudad)
SELECT 2, 'Maria', 'Gomez', 'Medellín'
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE id = 2);

INSERT INTO sucursal (id, nombre, ciudad)
SELECT 10, 'Sucursal Norte', 'Bogotá'
WHERE NOT EXISTS (SELECT 1 FROM sucursal WHERE id = 10);

INSERT INTO producto (id, nombre, tipoproducto)
SELECT 100, 'Tarjeta Credito', 'Financiero'
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE id = 100);

--- CRUCE DE DATOS 

INSERT INTO visitan (idsucursal, idcliente, fechavisita)
SELECT 10, 1, CURRENT_DATE
WHERE NOT EXISTS (
SELECT 1 FROM visitan WHERE idsucursal = 10 AND idcliente = 1
);

INSERT INTO disponibilidad (idsucursal, idproducto)
SELECT 10, 100
WHERE NOT EXISTS (
SELECT 1 FROM disponibilidad WHERE idsucursal = 10 AND idproducto = 100
);

INSERT INTO inscripcion (idproducto, idcliente)
SELECT 100, 1
WHERE NOT EXISTS (
SELECT 1 FROM inscripcion WHERE idproducto = 100 AND idcliente = 1
);

--- CONSULTA FINAL

SELECT DISTINCT c.nombre
FROM cliente c
WHERE EXISTS (
    SELECT 1
    FROM inscripcion i
    JOIN disponibilidad d ON i.idproducto = d.idproducto
    JOIN visitan v 
        ON v.idcliente = c.id 
       AND v.idsucursal = d.idsucursal
    WHERE i.idcliente = c.id
);

