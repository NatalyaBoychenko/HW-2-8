package org.example;

import org.example.client.ClientService;
import org.flywaydb.core.Flyway;

public class Main {
    public static void main(String[] args) {

        ClientService clientService = new ClientService(Database.getInstance().getConnection());

        initDb();

        clientService.create("Nat");
        System.out.println("clientService.getById(1) = " + clientService.getById(1));
        clientService.setName(2, "John");
        clientService.deleteById(3);
        System.out.println("clientService.listAll() = " + clientService.listAll());
    }


    private static void initDb(){
        Flyway flyway = Flyway
                .configure()
                .dataSource("jdbc:h2:./db5", null, null)
                .load();

        flyway.migrate();
    }

}