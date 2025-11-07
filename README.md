# ecommerce-venta- API Secundaria

Este proyecto corresponde al **módulo de ventas del proyecto principal "Ecommerce"**, está desarrollado con **Spring Boot 3.x.x** y **Java 21**.  
Se encarga de la gestión de **ventas** y **confirmacion de pago**
Este es un proyecto con fines educativos, está abierto para el uso de todo público.

---

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.x.x**
  - Spring Web (API REST)
  - Spring Data JPA
- **Lombok**
- **Swagger / OpenAPI 3**
- **PostgreSQL**
- **RabbitMQ (mensajería asíncrona)**
- **Docker (para levantar RabbitMQ)**
- **Manejo centralizado de excepciones**

---


## Para levantar el proyecto es necesario que tengas 
-Java 21

-Maven 3.9+

-PostgreSQL en ejecución

-RabbitMQ (Docker)



## Comando para levantar el contenedor de Rabbit en Docker
docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

## DDL para la base de datos 
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion TEXT,
    estado VARCHAR(20)
);


CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    stock INT NOT NULL CHECK (stock >= 0)
);

ALTER TABLE producto
ADD COLUMN estado VARCHAR(20);

CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10, 2) CHECK (total >= 0)
);

ALTER TABLE venta
ADD COLUMN estado VARCHAR(20);


CREATE TABLE detalle_venta (
    id_detalle SERIAL PRIMARY KEY,
    id_venta INT NOT NULL REFERENCES venta(id_venta) ON DELETE CASCADE,
    id_producto INT NOT NULL REFERENCES producto(id_producto),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10, 2) NOT NULL CHECK (precio_unitario >= 0)
);


CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_venta INT NOT NULL REFERENCES venta(id_venta) ON DELETE CASCADE,
    metodo_pago VARCHAR(50) NOT NULL,
    monto NUMERIC(10, 2) NOT NULL CHECK (monto >= 0),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'PENDIENTE'
);
