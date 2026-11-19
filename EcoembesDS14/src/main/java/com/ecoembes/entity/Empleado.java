package com.ecoembes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Persona del personal de Ecoembes que puede autenticarse en el sistema
@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    private String id;
    private String nombre;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    protected Empleado() { } //Constructor vacío que necesita JPA

    public Empleado(String id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    //Comprueba si la contraseña recibida coincide con la del empleado
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
