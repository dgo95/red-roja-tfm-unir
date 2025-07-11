-- 01-enforce-x509.sql

/* 1 ▸ Asegura que sólo hay dos raíces:
        - root@localhost (administración local, sin X509)
        - root@% (remoto, con X509 obligatorio)          */
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '${MYSQL_DB_NUCLEO_ROOT_PASSWORD}' REQUIRE X509;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

/* 3 ▸ Refresca los privilegios */
FLUSH PRIVILEGES;